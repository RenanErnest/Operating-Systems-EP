import java.util.Collections;
import java.util;

File priorityFile, quantumFile;
File [] processFiles = new File[10];

ArrayList<BCP> runningProcessTable = new ArrayList<BCP>(); // Store all BCP references
LinkedList[] readyList = new LinkedList[]
Queue<Object> ready = new LinkedList<Object>(); // Create an empty priority queue for ready process
Queue<Object> blocked = new LinkedList<Object>(); // Simple FIFO for blocked process
int X, Y, quantum, processName = "";
String output = "";

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
    saida += "E/S iniciada em " + processName;
  }
}

BCP AlternateProcess()
{
  return ready.poll(); // Return the top priority process
}

void Init()
{
  priorityFile = new File("prioridades.txt"); 
  quantumFile = new File("quantum.txt");  
  BufferedReader br = new BufferedReader(new FileReader(priorityFile)); 
  BufferedReader qbr = new BufferedReader(new FileReader(quantumFile)); 
  quantum = Integer.parseInt(qbr.readLine());
  
  for(int i = 1; i < 11; i++) // Read each command block
  {
    String index = (i < 10 ? "0" : "") + i;
    processFiles[i-1] = new File(index + ".txt");  
    BufferedReader pbr = new BufferedReader(new FileReader(processFiles[i-1])); 
    BCP newProcess = new BCP(pbr.readLine(), br.readLine()); // Create BCP with name and priority of the process 
    runningProcessTable.add(newProcess); // The process is running
    ready.add(newProcess); // The new process is ready to execute
  }
}
