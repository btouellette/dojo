package dojo;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.DefaultListModel;

/**
 * TODO Put here a description of what this class does.
 *
 * @author Solus.
 *         Created Aug 28, 2010.
 */
public class Decklist extends DefaultListModel implements Comparable
{
	ArrayList<StoredCard> deck;
	
	public Decklist()
	{
		super();
		deck = new ArrayList<StoredCard>();
		setModel();
	}
	public void setModel()
	{
		clear();
		
		int x = 0, i = 1;
		//Collections.sort(deck);
		
		StoredCard currentCard;
		
		for (int z = 0; z < size(); z++)
			for (int y = 0; y < size(); y++)
				if(deck.get(z).getName().toString().compareToIgnoreCase(deck.get(y).getName().toString())<0)
				{
					currentCard = deck.get(y);    //temp = y
					deck.set(y,deck.get(z));   	   //y = z
					deck.set(z,currentCard);    //z = temp
				}
		do
		{
			if(deck.size() != 0)
			{
				while(x + 1 < deck.size())
				{
					if (deck.get(x).getName().equals(deck.get(x + 1).getName()))
					{
						i++;
						x++;
					}
					else
						break;
				}

				addElement(i + "x " + deck.get(x).getName());
				i = 1;
			}
			x++;
		}while(x < deck.size());
	}
	public int size()
	{
		return deck.size();
	}
	public void add(StoredCard card)
	{
		deck.add(card);
	}
	
	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub.
		return 0;
	}
}
