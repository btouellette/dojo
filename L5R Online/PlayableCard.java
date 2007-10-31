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
	int x, y;

    public PlayableCard(String id)
    {
		this.id = id;
    }

	public String getID()
	{
		return id;
	}

	public void setLocation(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public Dimension getLocation()
	{
		return new Dimension(x, y);
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