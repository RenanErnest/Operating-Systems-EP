import java.util.Collections;
import java.util;

ArrayList<BCP> runningProcessTable = new ArrayList<BCP>(); // Store all BCP references
  
PriorityQueue<Object> ready = new PriorityQueue<Object>(); // Create an empty priority queue for ready process
Queue<Object> blocked = new LinkedList<Object>(); // Simple FIFO for blocked process

Object alternateProcess()
{
  ready.poll(); // Return the top priority process
}
