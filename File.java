import java.util.Map;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class File
{
    /** The string that stores the input for the file's PATH */
    private String fpath; 
    // PATH "/Users/beelee/Desktop/Fall 2019/CMPU 102/HW3/102f19-hw3-testfile.txt" is the provided testfile
    // PATH "/Users/beelee/Desktop/Fall 2019/CMPU 102/HW3/testfile.txt" is my own test file
    
    /** Constructor */
    public File(){
        
    }
    
    /** 
     * Takes in file inputs on what to calculate and whether to continue
     * @param memory The memory of all the stored Polynomials
     */
    // passes in the existing memory stored in PolyCalc class
    public void read(Map<Character,Polynomial> memory){
        Scanner s = new Scanner(System.in);
        while(true){
            // reads all file txt files with valid PATH line by line until every line is evaluated
            System.out.println("What's the file path?");
            String fpath = s.nextLine();
            
            try {
                BufferedReader r = new BufferedReader(new FileReader(fpath));
                String line = r.readLine();
                do{
                    //show the line of expression the calculator is about to evaluate
                    System.out.println(line);
                
                    //remove all white spaces from the input
                    String cleanLine= line.replaceAll(" ","");
                
                    //run the input into our 3 evaluations to see if it is valid
                    Execute e = new Execute(cleanLine, memory);
                    e.run();
                    
                    //read new line
                    line = r.readLine();
                    
                }while(line != null);
            
                r.close();
            }catch (IOException e) { 
                System.err.println(e);
            }
            
            //reads a new file if there is one
            System.out.println("Another file?");
            String another = s.nextLine();
            another = another.toLowerCase();
            
            if((!another.equals("yes"))&&(!another.equals("sure"))){
                System.out.println("Thanks for using the File mode of the calculator!");
                System.out.print("All of your variables are still stored");
                System.out.println("and you may access them later or in the Interactive mode!");
                break;
            }
        }
    }
    
    /** Static main to test this class */
    public static void main(String[] args){
        PolyCalc p = new PolyCalc();
        File f = new File();
        f.read(p.storage);
    }
}
