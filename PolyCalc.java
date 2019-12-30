import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;

/**
 * Welcome to PolyCalc. This calculator allows you to input arithmetic expressions or equations and it will do the
 * math for you, either through direct input or text file imports. Your variables will be stored until you quit 
 * the program, so you could use and update the variables anywhere.
 *
 * @author Jo Hsuan 'Brian' Lee
 * @version 12/7
 */
public class PolyCalc
{
    /**
     * Accesses the stored polynomials with an associated variable (char).
     */
    Map<Character,Polynomial> storage = new HashMap<Character,Polynomial>();
    
    /** Constructor */
    public PolyCalc(){
        
    }
    
    /**
     * Asks user to select an input method, either 'Interactive', which is through direct input,
     * or 'File', which is through text files. The program exits if the user inputs 'Quit', and all memory
     * associated with the variables will be deleted.
     */
    public static void main(String[] args)
    {
        PolyCalc p = new PolyCalc();
        
        Scanner s = new Scanner(System.in);
        while(true){
            System.out.println("How do you want to do your math?");
            System.out.println("(type in 'interactive' or 'file' or 'quit')");
            String mode = s.nextLine();
            
            //convert mode to all lower case for easier operation
            mode = mode.toLowerCase();
            
            //call interactive object, file object or quit. an invalid input 
            if(mode.equals("interactive")){
                Interactive i = new Interactive();
                i.ask(p.storage);
            }else if(mode.equals("file")){ 
                File f = new File();
                f.read(p.storage);
            }else if(mode.equals("quit")) break;//quit program
            else System.out.println("That was an invalid input. Try again!");
        }
        System.out.println("Deleted all your variables. No one would ever know. Peace out.");
    }
}
