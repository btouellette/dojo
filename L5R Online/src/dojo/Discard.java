package dojo;

import java.awt.image.BufferedImage;

public class Discard extends Deck {
	//TODO: Store as PlayableCard instead of StoredCard in discard decks
	public PlayableCard remove()
	{
		if(cards.size() > 1)
		{
			setImage((new PlayableCard(cards.get(1))).getImage());
		}
		else
		{
			setImage(null);
		}
		return new PlayableCard(cards.remove(0));
	}
	
	public void add(StoredCard card)
	{
		super.add(card);
		setImage((new PlayableCard(card)).getImage());
	}

	public void add(PlayableCard card)
	{
		super.add(card);
		setImage(card.getImage());
	}
	
	public BufferedImage getImage()
	{
		if(cards.isEmpty())
		{
			return null;
		}
		return deckImage;
	}
}
