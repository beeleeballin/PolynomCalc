import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Map;
import java.util.Iterator; 
import java.util.Deque;

public class HelloGUI{
    public static void main(){
        //title
        JFrame window = new JFrame("Polynomial Calculator");
        window.setSize(500, 200);
        window.setLocationRelativeTo(null);
        
        //container for all content
        JPanel content = new JPanel();
        window.setContentPane(content);
        
        //asks for user input
        JLabel question = new JLabel("What to calculate?");
        content.add(question);
        
        //allows for user input
        JTextField inputfield = new JTextField("", 30);
        content.add(inputfield);
        
        //hit to evaluate
        JButton runButton = new JButton("Evaluate");
        content.add(runButton);
        
        //hit to exit
        JButton exitButton = new JButton("Exit");
        content.add(exitButton);
        
        //presents evaluation 
        JLabel res = new JLabel("");
        content.add(res);
        
        //presents equivalent variables 
        JLabel eqv = new JLabel("");
        content.add(eqv);
        
        PolyCalc p = new PolyCalc();
        
        // 'Evaluate' button function
        runButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent a) {
                String resStr = "";
                String eqvStr = "";
                String input = inputfield.getText().replaceAll(" ","");
                try{
                    FSM parser = new FSM();
                    Deque<Token> infix = parser.parsePoly(input, p.storage);
                    if(infix == null) {
                        resStr += ("Did you accidently keyed in an invalid arithmetic expression, ");
                        eqvStr += ("such as an invalid operator or operand?");
                    }
                    
                    PostfixConverter organizer = new PostfixConverter();
                    Deque<Token> postfix = organizer.convert(infix);
                    if(postfix == null) {
                        resStr += ("Did you accidently keyed in a mismatching parenthesis?");
                    }
                    
                    Eval res = new Eval();
                    Polynomial end = res.sol(postfix);
                    if(end == null) {
                        resStr += ("Cannot divide by 0!");
                        eqvStr += ("Or this is a valid arithmetic expression,");
                        eqvStr += ("but sorry our program does not allow remainders when dividing....");
                    }
                    
                    if(parser.getTemp() != '!') {
                        p.storage.put(parser.getTemp(), end); 
                        resStr += ("Variable "+parser.getTemp()+" is saved as: "+end);
                    }else resStr += ("Your result is "+end);
                    
                    Iterator<Map.Entry<Character,Polynomial>> itr = p.storage.entrySet().iterator();
                    int counter = 0;
                    while(itr.hasNext()) 
                    { 
                         Map.Entry<Character,Polynomial> storedValue = itr.next(); 
                         Polynomial diff = end.subtraction(storedValue.getValue());
                         if((diff.isZero())&&(storedValue.getKey()!= parser.getTemp())){
                             if(counter == 0){
                                 eqvStr += ("Which is equal to variable(s) "+storedValue.getKey());
                                 counter++;
                             }else eqvStr += (", "+storedValue.getKey());
                         }
                    } 
                } catch(NullPointerException err){
                    
                }
                res.setText(resStr);
                eqv.setText(eqvStr);
            }
        });
        
        // 'Exit' button function
        exitButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent a) {
                System.exit(0);
            }
        });
        
        window.setVisible(true);
        window.toFront();
    }
}
