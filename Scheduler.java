import java.util.Collections;
import java.util;

File priorityFile, quantumFile;
File [] processFiles;

ArrayList<BCP> runningProcessTable = new ArrayList<BCP>(); // Store all BCP references
  
PriorityQueue<Object> ready = new PriorityQueue<Object>(); // Create an empty priority queue for ready process
Queue<Object> blocked = new LinkedList<Object>(); // Simple FIFO for blocked process

public static void main(String [] args)
{
  Init();
}

Object AlternateProcess()
{
  return ready.poll(); // Return the top priority process
}

void Init()
{
  priorityFile = new File("prioridades.txt"); 
  quantumFile = new File("quantum.txt"); 
  
  
  for(int i = 1; i < 11; i++) // Read each process
  {
    String index = (i < 10 ? "0" : "") + i;
    processFiles[i] = new File(index + ".txt");
    
    BCP newProcess = new BCP(index, GetPriority(i)); // Create process BCP
  }
}
