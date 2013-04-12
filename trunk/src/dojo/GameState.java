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
	
	// Opponents game state, mapped by client ID
	private Map<Integer, GameState> opponentStates;
	// Map between client and player IDs and back
	// This is used since Egg differentiates these IDs depending on where in the game we are
	// Client IDs who do not have associated player IDs are spectating a game and will not be in these maps
	private Map<Integer, Integer> clientIDToPlayerID;
	private Map<Integer, Integer> playerIDToClientID;
	// Map between card IDs and the cards themselves, this includes all cards we know about
	private Map<Integer, Card> cards;
	// Name of the owner of this state
	public String name;
	// Honor for the owner of this state
	public int honor;

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
		clientIDToPlayerID = new HashMap<Integer, Integer>();
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
		opponentStates.put(clientID, new GameState(name));
	}

	public void setOpponentName(int clientID, String name)
	{
		opponentStates.get(clientID).name = name;
	}

	public void loadOpponentDecks(int clientID, Map<String, Integer> cardList)
	{
		// Grab the game state associated with this client
		GameState state = opponentStates.get(clientID);
		// Add each card in the card list the appropriate number of times to the correct deck
		for (Map.Entry<String, Integer> entry : cardList.entrySet())
		{
			int numCards = entry.getValue();
			for (int i = 0; i < numCards; i++)
			{
				PlayableCard card = new PlayableCard(entry.getKey());
				if(card.isDynasty()) {
					state.dynastyDeck.add(card);
				} else {
					state.fateDeck.add(card);
				}
			}
		}
	}

	public void setName(String name) {
		// TODO: Send out update to network
		this.name = name;
	}

	public void associateClientIDToPlayerID(int clientID, int playerID) {
		clientIDToPlayerID.put(clientID, playerID);
		playerIDToClientID.put(playerID, clientID);
	}

	public void setOpponentZone(Location zone, int[] cardIDs, int playerID) {
		opponentStates.get(playerIDToClientID.get(playerID)).setZone(zone, cardIDs);
	}

	public void setZone(Location zone, int[] cardIDs) {
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
				card = new Card(cardID);
				cards.put(cardID, card);
			}
			card.location = zone;
		}
	}

	public void setHonor(int honor) {
		this.honor = honor;
	}

	public void setOpponentHonor(int playerID, int honor) {
		opponentStates.get(playerIDToClientID.get(playerID)).setHonor(honor);
	}
}