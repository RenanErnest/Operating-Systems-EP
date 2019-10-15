public class BCP{
  public int PC;
  public int processStatus;
  public int priority;
  public int X, Y;
  public int textSegmentIndex;
  public String programName;

  public BCP(String name, int priority, int textSegmentIndex)
  {
    this.priority = priority;
    programName = name;
    this.textSegmentIndex = textSegmentIndex;
  }
}
