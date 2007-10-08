/**
 * @(#)TextActionListener.java
 *
 *
 * @author 
 * @version 1.00 2007/10/5
 */
 
package l5r;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;

public class TextActionListener implements ActionListener{

	private JTextPane output;
	private final static String newline = "\n";
	
    public TextActionListener(JTextPane chatBox)
    {
    	//Recieves the textfield to which the ActionListener outputs
    	output = chatBox;
    	addStyles();
    	output.setText("Welcome to L5R Online!");
    }
    
    public void addStyles()
    {
    	StyledDocument doc = output.getStyledDocument();
    	
    	Style style = output.addStyle("Default", null);
    
	    style = output.addStyle("Action", null);
	    StyleConstants.setForeground(style, new Color(99, 204, 33));
	    
	    style = output.addStyle("YourName", null);
	    StyleConstants.setForeground(style, Color.BLUE);
	    
	    style = output.addStyle("OppName", null);
	    StyleConstants.setForeground(style, Color.RED);
    }
    
    public void actionPerformed(ActionEvent e)
    {
    	send(((JTextField)e.getSource()).getText());
    }
    
    public void send(String text)
    {
    	//Just updates the chat area locally for now, needs to be updated to send a network command
    	try	{
    		StyledDocument doc = output.getStyledDocument();
    		//Default will be replaced with the appropriate style
    		doc.insertString(doc.getLength(), newline + text, doc.getStyle("Default"));
    	} catch(BadLocationException e)	{
    		System.err.println("The following error ocured: " + e);
    	}
    }
}