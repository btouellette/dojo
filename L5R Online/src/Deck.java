import java.util.*;

class Deck
{
	// 0 is top card
	ArrayList<StoredCard> cards;
	
	public Deck()
	{
		cards = new ArrayList<StoredCard>(45);
	}
	
	public PlayableCard removeCard()
	{
		return new PlayableCard(cards.remove(0).getID());
	}
	
	public PlayableCard removeCard(StoredCard card)
	{
		cards.remove(card);
		return new PlayableCard(card.getID());
	}
	
	public void addCard(StoredCard card)
	{
		// Put on top
		cards.add(0, card);
	}

	public void addCard(PlayableCard card)
	{
		cards.add(0, Main.database.get(card.getID()));
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