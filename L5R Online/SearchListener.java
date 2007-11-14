// SearchListener.java
// Written by James Spencer
// Part of Dojo

package l5r;

//import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

class SearchListener implements ActionListener, ListSelectionListener
{
	public static Vector<StoredCard> storage;
	ArrayList legal, type, faction;

	public SearchListener()
	{
		//storage = new Vector<StoredCard>();
	}

	public void actionPerformed(ActionEvent e)
	{
		//One of the JTextFields has changed
	    sort();
    }

    public void valueChanged(ListSelectionEvent e)
    {
		//One of the JLists has changed
		if(!e.getValueIsAdjusting())
		{
			legal = new ArrayList<Object>(Arrays.asList(Deckbuilder.legal.getSelectedValues()));
			type = new ArrayList<Object>(Arrays.asList(Deckbuilder.type.getSelectedValues()));
			faction = new ArrayList<Object>(Arrays.asList(Deckbuilder.faction.getSelectedValues()));
			sort();
		}
	}

    public void sort()
    {
		Deckbuilder.vect.clear();          //put all of the data in storage from vect, then add back to vect
										   //IT SHOULD JUST TAKE OUT OF VECT AND ADD TO STORAGE IF NEEDED
		for(int x=0;x<storage.size();x++)
		{

			if(storage.elementAt(x).getType().equals(type))
			{
				Deckbuilder.vect.add(storage.elementAt(x));
				//storage.remove(x);
			}
		}

		refresh();
		System.out.println(storage.size());

		Deckbuilder.alphasort(Deckbuilder.vect);
	}

	public void refresh()
	{
		Deckbuilder.apples.setText("Card Results("+Deckbuilder.vect.size()+"):");
		Deckbuilder.listScroller.revalidate();
		Deckbuilder.listScroller.repaint();
	}
}