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

	private JTextPane chatBox;
	private final static String newline = "\n";

    public TextActionListener(JTextPane chatBox)
    {
    	//Recieves the textfield to which the ActionListener outputs
    	this.chatBox = chatBox;
    	addStyles();
    	chatBox.setText("Welcome to L5R Online!");
    }

    public void addStyles()
    {
    	StyledDocument doc = chatBox.getStyledDocument();

    	Style style = chatBox.addStyle("Default", null);

	    style = chatBox.addStyle("Action", null);
	    StyleConstants.setForeground(style, new Color(99, 204, 33));

	    style = chatBox.addStyle("YourName", null);
	    StyleConstants.setForeground(style, Color.BLUE);

	    style = chatBox.addStyle("OppName", null);
	    StyleConstants.setForeground(style, Color.RED);
    }

    public void actionPerformed(ActionEvent e)
    {
		JTextField textBox = (JTextField)e.getSource();
		//Send the inputted text to the display
    	send(textBox.getText());
    	//Clear the text box
    	textBox.setText("");
    }

    public void send(String text)
    {
    	//Just updates the chat area locally for now, needs to be updated to send a network command
    	try	{
    		StyledDocument doc = chatBox.getStyledDocument();
    		//Default will be replaced with the appropriate style
    		doc.insertString(doc.getLength(), newline + text, doc.getStyle("Default"));

    	} catch(BadLocationException e)	{
    		System.err.println("The following error ocured: " + e);
    	}
    }
}