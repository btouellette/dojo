package dojo;
// Discard.java
// Written by Brian Ouellette
// Represents fate and dynasty discards

import java.awt.image.BufferedImage;

class Discard extends CardHolder
{
	public PlayableCard remove()
	{
		PlayableCard returnedCard = null;
		if(cards.size() > 1)
		{
			returnedCard = cards.remove(0);
			// Top card has changed so update image
			setImage(cards.get(0).getImage());
		}
		else
		{
			setImage(null);
		}
		return returnedCard;
	}

	//TODO: Move this into CardHolder once there is a better way to update the location
	public void doubleClicked()
	{
		PlayArea.displayedCards.add(remove());
		//TODO: Set location
	}

	public void add(PlayableCard card)
	{
		super.add(card);
		// Top card has changed so update image
		setImage(card.getImage());
	}
	
	public BufferedImage getImage()
	{
		if(cards.isEmpty())
		{
			return null;
		}
		return image;
	}
}
