package dojo;
// TextActionListener.java
// Written by Brian Ouellette
// This is the ActionListener that controls the chat panel. It formats the text and sends out commands.

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

class TextActionListener implements ActionListener
{
	private JTextPane chatBox;

	public TextActionListener()
	{
		// Receives the textfield to which the ActionListener outputs
		this.chatBox = Main.chatBox;
		addStyles();
		chatBox.setText("Welcome to Dojo!");
	}

	private void addStyles()
	{
		// Use different colors for different classes of messages
		//TODO: Make customizable
		Style style = chatBox.addStyle("Default", null);

		style = chatBox.addStyle("Action", null);
		StyleConstants.setForeground(style, new Color(51, 153, 51)); // Green

		style = chatBox.addStyle("You", null);
		StyleConstants.setForeground(style, new Color(51, 51, 204)); // Blue

		style = chatBox.addStyle("Opp", null);
		StyleConstants.setForeground(style, new Color(204, 51, 51)); // Red
		
		style = chatBox.addStyle("Error", null);
		StyleConstants.setForeground(style, new Color(255, 0, 0)); // Red
	}

	public void actionPerformed(ActionEvent e)
	{
		JTextField textBox = (JTextField)e.getSource();
		//Send the input text to the display
		send(Preferences.userName + ": " + textBox.getText(), "You");
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