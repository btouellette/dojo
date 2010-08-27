package dojo;
// TextActionListener.java
// Written by Brian Ouellette
// This is the ActionListener that controls the chat panel. It formats the text and sends out commands.

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

class TextActionListener implements ActionListener
{
	private JTextPane chatBox;
	// Locks the updating of the chat box so multiple threads can send updates
	private static Lock lock = new ReentrantLock();

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
		update(text, style);
	}

	public static void update(String text, String style)
	{
		// Can't guarantee the Swing component is thread safe and multiple clients may try to update the chatbox at the same time
		lock.lock();
		try
		{
			StyledDocument doc = Main.chatBox.getStyledDocument();
			// Add the string to the end of the chatbox, appropriately styled
			doc.insertString(doc.getLength(), "\n" + text, doc.getStyle(style));
		} catch(BadLocationException e) {
			System.err.println("The following error ocured: " + e);
		} finally {
			lock.unlock();
		}
	}
}
