class BCP{
    int pc; //program counter
    int state; //process's state(0-blocked;1-ready;2-executing)
    int priority; //process's priority
    /*general registers's content*/
    int X;
    int Y;
    /* */
    String textSegment; //reference to program's text segment
}