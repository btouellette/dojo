package dojo;
// CardHolder.java
// Written by Brian Ouellette
// Abstract class representing any game element which can hold cards in it

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

public abstract class CardHolder
{
	protected BufferedImage image;
	// 0 is top card
	protected ArrayList<PlayableCard> cards;
	protected int location[];

	public CardHolder()
	{
		location = new int[2];
		cards = new ArrayList<PlayableCard>(45);
	}
	
	public CardHolder(int listSize)
	{
		location = new int[2];
		cards = new ArrayList<PlayableCard>(listSize);
	}
	
	public void add(StoredCard card)
	{
		// Put on top
		cards.add(0, new PlayableCard(card));
	}

	public void add(PlayableCard card)
	{
		// Put on top
		cards.add(0, card);
	}

	public PlayableCard remove()
	{
		PlayableCard returnedCard = null;
		if(!cards.isEmpty())
		{
			returnedCard = cards.remove(0);
		}
		return returnedCard;
	}
	
	public PlayableCard remove(StoredCard card)
	{
		PlayableCard returnedCard = null;
		if(cards.remove(card))
		{
			returnedCard = new PlayableCard(card);
		}
		return returnedCard;
	}
	
	public void removeAll()
	{
		cards.clear();
	}

	public void doubleClicked()
	{
		// If we have a card to put on the table do so
		if(!cards.isEmpty())
		{
			PlayableCard card = remove();
			PlayArea.displayedCards.add(card);
			card.setLocationSimple(location[0], location[1] - (PlayArea.cardHeight+8));
		}
	}

	public int[] getLocation()
	{
		return location.clone();
	}

	public void setLocation(int[] location)
	{
		this.location = location.clone();
	}

	public int numCards()
	{
		return cards.size();
	}
	
	public void shuffle()
	{
		Collections.shuffle(cards);
	}
	
	public void setImage(BufferedImage image)
	{
		this.image = image;
	}
}
