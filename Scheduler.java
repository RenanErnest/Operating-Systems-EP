import java.util.Collections;
import java.util;

File priorityFile, quantumFile;
File [] processFiles = new File[10];

ArrayList<BCP> runningProcessTable = new ArrayList<BCP>(); // Store all BCP references
LinkedList<LinkedList<BCP>> readyList; // List of ready queues
Queue<Object> blocked = new LinkedList<Object>(); // Simple FIFO for blocked process
int X, Y, quantum, programName = "";
String output = "";
int maxPriorityQueue = 0;

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
  // Get instruction  
  if((aux = instruction.indexOf('=')) != -1)
  {
    if(instruction.charAt(0) == 'X') X = Character.getNumericValue(instruction.charAt(2));
    else Y = Character.getNumericValue(instruction.charAt(2));
  }
  else if(instruction.equals("E/S"))
  {
    saida += "E/S iniciada em " + programName + "\n";
  }
}

BCP AlternateProcess()
{
  return ready.poll(); // Return the top priority process
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
  for(int i = 0; i <= maxPriorityQueue; i++) // Add a new list from max priority (0 counting)
  {
    readyList.add(new LinkedList<BCP>());
  }
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
    BCP newProcess = new BCP(pbr.readLine(), processPriority); // Create BCP with name and priority of the process 
    readyList.get(processPriority).add(newProcess); // The new process is ready to execute
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
