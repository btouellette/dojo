package dojo;
// PlayableCard.java
// Written by Brian Ouellette
// Part of Dojo

import java.util.ArrayList;
import java.awt.*;
import java.awt.image.BufferedImage;

class PlayableCard
{
	//Info needed
	private final String id;
	private final String type;
	private ArrayList<PlayableCard> attachments;
	private int[] location;
	private BufferedImage cardImage;
	private boolean isDynasty;

	public PlayableCard(String id)
	{
		this.id = id;
		location = new int[2];
		location[0] = 0;
		location[1] = 0;
		attachments = new ArrayList<PlayableCard>();
		type = Main.database.get(id).getType();
		
		if(type.equals("actions")   || type.equals("kihos")     || type.equals("spells") ||
		   type.equals("ancestors") || type.equals("followers") || type.equals("items")  ||
		   type.equals("rings")     || type.equals("senseis")   || type.equals("winds"))
		{
			isDynasty = false;
		}
		else
		{
			// True for: events, regions, holdings, personalities, strongholds
			isDynasty = true;
		}
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
		// Return copy so as not to allow inadvertent changes
		return location.clone();
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
	
	public BufferedImage createImage()
	{
		// 306x428 is size of high res images provided by Alderac
		BufferedImage image = new BufferedImage(306, 428, BufferedImage.TYPE_BYTE_GRAY);
		image.createGraphics();
		Graphics2D g = (Graphics2D)image.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 306, 428);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(10, 10, 286, 408);
		//TODO: Handle long names well
		//TODO: Consider using templates that are type appropriate
		String name = Main.database.get(id).getName();
		Font font = new Font(g.getFont().getFontName(), Font.ITALIC | Font.BOLD, 25);
		g.setFont(font);
	    int x = (306 - g.getFontMetrics().stringWidth(name)) / 2;  
		g.setColor(Color.BLACK);  
		g.drawString(name, x, 50);
		cardImage = image;
		return image;
	}

	public void setImage(BufferedImage cardImage)
	{
		this.cardImage = cardImage;
	}
	
	public boolean isDynasty()
	{
		return isDynasty;
	}
	
	public boolean isFate()
	{
		return !isDynasty;
	}
}
