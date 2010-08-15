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
	private List<PlayableCard> displayedCards;

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
		displayedCards = new ArrayList<PlayableCard>(30);
	}

	public void rescale()
	{
		// Rescale images for all cards in units in play
		for(PlayableCard card : displayedCards)
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
		displayedCards.clear();
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
		// Takes a card from displayedCards and puts it in the appropriate discard
		displayedCards.remove(card);
		if(card.isDynasty())
		{
			dynastyDiscard.add(card);
		}
		else
		{
			fateDiscard.add(card);
		}
	}

	public List<PlayableCard> getDisplayedCards()
	{
		return displayedCards;
	}

	public void addDisplayedCard(PlayableCard card)
	{
		displayedCards.add(card);
	}

	public void removeDisplayedCard(PlayableCard card)
	{
		displayedCards.remove(card);
	}
}
