package dojo;
// Discard.java
// Written by Brian Ouellette
// Represents fate and dynasty discards

import java.awt.image.BufferedImage;

class Discard extends CardHolder
{
	public PlayableCard remove()
	{
		// Remove the top card or return null if there isn't one to remove
		PlayableCard returnedCard = null;
		if(!cards.isEmpty())
		{
			returnedCard = cards.remove(0);
			// If the discard is now empty set the image to null, otherwise update it
			if(cards.isEmpty())
			{
				setImage(null);
			}
			else
			{
				// Top card has changed so update image
				setImage(cards.get(0).getImage());
			}
		}
		return returnedCard;
	}

	//TOOD: implement public PlayableCard remove(PlayableCard card) for use in search

	public void add(PlayableCard card)
	{
		super.add(card);
		// Top card has changed so update image
		setImage(card.getImage());
	}
	
	public BufferedImage getImage()
	{
		return image;
	}
}
