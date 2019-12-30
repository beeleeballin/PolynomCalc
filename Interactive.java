import java.util.Map;
import java.util.Scanner;

/**
 * A class that ask for user inputs to evaluate
 */
public class Interactive
{
    
    /** Constructor */
    public Interactive(){
    }
    
    /** 
     * Takes in user inputs on what to calculate and whether to continue
     * @param memory The memory of all the stored Polynomials
     */
    public void ask(Map<Character,Polynomial> memory){
        Scanner s = new Scanner(System.in);
        while(true){
            System.out.println("What's your expression?");
            String eq = s.nextLine();
            
            //remove all white spaces from the input
            String cleanEq= eq.replaceAll(" ","");
            
            //run the input into our 3 evaluations
            Execute e = new Execute(cleanEq, memory);
            e.run();
            
            System.out.println("Another one?");
            String another = s.nextLine();
            another = another.toLowerCase();
            
            if((!another.equals("yes"))&&(!another.equals("sure"))){
                System.out.println("Thanks for using the Interactive mode of the calculator!");
                System.out.print("All of your variables are still stored");
                System.out.println("and you may access them later or in the File mode!");
                break;
            }
        }
    }
    
    /** Static main to test this class  */
    public static void main(String[] args){
        PolyCalc p = new PolyCalc();
        Interactive i = new Interactive();
        i.ask(p.storage);
    }
}
