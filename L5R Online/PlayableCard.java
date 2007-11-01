// PlayableCard.java
// Written by Brian Ouellette
// Part of Dojo

package l5r;

import java.util.ArrayList;
import java.awt.Dimension;

class PlayableCard
{
	//Info needed
	String id;
	ArrayList<Card> attachments;
	int[] location;

    public PlayableCard(String id)
    {
		this.id = id;
		location = new int[2];
		location[0] = 0;
		location[1] = 0;
    }

	public String getID()
	{
		return id;
	}

	public void setLocation(int x, int y)
	{
		location[0] = x;
		location[1] = y;
	}

	public int[] getLocation()
	{
		return location;
	}

	public void attach(Card attachingCard)
	{
		attachments.add(attachingCard);
	}

	public void unattach(Card unattachingCard)
	{
		attachments.remove(attachments.indexOf(unattachingCard));
	}
}