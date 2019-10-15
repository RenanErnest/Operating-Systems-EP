int PC;
int processStatus;
int priority;
int X, Y;
int textSegmentIndex;
String programName;
int credits;

public BCP(String name, String priorityS)
{
  priority = (int)(new Integer.parseint(priorityS));
  programName = name;
}
