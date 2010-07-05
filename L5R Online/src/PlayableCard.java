// PlayableCard.java
// Written by Brian Ouellette
// Part of Dojo

import java.util.ArrayList;
import java.awt.image.BufferedImage;

class PlayableCard
{
	//Info needed
	private String id;
	private ArrayList<PlayableCard> attachments;
	private int[] location;
	private BufferedImage cardImage;

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
		//TODO: Make sure this works properly with multiple of the same card attached
		attachments.remove(unattachingCard);
	}

	public void updateAttachmentLocations()
	{
		ArrayList<PlayableCard> allAttachments = getAllAttachments();
		for(int i = 0; i < allAttachments.size(); i++)
		{
			PlayableCard currentCard = allAttachments.get(i);
			currentCard.setLocationSimple(location[0], location[1] - (int)(PlayArea.cardHeight*PlayArea.attachmentHeight*(i+1)));
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
