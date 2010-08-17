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
	
	public Province()
	{
		// Only allocate space for 1 card
		super(1);
		attachments = new ArrayList<PlayableCard>();
	}

	public void doubleClicked()
	{
		if(!cards.isEmpty())
		{
			// Turn the card in the province over on double click
			if(!cards.get(0).isFaceUp())
			{
				cards.get(0).setFaceUp();
			}
			// Or do default (put card on table)
			else
			{
				super.doubleClicked();
			}
		}
	}

	public boolean isEmpty()
	{
		return cards.isEmpty();
	}
		
	public ArrayList<PlayableCard> getAttachments()
	{
		return attachments;
	}

	public void attach(PlayableCard card)
	{
		Main.state.removeDisplayedCard(card);
		// Add card and any attachments of it to the province's attachments
		attachments.add(card);
		attachments.addAll(card.getAllAttachments());
		updateAttachmentLocations();
	}

	private void updateAttachmentLocations()
	{
		// Update all attachment locations
		for(int i = 0; i < attachments.size(); i++)
		{
			attachments.get(i).setLocationSimple(location[0], location[1] - (int)(Main.playArea.getCardHeight()*Main.playArea.getAttachmentHeight()*(i+1)) - 4);
		}
	}
	
	public void unattach(PlayableCard card)
	{
		// Move card from attachments to the play table
		if(attachments.remove(card))
		{
			Main.state.addDisplayedCard(card);
			// Unattach any cards attached to the card from the province as well
			for(PlayableCard attachment : card.getAllAttachments())
			{
				attachments.remove(attachment);
			}
			card.setLocation(location[0], location[1] - (Main.playArea.getCardHeight()+8));
			updateAttachmentLocations();
		}

	}

	public BufferedImage getImage()
	{
		if(cards.isEmpty())
		{
			return null;
		}
		return cards.get(0).getImage();
	}

	public void rescale()
	{
		// Rescale all cards in the province
		for(PlayableCard card : cards)
		{
			card.rescale();
		}
		// And all cards attached to the province
		for(PlayableCard attachment : attachments)
		{
			attachment.rescale();
		}
		updateAttachmentLocations();
	}
	
	public void destroy()
	{
		// Destroy all attachments
		for(PlayableCard attachment : attachments)
		{
			attachment.destroy();
		}
		// And the cards in the province
		for(PlayableCard card : cards)
		{
			card.destroy();
		}
	}
}
