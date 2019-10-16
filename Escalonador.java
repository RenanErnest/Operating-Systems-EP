import java.util.Collections;
import java.util;

public class Escalonador{
  File priorityFile, quantumFile;
  File [] processFiles = new File[10];

  LinkedList<BCP> runningProcessTable = new LinkedList<BCP>(); // Store all BCP references
  LinkedList<LinkedList<BCP>> readyList; // List of ready queues
  Queue<BCP> blocked = new LinkedList<BCP>(); // Simple FIFO for blocked process
  int X, Y, quantum, programName, programQuantum, PC, textSegmentIndex, credits;
  String output = "";
  String[] memory = new String[220]; // 22 lines per program
  int maxPriorityQueue = 0;
  int swapTotalTime, totalInstructionPerQuantum, swapCounter = 0;
  
  public static void main(String [] args)
  {
    bool end = false; 
    Init();  
    while(!end)
    {
      Run();
      // Are all the process ended ?
    }
  }

  void Run()
  {
    // Run a process quantum times then return
    String instruction;
    int aux = 0;
    int queue = maxPriorityQueue;

    for(int i = 0; i < maxPriorityQueue; i++)
    {
        try {
          BCP bcp =  readyList.get(queue).removeFirst(); // Pick the first process of the max priority queue
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
          else return; // The loop in the main function will call Run again    
          }
    }
    if(bcp == null) return; // Check
    this.PC = bcp.PC;                                 // Get the program context
    this.programQuantum = bcp.programQuantum;
    this.X = bcp.X;
    this.Y = bcp.Y;
    this.programName = bcp.programName;
    this.textSegmentIndex = bcp.textSegmentIndex;
    this.credits = bcp.credits;
    
    int instExNumb = 0; // Number of executed instructions
    int tempQuantum = programQuantum;
    if(queue == 0) tempQuantum = queue; // 0 queue uses round robin with 1 quantum
    for(int j = 0; j < tempQuantum; j++) // This same program will run quantum times
    {
       instruction = memory[textSegmentIndex + PC];
       PC++;
      
       for(BCP o : blocked) // Decrease the time to complete E/S for all blocked process
       {
         if(o.blockedCounter == 2)
         {
           o.blockedCounter--;
         }
         else 
         {
           blocked.remove(); // Remove the first BCP of the list
           readyList.get(o.credits).add(o); // Add the BCP to the ready queue
         }
       }
      
      if((aux = instruction.indexOf('=')) != -1)
      {
        instExNumb++;
        if(instruction.charAt(0) == 'X') X = Character.getNumericValue(instruction.charAt(2));
        else Y = Character.getNumericValue(instruction.charAt(2));
      }
      else if(instruction.equals("E/S"))
      {
        instExNumb++;
        saida += "E/S iniciada em " + programName + "\n";
        blocked.add(bcp);
        bcp.processStatus = 1;
        bcp.blockedCounter = 2;
      }
      else if(instruction.equals("COM"))
      {
        instExNumb++;
      }
      else if(instruction.equals("SAIDA"))
      {
        instExNumb++;
        saida += programName + " terminado. X=" + X + ". Y=" + Y +".\n";
        totalInstructionPerQuantum += instExNumb;
        swapCounter++;
        return;
      }
    }
      totalInstructionPerQuantum += instExNumb;
      swapCounter++;
      credits -= 2;
      programQuantum++;
    
      bcp.PC = this.PC;                           // Update the process BCP and add it to the ready queue
      bcp.programQuantum = this.programQuantum;
      bcp.X = this.X;
      bcp.Y = this.Y;
      bcp.credits = this.credits;
      readyList.get(credits).add(bcp);
  }

  void Init()
  {
    priorityFile = new File("prioridades.txt"); 
    BufferedReader brGetMax = new BufferedReader(new FileReader(priorityFile)); 
    
    for(int i = 0; i < 10; i++) // Get the maximum priority
    {
       int processPriority = Integer.parseInt(brGetMax.readLine());
       if(processPriority > maxPriorityQueue) maxPriorityQueue = processPriority;
    }
    brGetMax.close();
    
    for(int i = 0; i <= maxPriorityQueue; i++) readyList.add(new LinkedList<BCP>()); // Add a set of lists from 0 to maxPriorityQueue
    quantumFile = new File("quantum.txt");  
    BufferedReader br = new BufferedReader(new FileReader(priorityFile)); 
    BufferedReader qbr = new BufferedReader(new FileReader(quantumFile)); 
    quantum = Integer.parseInt(qbr.readLine());
    
    for(int i = 1; i < 11; i++) // Read each command block
    {
      String index = (i < 10 ? "0" : "") + i;
      processFiles[i-1] = new File(index + ".txt");  
      BufferedReader pbr = new BufferedReader(new FileReader(processFiles[i-1])); 
      int processPriority = Integer.parseInt(br.readLine());
      BCP newProcess = new BCP(pbr.readLine(), processPriority, (i-1) * 22); // Create BCP with name, priority of the process and textSegmentIndex
      readyList.get(processPriority).add(newProcess); // The new process is ready to execute 
      for(int j = 0; j < 22; j++) memory[(i-1) + j] = pbr.readLine(); // Fill the memory with program code or null if the end was reached
      pbr.close();
    }
    
    for(int i = maxPriorityQueue; i >= 0; i--) 
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
