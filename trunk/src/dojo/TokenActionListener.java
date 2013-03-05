package dojo;
// TokenActionListener.java
// Written by Brian Ouellette
// This is the Action and Mouse Listener for the JComboBox that creates tokens
// It takes input, stores previous token names, and generates tokens.

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JTextField;

class TokenActionListener implements ActionListener, MouseListener
{
	// This method is called whenever the user or program changes the selected item.
	// Note: The new item may be the same as the previous item.
	public void actionPerformed(ActionEvent e)
	{
		boolean dupeTest = true;
		@SuppressWarnings("unchecked") // WTB reified generics
		JComboBox<String> comboBox = (JComboBox<String>)e.getSource();

		// Get the text entered
		String text = (String)comboBox.getSelectedItem();

		if ("comboBoxEdited".equals(e.getActionCommand()))
		{
			// String has been entered and the user wants to make a token
			TextActionListener.send(Preferences.userName + " makes a token.", "Action");
			// Update the list of token names
			for(int i = 0; i < comboBox.getItemCount(); i++)
			{
				if(text.equals(comboBox.getItemAt(i)))
				{
					dupeTest = false;
				}
			}
			if(dupeTest)
			{
				comboBox.insertItemAt(text, 0);
			}
			// Make a token and place it on the table
			//TODO: Save the top 3 tokens on exit
			Main.state.addToTable(new PlayableCard(text, true));
			Main.playArea.repaint();
		}
	}

	public void mouseClicked(MouseEvent e)
	{
		// This is implementing a tip style combobox (light gray text until clicked in)
		@SuppressWarnings("unchecked") // WTB reified generics
		JComboBox<String> comboBox = (JComboBox<String>)e.getComponent();
		if(!comboBox.isEditable())
		{
			comboBox.setEnabled(true);
			comboBox.setEditable(true);
			// Bias the text five pixels from the edge of the combo box
			JTextField textField = ((JTextField)comboBox.getEditor().getEditorComponent());
			textField.setBorder(BorderFactory.createCompoundBorder(textField.getBorder(), BorderFactory.createEmptyBorder(0,3,0,0)));
			comboBox.removeAllItems();
			comboBox.requestFocusInWindow();
		}
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{
	}

	public void mouseReleased(MouseEvent e)
	{
	}
}
