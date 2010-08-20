package dojo;

import java.util.Vector;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

class DeckActionListener implements ListSelectionListener
{
	Vector<StoredCard> vec;
	public DeckActionListener(Vector<StoredCard> v)
	{
		vec = v;
	}

	public void valueChanged(ListSelectionEvent e)
	{
		JList list = (JList) e.getSource();
		if(e.getValueIsAdjusting() == false)
		{
			/*
			if(list.getSelectedIndex() < 0)
			{
				list.clearSelection();
			}*/
			if(list.getSelectedValue()==null)
				list.clearSelection();
			else
			{
				for (int i=0;i<vec.size();i++)
     			{
     				if (vec.elementAt(i).getName().equals(list.getSelectedValue().toString().substring(3)))
     				{
     					Deckbuilder.card.setCard(vec.elementAt(i));
     					break;
     				}
     			}
			}
		}
    }
}