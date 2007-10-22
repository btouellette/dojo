// TextActionListener.java
// Written by Brian Ouellette
// Part of Dojo

package l5r;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;

class TextActionListener implements ActionListener
{
	private JTextPane chatBox;
	private final static String newline = "\n";

    public TextActionListener()
    {
    	//Recieves the textfield to which the ActionListener outputs
    	this.chatBox = Main.chatBox;
    	addStyles();
    	chatBox.setText("Welcome to L5R Online!");
    }

    public void addStyles()
    {
    	StyledDocument doc = chatBox.getStyledDocument();

    	Style style = chatBox.addStyle("Default", null);

	    style = chatBox.addStyle("Action", null);
	    //Green
	    StyleConstants.setForeground(style, new Color(51, 153, 51));

	    style = chatBox.addStyle("You", null);
	    //Blue
	    StyleConstants.setForeground(style, new Color(51, 51, 204));

	    style = chatBox.addStyle("Opp", null);
	    //Red
	    StyleConstants.setForeground(style, new Color(204, 51, 51));
    }

    public void actionPerformed(ActionEvent e)
    {
		JTextField textBox = (JTextField)e.getSource();
		//Send the inputted text to the display
		send(Main.userName + ": " + textBox.getText(), "You");
    	//Clear the text box
    	textBox.setText("");
    }

    public static void send(String text, String style)
    {
    	//Just updates the chat area locally for now, needs to be updated to send a network command
    	try	{
    		StyledDocument doc = Main.chatBox.getStyledDocument();
			doc.insertString(doc.getLength(), newline + text, doc.getStyle(style));

    	} catch(BadLocationException e)	{
    		System.err.println("The following error ocured: " + e);
    	}
    }
}