package dojo;
// Deck.java
// Written by Brian Ouellette
// Used for your dynasty and fate decks

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

//TODO: Add peek functionality
class Deck extends CardHolder
{
	private boolean isDynasty;
		
	public Deck(boolean isDynasty)
	{
		super();
		this.isDynasty = isDynasty;
	}

	public void doubleClicked()
	{
		boolean refilled = false;
		if(isDynasty)
		{
			// Go through all the provinces looking for any that are empty and refilling
			for(int i = 0; i < PlayArea.provinces.size(); i++)
			{
				if(PlayArea.provinces.get(i).isEmpty())
				{
					PlayableCard card = remove();
					if(card != null)
					{
						PlayArea.provinces.get(i).add(card);
						refilled = true;
					}
				}
			}
		}
		// If the deck is a fate deck or no province got refilled put a card on the table
		if(!isDynasty || !refilled)
		{
			PlayableCard card = remove();
			if(card != null)
			{
				PlayArea.displayedCards.add(card);
				//TODO: Set location appropriately
			}
		}
	}
		
	public BufferedImage getImage()
	{
		// If we have't loaded in an image for this yet
		if(image == null && !cards.isEmpty())
		{
			if(isDynasty)
			{
				return StoredImages.dynasty;
			}
			else
			{
				return StoredImages.fate;
			}
		}
		else if(cards.isEmpty())
		{
			return null;
		}
		return image;
	}
}
