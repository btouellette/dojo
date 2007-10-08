/**
 * @(#)TextActionListener.java
 *
 *
 * @author 
 * @version 1.00 2007/10/5
 */
 
package l5r;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;

public class TextActionListener implements ActionListener{

	private JEditorPane output;
	private final static String newline = "\n";
	
    public TextActionListener(JEditorPane chatBox)
    {
    	//Recieves the textfield to which the ActionListener outputs
    	output = chatBox;
    	output.setText("Welcome to L5R Online!");
    }
    
    public void actionPerformed(ActionEvent e)
    {
    	send(((JTextField)e.getSource()).getText());
    }
    
    public void send(String text)
    {
    	//Just updates the chat area locally for now, needs to be updated to send a network command
    	output.setText(output.getText() + newline + text);
    }
}