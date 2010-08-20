package dojo;
// GameState.java
// Written by Brian Ouellette
// Contains any pertinent information to the state of the game (cards on the table, decks, etc)

import java.util.ArrayList;
import java.util.List;

class GameState {
	// Your decks and discard piles 
	private Deck dynastyDeck, fateDeck;
	private Discard dynastyDiscard, fateDiscard;
	// Provinces (left to right from 0->max)
	private List<Province> provinces;
	// The base cards of all units to be displayed. Attachments are fetched at display time and aren't present here
	// This is done so that attachments will always be drawn behind units
	private List<PlayableCard> table;
	// Cards present in the players hand
	private List<PlayableCard> hand;
	// All cards visible to player, to keep necessary logic during repaint down
	private List<PlayableCard> allCards;

	public GameState()
	{
		//TODO: Support Ratling/Spirit or other starting sizes
		// Initialize four provinces
		provinces = new ArrayList<Province>(4);
		provinces.add(new Province());
		provinces.add(new Province());
		provinces.add(new Province());
		provinces.add(new Province());

		// Create decks, discards, and provinces
		dynastyDeck = new Deck(true);
		fateDeck = new Deck(false);
		dynastyDiscard = new Discard();
		fateDiscard = new Discard();

		// Create a new ArrayList to hold the cards to display
		table = new ArrayList<PlayableCard>(12);
		hand = new ArrayList<PlayableCard>(8);
		allCards = new ArrayList<PlayableCard>(20);
	}

	public void rescale()
	{
		// Rescale images for all cards in units in play
		for(PlayableCard card : table)
		{
			card.rescale();
			card.updateAttachmentLocations();
			// Including their attachments
			for(PlayableCard attachment : card.getAllAttachments())
			{
				attachment.rescale();
			}
		}
		// Rescale images from cards contained in or attached to provinces
		for(Province province : provinces)
		{
			province.rescale();
		}
	}

	public void resetState(Deck dynasty, Deck fate)
	{
		// Used when loading a new deck or clearing the field
		// Remove all cards from play
		table.clear();
		hand.clear();
		allCards.clear();
		// Remove all cards from discards
		fateDiscard.removeAll();
		dynastyDiscard.removeAll();
		// Reset decks to new decks
		dynastyDeck = dynasty;
		fateDeck = fate;
		provinces.clear();
		provinces.add(new Province());
		provinces.add(new Province());
		provinces.add(new Province());
		provinces.add(new Province());
	}

	//TODO: Move logic from random classes into GameState, these getters should all be gone eventually ideally
	public List<Province> getProvinces()
	{
		return provinces;
	}

	public Deck getDynastyDeck()
	{
		return dynastyDeck;
	}

	public Deck getFateDeck()
	{
		return fateDeck;
	}

	public Discard getDynastyDiscard()
	{
		return dynastyDiscard;
	}

	public Discard getFateDiscard()
	{
		return fateDiscard;
	}

	public void addToDiscard(PlayableCard card)
	{
		// Takes a card from table and puts it in the appropriate discard
		table.remove(card);
		allCards.remove(card);
		if(card.isDynasty())
		{
			dynastyDiscard.add(card);
		}
		else
		{
			fateDiscard.add(card);
		}
	}

	public void addToDeck(PlayableCard card)
	{
		// Takes a card from table and puts it in the appropriate deck
		table.remove(card);
		allCards.remove(card);
		if(card.isDynasty())
		{
			dynastyDeck.add(card);
		}
		else
		{
			fateDeck.add(card);
		}
	}
	
	public void unbowAll()
	{
		// Unbow all cards on the table
		for(PlayableCard card : table)
		{
			card.unbow();
			// And all cards attached to them
			for(PlayableCard attachment : card.getAllAttachments())
			{
				attachment.unbow();
			}
		}
		// And all cards attached to provinces
		for(Province province : provinces)
		{
			for(PlayableCard attachment : province.getAttachments())
			{
				attachment.unbow();
			}
		}
	}
	
	public List<PlayableCard> getAllCards()
	{
		return allCards;
	}

	public boolean addToTable(PlayableCard card)
	{
		// If we successfully add it then also add to all cards and indicate success
		if(table.add(card))
		{
			allCards.add(card);
			return true;
		}
		return false;
	}
	
	// Remove from either the table or the hand, wherever it is
	public boolean removeCard(PlayableCard card)
	{
		return removeFromTable(card) || removeFromHand(card);
	}

	public boolean removeFromTable(PlayableCard card)
	{
		// If we successfully remove it then also remove from all cards and indicate success
		if(table.remove(card))
		{
			allCards.remove(card);
			return true;
		}
		return false;
	}
	
	public boolean addToHand(PlayableCard card)
	{
		// If we successfully add it then also add to all cards and indicate success
		if(hand.add(card))
		{
			allCards.add(card);
			return true;
		}
		return false;
	}
	
	public boolean removeFromHand(PlayableCard card)
	{
		// If we successfully remove it then also remove from all cards and indicate success
		if(hand.remove(card))
		{
			allCards.remove(card);
			return true;
		}
		return false;
	}
	
	public boolean handContains(PlayableCard card)
	{
		// See if the card is directly contained in the hand
		if(hand.contains(card))
		{
			return true;
		}
		// If not see if it is attached to something in the hand
		for(PlayableCard base : hand)
		{
			if(base.getAllAttachments().contains(card))
			{
				return true;
			}
		}
		// Failed all tests. Must not be in hand
		return false;
	}
}
