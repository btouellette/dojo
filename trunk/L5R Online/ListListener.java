// ListListener.java
// Written by James Spencer
// Part of Dojo

package l5r;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

class ListListener implements ListSelectionListener
{
	public ListListener()
	{
	}

	public void valueChanged(ListSelectionEvent e)
	{
		if(e.getValueIsAdjusting() == false)
		{
			Deckbuilder.card.setCard(Deckbuilder.vect.elementAt(Deckbuilder.list.getSelectedIndex()));
		}
    }
}