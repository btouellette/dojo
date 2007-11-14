// SearchListener.java
// Written by James Spencer
// Part of Dojo

package l5r;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.Vector;

class SearchListener implements ActionListener
{

	public static Vector<StoredCard> storage;
	String type,clan,legal;
	int max, y;
	public SearchListener()
	{
		storage = new Vector<StoredCard>();
		type="";
		clan=" ";
		legal="samurai";
		storage=new Vector<StoredCard>();
		for (int x=0;x<Deckbuilder.vect.size();x++)
			storage.add(Deckbuilder.vect.elementAt(x));
	}
	public void actionPerformed(ActionEvent e)
	{
	        JComboBox cb = (JComboBox)e.getSource();
	        //String item = (String)cb.getSelectedItem();


			for (int x = 0; x < 100; x++)
			{
				//assign Legal menu option
				if (x<Deckbuilder.legalChoices.length &&
				   ((String)cb.getSelectedItem()).equals(Deckbuilder.legalChoices[x]))
				        legal=(String)cb.getSelectedItem();

				//assign Card Type menu option
				else if (x<Deckbuilder.types.length&&((String)cb.getSelectedItem()).equals(Deckbuilder.types[x]))
				{
					type=(String)cb.getSelectedItem();
					if (type.equals("Personality"))
						type="personalities";
				}

				//assign Faction menu option
				else if (x<Deckbuilder.clans.length&&((String)cb.getSelectedItem()).equals(Deckbuilder.clans[x]))
				{
					clan=(String)cb.getSelectedItem();
				}






			}
			Display();
    }

    public void Display()
    {
		//reset the vector
		Deckbuilder.vect.clear();

		for (int x=0;x<storage.size();x++)
			Deckbuilder.vect.add(storage.elementAt(x));

		//reset variables
		max=Deckbuilder.vect.size();
		y=0;

		do
		{
			if(!Deckbuilder.vect.elementAt(y).getLegal().contains(legal.toLowerCase()))
				Deckbuilder.vect.remove(y);
			else if(!Deckbuilder.vect.elementAt(y).getType().startsWith(type.toLowerCase()))
				Deckbuilder.vect.remove(y);
			else if ((Deckbuilder.vect.elementAt(y).getText()==null))
				y++;
			else if(!(clan.equals(" ")) && !(Deckbuilder.vect.elementAt(y).getText().indexOf(clan)>0))
				Deckbuilder.vect.remove(y);
			else y++;

			max=Deckbuilder.vect.size();

		}
		while(y<max);

		refresh();
	}

	public void refresh()
	{
		Deckbuilder.alphasort(Deckbuilder.vect);
		Deckbuilder.apples.setText("Card Results("+Deckbuilder.vect.size()+"):");
		Deckbuilder.list.setListData((Vector<StoredCard>)Deckbuilder.vect.clone());
	}

}