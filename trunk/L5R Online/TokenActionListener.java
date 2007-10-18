// TokenActionListener.java
// Written by Brian Ouellette
// Part of Dojo

package l5r;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;

class TokenActionListener implements ActionListener, MouseListener
{
    public TokenActionListener()
    {
    }

    public void actionPerformed(ActionEvent e)
    {
		System.out.println("Action");
    }

    public void mouseClicked(MouseEvent e)
	{
		//This is implementing a tip style combobox (light gray text until clicked in)
		JComboBox comboBox = (JComboBox)e.getComponent();
		if(comboBox.isEditable() == false)
		{
			comboBox.setEnabled(true);
			comboBox.setEditable(true);
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