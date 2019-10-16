public class BCP{
  public int PC = 0; // Start from the begginning
  public int processStatus;
  public int priority;
  public int X = 0, Y = 0;
  public int blockedCounter;
  public int textSegmentIndex;
  public int programQuantum = 1; // Initially 1 quantum for every program
  public String programName;
  public int credits;

  public BCP(String name, int priority, int textSegmentIndex)
  {
    this.priority = priority;
    this.credits = priority;
    programName = name;
    this.textSegmentIndex = textSegmentIndex;
  }
}
