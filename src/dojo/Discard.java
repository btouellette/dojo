package dojo;
// Discard.java
// Written by Brian Ouellette
// Represents fate and dynasty discards

import java.awt.image.BufferedImage;

class Discard extends CardHolder
{
	public BufferedImage getImage()
	{
		if(!cards.isEmpty())
		{
			return cards.get(0).getImage();
		}
		return null;
	}
}
