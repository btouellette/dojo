package dojo;
// Province.java
// Written by Brian Ouellette
// Used for representing your provinces

import java.util.ArrayList;
import java.awt.image.BufferedImage;

//TODO: Add support for provinces which contain multiple cards
//TODO: Add support for provinces which don't refill
class Province extends CardHolder
{
	private ArrayList<PlayableCard> attachments;
	// Whether the cards in the province are face-up or not
	private boolean faceUp;
	
	public Province()
	{
		super(1);
		attachments = new ArrayList<PlayableCard>();
	}
		
	public ArrayList<PlayableCard> getAttachments()
	{
		return attachments;
	}

	public void attach(PlayableCard card)
	{
		PlayArea.displayedCards.remove(card);
		attachments.add(card);
		attachments.addAll(card.getAllAttachments());
	}

	public BufferedImage getImage()
	{
		if(cards.isEmpty())
		{
			return null;
		}
		if(faceUp)
		{
			return cards.get(0).getImage();
		}
		return StoredImages.dynasty;
	}
	
	public void destroy()
	{
		while(!attachments.isEmpty())
		{
			attachments.remove(0).destroy();
		}
		while(!cards.isEmpty())
		{
			cards.remove(0).destroy();
		}
	}
}
