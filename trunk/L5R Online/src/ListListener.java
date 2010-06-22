// ListListener.java
// Written by James Spencer
// Part of Dojo

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
			if(Deckbuilder.list.getSelectedIndex() < 0)
			{
				Deckbuilder.list.setSelectedIndex(0);
			}
			//TODO: Handle case with no selected cards properly
			Deckbuilder.card.setCard(Deckbuilder.vect.elementAt(Deckbuilder.list.getSelectedIndex()));
		}
    }
}