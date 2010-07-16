package dojo;
import java.util.*;

class Deck
{
	// 0 is top card
	ArrayList<StoredCard> cards;
	
	public Deck()
	{
		cards = new ArrayList<StoredCard>(45);
	}
	
	public PlayableCard remove()
	{
		return new PlayableCard(cards.remove(0));
	}
	
	public PlayableCard remove(StoredCard card)
	{
		cards.remove(card);
		return new PlayableCard(card);
	}
	
	public void removeAll()
	{
		cards.clear();
	}
	
	public void add(StoredCard card)
	{
		// Put on top
		cards.add(0, card);
	}

	public void add(PlayableCard card)
	{
		cards.add(0, Main.databaseID.get(card.getID()));
	}
	
	public void shuffle()
	{
		Collections.shuffle(cards);
	}
	
	public int numCards()
	{
		return cards.size();
	}
}