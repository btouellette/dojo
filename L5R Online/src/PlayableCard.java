// PlayableCard.java
// Written by Brian Ouellette
// Part of Dojo

import java.util.ArrayList;
import java.awt.image.BufferedImage;

class PlayableCard
{
	//Info needed
	String id;
	ArrayList<PlayableCard> attachments;
	int[] location;
	BufferedImage cardImage;

    public PlayableCard(String id)
    {
		this.id = id;
		location = new int[2];
		location[0] = 0;
		location[1] = 0;
		attachments = new ArrayList<PlayableCard>();
    }

	public String getID()
	{
		return id;
	}

	public void setLocation(int x, int y)
	{
		location[0] = x;
		location[1] = y;
		updateAttachmentLocations();
	}

	public void setLocationSimple(int x, int y)
	{
		location[0] = x;
		location[1] = y;
	}

	public int[] getLocation()
	{
		return location;
	}

	public void attach(PlayableCard attachingCard)
	{
		attachments.add(attachingCard);
	}

	public void unattach(PlayableCard unattachingCard)
	{
		attachments.remove(attachments.indexOf(unattachingCard));
	}

	public void updateAttachmentLocations()
	{
		for(int i = 0; i < attachments.size(); i++)
		{
			PlayableCard currentCard = attachments.get(i);
			currentCard.setLocationSimple(location[0], location[1] - (int)(PlayArea.cardHeight*PlayArea.attachmentHeight));
			if(!currentCard.getAttachments().isEmpty())
			{
				currentCard.updateAttachmentLocations();
			}
		}
	}

	public ArrayList<PlayableCard> getAttachments()
	{
		return attachments;
	}

	public ArrayList<PlayableCard> getAllAttachments()
	{
		ArrayList<PlayableCard> recursedAttachments = new ArrayList<PlayableCard>();
		for(int i = 0; i < attachments.size(); i++)
		{
			recursedAttachments.add(attachments.get(i));
			recursedAttachments.addAll(attachments.get(i).getAllAttachments());
		}
		return recursedAttachments;
	}

	public BufferedImage getImage()
	{
		return cardImage;
	}

	public void setImage(BufferedImage cardImage)
	{
		this.cardImage = cardImage;
	}
}