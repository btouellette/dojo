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
public class Decklist extends DefaultListModel
{
	private static final long serialVersionUID = 1L;
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
		
		Collections.sort(deck);
		
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
}
