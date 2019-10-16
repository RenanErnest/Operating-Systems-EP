import java.util.Collections;
import java.util.*;
import java.io.*;

public class Escalonador{
  static File priorityFile, quantumFile;
  static File [] processFiles = new File[10];

  static LinkedList<BCP> runningProcessTable = new LinkedList<BCP>(); // Store all BCP references
  static LinkedList<LinkedList<BCP>> readyList = new LinkedList<LinkedList<BCP>>(); // List of ready queues
  static LinkedList<BCP> blocked = new LinkedList<BCP>(); // Simple FIFO for blocked process
  static int X, Y, quantum, programQuantum, PC, textSegmentIndex, credits;
  static String programName;
  static String output = "";
  static String[] memory = new String[210]; // 22 lines per program
  static int maxPriorityQueue = 0;
  static int totalInstructionPerQuantum, swapCounter = 0;
  static boolean end = false;

  public static void main(String [] args)
  {
    try{
      Init();
    } catch(Exception e) { e.printStackTrace(); }  
    while(!end)
    {
      Run();
    }
    float mediaTrocas = swapCounter/10;
    float mediaQuantum = totalInstructionPerQuantum/swapCounter;
    output += "MEDIA DE TROCAS: " + mediaTrocas + "\n";
    output += "MEDIA DE INSTRUCOES: " + mediaQuantum + "\n";
    
    try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("log" + quantum + ".txt"), "utf-8"))) {
      writer.write(output);
    }
    catch(Exception e) { System.out.println("Error: it cannot has access to write the answer file!"); }
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
          } catch (NoSuchElementException e) {
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
    for(int j = 0; j < tempQuantum; j++) // This same program will run quantum times
    {
       instruction = memory[textSegmentIndex + PC];
       PC++;
      
      if(instruction == null) break;
      if((aux = instruction.indexOf('=')) != -1)
      {
        instExNumb++;
        if(instruction.charAt(0) == 'X') X = Character.getNumericValue(instruction.charAt(2));
        else Y = Character.getNumericValue(instruction.charAt(2));
      }
      else if(instruction.equals("E/S"))
      {
        instExNumb++;
        output += "E/S iniciada em " + programName + "\n";
        blocked.add(bcp);
        bcp.processStatus = 2; // Blocked
        bcp.blockedCounter = 2;
        break;
      }
      else if(instruction.equals("COM"))
      {
        instExNumb++;
      }
      else if(instruction.equals("SAIDA"))
      {
        instExNumb++;
        output += programName + " terminado. X=" + X + ". Y=" + Y +".\n";
        totalInstructionPerQuantum += instExNumb;
        swapCounter++;
        runningProcessTable.remove(bcp); // This program isn't running anymore
        return;
      }
    }
      if(bcp.processStatus != 2) 
      {
        bcp.processStatus = 0; // Ready except by E/S
        readyList.get(credits).addFirst(bcp); // The process had most priority before
      }
      output += "Interrompendo " + programName + " após " + instExNumb + " instruções\n";
    
      totalInstructionPerQuantum += instExNumb;
      swapCounter++;
      credits -= 2;
      if (credits < 0) credits = 0;
      if (programQuantum < quantum) programQuantum++; // Limit is quantum
    
      bcp.PC = PC;                           // Update the process BCP and add it to the ready queue
      bcp.programQuantum = programQuantum;
      bcp.X = X;
      bcp.Y = Y;
      bcp.credits = credits;
    
      BlockTimeCounter();
  }

  static void BlockTimeCounter() 
  {
    for(BCP o : blocked) // Decrease the time to complete E/S for all blocked process
       {
         if(o.blockedCounter == 2)
         {
           o.blockedCounter--;
         }
         else 
         {
           readyList.get(o.credits).add(o); // Add the BCP to the ready queue
           o.processStatus = 0; // Ready
           blocked.remove(o); 
         }
       }
  }
  static void Init() throws Exception
  {
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
      BCP newProcess = new BCP(pbr.readLine(), processPriority, (i-1) * 21); // Create BCP with name, priority of the process and textSegmentIndex
      readyList.get(processPriority).add(newProcess); // The new process is ready to execute 
      for(int j = 0; j < 21; j++) memory[(i-1) * 21 + j] = pbr.readLine(); // Fill the memory with program code or null if the end was reached
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
