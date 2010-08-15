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
			for(Province province : Main.state.getProvinces())
			{
				if(province.isEmpty())
				{
					PlayableCard card = remove();
					// Make sure there was a card to pull from the deck still
					if(card != null)
					{
						province.add(card);
						// Mark that we refilled a province so we don't also put a card on the table
						refilled = true;
					}
				}
			}
		}
		// If the deck is a fate deck or no province got refilled put a card on the table
		if(!isDynasty || !refilled)
		{
			// Do default (put card on table)
			super.doubleClicked();
		}
	}
		
	public BufferedImage getImage()
	{
		// If there is a card in the deck display the appropriate image
		if(!cards.isEmpty())
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
		// Otherwise display nothing
		return null;
	}
}
