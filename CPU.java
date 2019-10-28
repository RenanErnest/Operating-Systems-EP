import java.util.Collections;
import java.util.*;
import java.io.*;

class CPU{
    public static void main(String [] args)
    {   
        for(int quantum = 1; quantum < 21; quantum++) {
            String output = Escalonador.Execution(quantum);
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("log" + quantum + ".txt"), "UTF-8"))) {
                writer.write(output);
            }
            catch(Exception e) { System.out.println("Error: it cannot has access to write the answer file!"); }
        }
    }
}