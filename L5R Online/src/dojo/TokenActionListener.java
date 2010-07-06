package dojo;
// TokenActionListener.java
// Written by Brian Ouellette
// Part of Dojo
// This is the Action and Mouse Listener for the JComboBox that creates tokens
// It takes input, stores previous token names, and generates tokens.

import java.awt.event.*;
import javax.swing.*;

class TokenActionListener implements ActionListener, MouseListener
{
	// This method is called whenever the user or program changes the selected item.
	// Note: The new item may be the same as the previous item.
	public void actionPerformed(ActionEvent e)
	{
		boolean dupeTest = true;
		JComboBox comboBox = (JComboBox)e.getSource();

		// Get the text entered
		Object text = comboBox.getSelectedItem();

		if ("comboBoxEdited".equals(e.getActionCommand()))
		{
			// String has been entered and the user wants to make a token
			TextActionListener.send(Main.userName + " makes a token.", "Action");
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
		}
	}

	public void mouseClicked(MouseEvent e)
	{
		//This is implementing a tip style combobox (light gray text until clicked in)
		JComboBox comboBox = (JComboBox)e.getComponent();
		if(comboBox.isEditable() == false)
		{
			comboBox.setEnabled(true);
			comboBox.setEditable(true);
			//Bias the text five pixels from the edge of the combo box
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
