// TextActionListener.java
// Written by Brian Ouellette
// Part of Dojo
// This is the ActionListener that controls the chat panel. It formats the text and sends out commands.

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;

class TextActionListener implements ActionListener
{
	private JTextPane chatBox;

	public TextActionListener()
	{
		//Recieves the textfield to which the ActionListener outputs
		this.chatBox = Main.chatBox;
		addStyles();
		chatBox.setText("Welcome to Dojo!");
	}

	private void addStyles()
	{
		//TODO: Make customizable
		Style style = chatBox.addStyle("Default", null);

		style = chatBox.addStyle("Action", null);
		StyleConstants.setForeground(style, new Color(51, 153, 51)); // Green

		style = chatBox.addStyle("You", null);
		StyleConstants.setForeground(style, new Color(51, 51, 204)); // Blue

		style = chatBox.addStyle("Opp", null);
		StyleConstants.setForeground(style, new Color(204, 51, 51)); // Red
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
		//TODO: Just updates the chat area locally for now, needs to be updated to send a network command
		try
		{
			StyledDocument doc = Main.chatBox.getStyledDocument();
			doc.insertString(doc.getLength(), "\n" + text, doc.getStyle(style));
		}
		catch(BadLocationException e)
		{
			System.err.println("The following error ocured: " + e);
		}
	}
}
