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
