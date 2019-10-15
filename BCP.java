public int PC;
public int processStatus;
public int priority;
public int X, Y;
public int textSegmentIndex;
public String programName;

public BCP(String name, int priority)
{
  this.priority = priority;
  programName = name;
}
