import java.util.Collections;
import java.util.*;
import java.io.*;

public class Escalonador{
  static File priorityFile, quantumFile;
  static File [] processFiles = new File[10];

  /*Tabela de processos*/
  static LinkedList<BCP> runningProcessTable = new LinkedList<BCP>(); // Store all BCP references
  
  /*Lista de processos prontos*/
  static LinkedList<LinkedList<BCP>> readyList = new LinkedList<LinkedList<BCP>>(); // List of ready queues
  
  /*Lista de processos bloqueados*/
  static LinkedList<BCP> blocked = new LinkedList<BCP>(); // Simple FIFO for blocked process
  
  static int X, Y, programQuantum, PC, textSegmentIndex, credits;
  static String programName;
  static String output = "";
  static int programInstructionNum = 42;
  static String[] memory = new String[programInstructionNum*10]; // 42 lines per program
  static int maxPriorityQueue = 0;
  static int totalInstructionExecuted, totalQuantumUsed, swapCounter = 0;
  static int quantum;
  static boolean end = false;

  public static void main(String[] args) {
    String output = Escalonador.Execution();
    try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("log" + quantum + ".txt"), "UTF-8"))) {
        writer.write(output);
    }
    catch(Exception e) { System.out.println("Error: it cannot has access to write the answer file!"); }
  }

  public static String Execution()
  {
    try{
      Init();
    } catch(Exception e) { e.printStackTrace(); }  
    while(!end)
    {
      Run();
    }
    double mediaTrocas = (double)swapCounter/10.0;
    double mediaQuantum = (double)totalInstructionExecuted/(double)totalQuantumUsed;
    output += "MEDIA DE TROCAS: " + mediaTrocas + "\n";
    output += "MEDIA DE INSTRUCOES: " + mediaQuantum + "\n";
    output += "QUANTUM: " + quantum + "\n";
    
    return output;
  }

  static void Run()
  {
    // Run a process quantum times then return
    String instruction;
    int aux = 0;
    int queue = maxPriorityQueue;
    BCP bcp = null;

    for(int i = 0; i < maxPriorityQueue + 1; i++)
    {
        try {
          bcp =  readyList.get(queue).removeFirst(); // Pick the first process of the max priority queue
          if(bcp != null) break;
        }
        catch (NoSuchElementException e) {
          if(queue > 0) // No process in this queue. Note that we cannot decrease max priority because some high priority program can be blocked
          {
            queue--;
            continue;
          } 
          else if(runningProcessTable.size() == 0) // There's no process running
          {
            end = true;
            return;
          }
          else if(queue == 0) // Are all the running process in the 0 queue ?
          {
            boolean allZero = true;
            for(BCP o : runningProcessTable)
            {
              if(o.credits > 0) allZero = false;
            }
            if(allZero)
            {
              readyList.get(0).clear();
              for(BCP o : runningProcessTable)
              {
                o.credits = o.priority;
                readyList.get(o.credits).add(o);
              }
              return;
            }
          }
          else BlockTimeCounter(); // There's only blocked processes   
          }
    }
    if(bcp == null) return; // Check
    PC = bcp.PC;                                 // Get the program context
    programQuantum = bcp.programQuantum;
    X = bcp.X;
    Y = bcp.Y;
    programName = bcp.programName;
    textSegmentIndex = bcp.textSegmentIndex;
    credits = bcp.credits;
    
    bcp.processStatus = 1; // Running 
    output += "Executando " + programName + "\n";
    int instExNumb = 0; // Number of executed instructions
    int tempQuantum = programQuantum;
    if(queue == 0) tempQuantum = 1; // 0 queue uses round robin with 1 quantum
    for(int j = 0; j < tempQuantum * quantum; j++) // This same program will run quantum times
    {
      instruction = memory[textSegmentIndex + PC];
      PC++;
      instExNumb++;
      
      if(instruction == null) break;
      if((aux = instruction.indexOf('=')) != -1)
      {
        if(instruction.charAt(0) == 'X') X = Integer.parseInt(instruction.substring(2));
        else Y = Integer.parseInt(instruction.substring(2));
      }
      else if(instruction.equals("E/S"))
      {
        output += "E/S iniciada em " + programName + "\n";
        blocked.add(bcp);
        bcp.processStatus = 2; // Blocked
        bcp.blockedCounter = 2;
        break;
      }
      else if(instruction.equals("COM"))
      {

      }
      else if(instruction.equals("SAIDA"))
      {
        output += programName + " terminado. X=" + X + ". Y=" + Y +".\n";
        totalInstructionExecuted += instExNumb;
        swapCounter++;
        runningProcessTable.remove(bcp); // This program isn't running anymore
        totalQuantumUsed += Math.ceil((double)instExNumb/(double)quantum);
        return;
      }
    }
    if(bcp.processStatus != 2) 
    {
      bcp.processStatus = 0; // Ready except by E/S
      readyList.get(credits).addFirst(bcp); // The process had more priority before
    }
    output += "Interrompendo " + programName + " após " + instExNumb + " instruções\n";
  
    totalInstructionExecuted += instExNumb;
    //System.out.println(Math.ceil(instExNumb/quantum))
    totalQuantumUsed += Math.ceil((double)instExNumb/(double)quantum);
    swapCounter++;
    credits -= 2;
    if (credits < 0) credits = 0;
    programQuantum++; // Limit is quantum
  
    bcp.PC = PC;                           // Update the process BCP and add it to the ready queue
    bcp.programQuantum = programQuantum;
    bcp.X = X;
    bcp.Y = Y;
    bcp.credits = credits;
    BlockTimeCounter();
  }

  static void BlockTimeCounter() 
  {
    for(Iterator<BCP> i = blocked.iterator(); i.hasNext();) // Decrease the time to complete E/S for all blocked process
       {
         BCP o = i.next();
         if(o.blockedCounter == 2)
         {
           o.blockedCounter--;
         }
         else 
         {
           readyList.get(o.credits).add(o); // Add the BCP to the ready queue
           o.processStatus = 0; // Ready
           i.remove(); 
         }
       }
  }
  static void Init() throws Exception
  {
    //reset variables
    output = "";
    processFiles = new File[10];
    runningProcessTable = new LinkedList<BCP>(); // Store all BCP references
    readyList = new LinkedList<LinkedList<BCP>>(); // List of ready queues
    blocked = new LinkedList<BCP>(); // Simple FIFO for blocked process
    X = Y = programQuantum = PC = textSegmentIndex = credits = 0;
    programName = "";
    memory = new String[programInstructionNum*10]; // 42 lines per program
    maxPriorityQueue = 0;
    totalInstructionExecuted = totalQuantumUsed = swapCounter = 0;
    end = false;

    priorityFile = new File("processos/prioridades.txt"); 
    BufferedReader brGetMax = null;
    try{
      brGetMax = new BufferedReader(new FileReader(priorityFile));
    } catch(Exception e) { System.out.println("Error: input files could not be open"); e.printStackTrace(); }

    for(int i = 0; i < 10; i++) // Get the maximum priority
    {
       int processPriority = Integer.parseInt(brGetMax.readLine());
       if(processPriority > maxPriorityQueue) maxPriorityQueue = processPriority;
    }
    if(brGetMax != null) brGetMax.close();
    
    for(int i = 0; i <= maxPriorityQueue; i++) readyList.add(new LinkedList<BCP>()); // Add a set of lists from 0 to maxPriorityQueue
    quantumFile = new File("processos/quantum.txt");  
    BufferedReader br = new BufferedReader(new FileReader(priorityFile)); 
    BufferedReader qbr = new BufferedReader(new FileReader(quantumFile)); 
    quantum = Integer.parseInt(qbr.readLine());
    
    for(int i = 1; i < 11; i++) // Read each command block
    {
      String index = (i < 10 ? "0" : "") + i; //adjusting the number format from for example 1 to 01
      processFiles[i-1] = new File("processos/" + index + ".txt");  
      BufferedReader pbr = new BufferedReader(new FileReader(processFiles[i-1])); 
      int processPriority = Integer.parseInt(br.readLine());
      BCP newProcess = new BCP(pbr.readLine(), processPriority, (i-1) * programInstructionNum); // Create BCP with name, priority of the process and textSegmentIndex
      readyList.get(processPriority).add(newProcess); // The new process is ready to execute
      String instruction = "";
      for(int j = 0; j < programInstructionNum; j++) if((instruction = pbr.readLine()) != null) memory[(i-1) * programInstructionNum + j] = instruction.toUpperCase(); // Fill the memory with program code (adjusted to uppercase) or null if the end was reached
      pbr.close();
    }
    
    for(int i = maxPriorityQueue; i >= 0; i--) //adding the process from the max priority queue into the running process table, that means the next processes that will run
    {
      LinkedList<BCP> list = readyList.get(i);
      for(BCP o : list) 
      {
          runningProcessTable.add(o); // The process can run
          output += "Carregando " + o.programName + "\n";
      }
    }
  }
}
