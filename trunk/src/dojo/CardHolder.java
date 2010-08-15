package dojo;
// CardHolder.java
// Written by Brian Ouellette
// Abstract class representing any game element which can hold cards in it

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

public abstract class CardHolder
{
	// Image to display on table
	protected BufferedImage image;
	// 0 is top card
	protected ArrayList<PlayableCard> cards;
	// Location inside the PlayArea
	protected int location[];

	public CardHolder()
	{
		location = new int[2];
		// If no size is stated use the biggest and assume deck-sized holder
		cards = new ArrayList<PlayableCard>(45);
	}
	
	public CardHolder(int listSize)
	{
		location = new int[2];
		// If size is stated use less memory
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
		// Remove and return the top card if there is one
		// Return null if there isn't
		PlayableCard returnedCard = null;
		if(!cards.isEmpty())
		{
			returnedCard = cards.remove(0);
		}
		return returnedCard;
	}
	
	public PlayableCard remove(StoredCard card)
	{
		// Remove the specific card if it is present and return a PlayableCard to be put on the table
		// Return null if it isn't
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
			// No need to check for null here since we know there was a card to retrieve
			// Put the removed card on the table and set its location to above the holder it used to be in
			System.out.println(location[0]);
			card.setLocationSimple(location[0], location[1] - (Main.playArea.getCardHeight()+8));
			Main.state.addDisplayedCard(card);
		}
	}

	public int[] getLocation()
	{
		// Return clone of location to prevent change by reference
		return location.clone();
	}

	public void setLocation(int[] location)
	{
		// Clone in the array to prevent change by reference
		this.location = location.clone();
	}

	public int numCards()
	{
		// Return the number of cards present in the CardHolder
		return cards.size();
	}
	
	public void shuffle()
	{
		// Randomly distributes the cards in the list
		Collections.shuffle(cards);
	}
	
	public void setImage(BufferedImage image)
	{ 
		this.image = image;
	}
}
