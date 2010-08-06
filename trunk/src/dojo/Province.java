package dojo;
// Province.java
// Written by Brian Ouellette
// Used for representing your provinces

import java.util.ArrayList;

class Province extends CardHolder
{
	private ArrayList<PlayableCard> attachments;
	
	public Province()
	{
		super(1);
		attachments = new ArrayList<PlayableCard>();
	}
		
	public ArrayList<PlayableCard> getAttachments()
	{
		return attachments;
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