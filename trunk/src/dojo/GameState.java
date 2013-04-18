package dojo;

// GameState.java
// Written by Brian Ouellette
// Contains any pertinent information to the state of the game (cards on the table, decks, etc)

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dojo.Card.Location;

public class GameState
{
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
	
	// Opponents game state, mapped by player ID
	private Map<Integer, GameState> opponentStates;
	// Name and loaded decks for clients who have connected but not joined the game yet
	// Mapped by client ID
	private Map<Integer, String> clientNames;
	private Map<Integer, List<String>> clientDecks;
	// Map between card IDs and the cards themselves, this includes all cards we know about
	private Map<Integer, Card> cards;
	// Name of the owner of this state
	public String name;
	// Honor for the owner of this state
	public int honor;
	// Player ID for this GameState
	public int playerID;
	// Deck (holding card names ala Edition001 not card IDs)
	private List<String> deck;

	public GameState()
	{
		// TODO: Support Ratling/Spirit or other starting sizes
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
		
		opponentStates = new HashMap<Integer, GameState>();
		clientNames = new HashMap<Integer, String>();
		clientDecks = new HashMap<Integer, List<String>>();
		cards = new HashMap<Integer, Card>();
	}
	
	public GameState(String name)
	{
		this();
		this.name = name;
	}

	public void rescale()
	{
		// Rescale images for all cards in units in play
		for (PlayableCard card : table) {
			card.rescale();
			card.updateAttachmentLocations();
			// Including their attachments
			for (PlayableCard attachment : card.getAllAttachments()) {
				attachment.rescale();
			}
		}
		// Rescale images from cards contained in or attached to provinces
		for (Province province : provinces) {
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

	// TODO: Move logic from random classes into GameState, these getters should all be gone eventually ideally
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
		if (card.isDynasty()) {
			dynastyDiscard.add(card);
		} else {
			fateDiscard.add(card);
		}
	}

	public void addToDeck(PlayableCard card)
	{
		// Takes a card from table and puts it in the appropriate deck
		table.remove(card);
		allCards.remove(card);
		if (card.isDynasty()) {
			dynastyDeck.add(card);
		} else {
			fateDeck.add(card);
		}
	}

	public void unbowAll()
	{
		// Unbow all cards on the table
		for (PlayableCard card : table) {
			card.unbow();
			// And all cards attached to them
			for (PlayableCard attachment : card.getAllAttachments()) {
				attachment.unbow();
			}
		}
		// And all cards attached to provinces
		for (Province province : provinces) {
			for (PlayableCard attachment : province.getAttachments()) {
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
		if (table.add(card)) {
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
		if (table.remove(card)) {
			allCards.remove(card);
			return true;
		}
		return false;
	}

	public boolean addToHand(PlayableCard card)
	{
		// If we successfully add it then also add to all cards and indicate success
		if (hand.add(card)) {
			allCards.add(card);
			return true;
		}
		return false;
	}

	public boolean removeFromHand(PlayableCard card)
	{
		// If we successfully remove it then also remove from all cards and indicate success
		if (hand.remove(card)) {
			allCards.remove(card);
			return true;
		}
		return false;
	}

	public boolean handContains(PlayableCard card)
	{
		// See if the card is directly contained in the hand
		if (hand.contains(card)) {
			return true;
		}
		// If not see if it is attached to something in the hand
		for (PlayableCard base : hand) {
			if (base.getAllAttachments().contains(card)) {
				return true;
			}
		}
		// Failed all tests. Must not be in hand
		return false;
	}
	
	
	
	public void opponentConnect(int clientID, String name)
	{
		clientNames.put(clientID, name);
	}

	public void loadOpponentDeck(Map<String, Integer> cardList, int clientID)
	{
		// Add each card in the card list the appropriate number of times to the correct deck
		for (Map.Entry<String, Integer> entry : cardList.entrySet())
		{
			int numCards = entry.getValue();
			for (int i = 0; i < numCards; i++)
			{
				if(!clientDecks.containsKey(clientID)) {
					clientDecks.put(clientID, new ArrayList<String>());
				}
				clientDecks.get(clientID).add(entry.getKey());
			}
		}
	}

	public void setName(String name) {
		// TODO: Send out update to network
		this.name = name;
	}

	public void setZone(Location zone, int[] cardIDs, int playerID) {
		if(this.playerID == playerID) {
			// Clear out any existing cards in this zone
			for(Card card : cards.values()) {
				// If there is a card already in the zone being set remove it
				if(card.location == zone) {
					card.location = Location.RemovedFromGame;
				}
			}
			// Add all cards into zone, creating them if they don't yet exist
			for(int cardID : cardIDs) {
				Card card = cards.get(cardID);
				if(card == null) {
					card = new Card(cardID, playerID);
					cards.put(cardID, card);
				}
				card.location = zone;
			}
		} else {
			opponentStates.get(playerID).setZone(zone, cardIDs, playerID);
		}
	}

	public void setHonor(int honor, int playerID) {
		if(this.playerID == playerID) {
			this.honor = honor;
		} else {
			opponentStates.get(playerID).setHonor(honor, playerID);
		}
	}

	public void playerJoined(int clientID, int playerID) {
		// Move the client name and deck into a game state
		GameState state = new GameState(clientNames.get(clientID));
		state.deck = clientDecks.get(clientID);
		opponentStates.put(playerID, state);
	}

	public void coinFlipped(boolean value, int playerID) {
		// TODO: report to chat
	}
	

	public void setCardProperty(int cardID, String property, boolean value, int playerID) {
		Card card = cards.get(cardID);
		card.ownerID = playerID;
		if("tapped".equals(property)) {
			card.bowed = value;
		} else if("faceUp".equals(property)) {
			card.faceUp = value;
		} else if("dishonored".equals(property)) {
			card.dishonored = value;
		}
	}

	public void dieRolled(int result, int size, int playerID) {
		// TODO: report to chat
	}
}