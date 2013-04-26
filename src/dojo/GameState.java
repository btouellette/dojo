package dojo;

// GameState.java
// Written by Brian Ouellette
// Contains any pertinent information to the state of the game (cards on the table, decks, etc)

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dojo.Card.GameArea;

public class GameState
{	
	// Name of the owner of this state
	public String name;
	// Honor for the owner of this state
	public int honor;
	// Player ID for this GameState
	public int playerID;
	// Complete deck list (holding XML DB IDs such as Celestial001 not game card IDs)
	public List<Card> deck;
	
	// Opponents game state, mapped by player ID
	private Map<Integer, GameState> opponentStates;
	// Name and loaded decks for clients who have connected but not joined the game yet
	// Mapped by client ID
	private Map<Integer, String> clientNames;
	private Map<Integer, List<Card>> clientDecks;
	
	// These are all the locations that can contain cards	
	// Map between card IDs and the cards themselves, this includes all cards owned by this player, ordered zones are lists, unordered zones are maps
	private Map<Integer, Card> hand, removedFromGame, table;
	private List<Card> dynastyDeck, dynastyDiscard, fateDeck, fateDiscard;
	
	// Provinces (left to right from 0->max)
	// Cards in provinces are in the table collection, this is for bookkeeping
	private List<Province> provinces;

	// Card storage architecture options	
	// Single flat storage: cards in single container in main game state
	// Multiple flat storage: cards in game area specific containers in main game state
	// Single split storage: cards in single containers in owner's game state
	// Multiple split storage: cards in game area specific containers in owner's game state
	//
	// Alternately we can maintain multiple containers in parallel (gross), multiple split plus master container with known cards
	// Generic card (or location or owner or container) lookup can be reduced to a subroutine
	// New card creation (only parallel maintenance) is limited to select situations
	//
	// Single vs Multiple: one container vs game area specific containers
	// CON: single makes zone ordering less obvious
	// CON: single makes zone shuffle slightly harder
	// PRO: single makes changing card area slightly simpler
	// PRO: single makes card display slightly simpler
	// PRO: single means that when game area not sent along with cardID in JSON search for correct game area not required
	//
	// Flat vs Split: main vs owner's game state
	// CON: flat requires playerID validation before interacting with card rather than just on entering state
	// CON: flat is less architecturally obvious
	// PRO: flat makes changing card owner slightly easier
	// PRO: flat makes card display slightly simpler
	// PRO: flat means that when playerID not sent along with cardID in JSON search for correct state not required
	
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
		dynastyDeck = new ArrayList<Card>(50);
		fateDeck = new ArrayList<Card>(50);
		dynastyDiscard = new ArrayList<Card>();
		fateDiscard = new ArrayList<Card>();

		// Create a new ArrayList to hold the cards to display
		table = new HashMap<Integer, Card>();
		hand = new HashMap<Integer, Card>();
		removedFromGame = new HashMap<Integer, Card>();
		
		opponentStates = new HashMap<Integer, GameState>();
		clientNames = new HashMap<Integer, String>();
		clientDecks = new HashMap<Integer, List<Card>>();
	}
	
	public GameState(int playerID, String name)
	{
		this();
		this.playerID = playerID;
		this.name = name;
	}

	public void rescale()
	{
		// Rescale images for all cards in units in play
		for (Card card : table.values()) {
			card.rescale();
			card.updateAttachmentLocations();
			// Including their attachments
			for (Card attachment : card.getAllAttachments()) {
				attachment.rescale();
			}
		}
	}

	public void resetState()
	{
		// Used when loading a new deck or clearing the field
		// Remove all cards from play
		table.clear();
		hand.clear();
		fateDiscard.clear();
		dynastyDiscard.clear();
		removedFromGame.clear();
		dynastyDeck.clear();
		fateDeck.clear();
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

	public List<Card> getDynastyDeck()
	{
		return dynastyDeck;
	}

	public List<Card> getFateDeck()
	{
		return fateDeck;
	}

	public List<Card> getDynastyDiscard()
	{
		return dynastyDiscard;
	}

	public List<Card> getFateDiscard()
	{
		return fateDiscard;
	}

	public void addToDiscard(Card card)
	{
		// Takes a card from table and puts it in the appropriate discard
		table.remove(card);
		if (card.isDynastyCard()) {
			dynastyDiscard.add(card);
		} else {
			fateDiscard.add(card);
		}
	}

	public void addToDeck(Card card)
	{
		// Takes a card from table and puts it at the top of the appropriate deck
		table.remove(card.id);
		if (card.isDynastyCard()) {
			dynastyDeck.add(0, card);
		} else {
			fateDeck.add(0, card);
		}
	}

	public void unbowAll()
	{
		// Unbow all cards on the table
		for (Card card : table.values()) {
			card.unbow();
			// And all cards attached to them
			for (Card attachment : card.getAllAttachments()) {
				attachment.unbow();
			}
		}
	}

	public List<Card> getAllCards()
	{ 
		//TODO: This is surely wildly inefficient, do this with iterators or something else, or just bake the logic into whatever is using it
		List<Card> allCards = new ArrayList<Card>(table.values());
		allCards.addAll(hand.values());
		for(GameState opponentState : opponentStates.values()) {
			allCards.addAll(opponentState.table.values());
		}
		return allCards;
	}

	public boolean addToTable(Card card)
	{
		// If we successfully add it indicate success
		if (!table.containsKey(card.id)) {
			table.put(card.id, card);
			return true;
		}
		return false;
	}

	// Remove from either the table or the hand, wherever it is
	public boolean removeCard(Card card)
	{
		return removeFromTable(card) || removeFromHand(card);
	}

	public boolean removeFromTable(Card card)
	{
		// If we successfully remove it then also remove from all cards and indicate success
		if (table.containsKey(card.id)) {
			table.remove(card.id);
			return true;
		}
		return false;
	}

	public boolean addToHand(Card card)
	{
		// If we successfully add it indicate success
		// Check if hand already contains this card
		if(!hand.containsKey(card.id)) {
			hand.put(card.id, card);
			return true;
		}
		return false;
	}

	public boolean removeFromHand(Card card)
	{
		// If we successfully remove it indicate success
		// Check if hand already contains this card
		if(hand.containsKey(card.id)) {
			hand.remove(card.id);
			return true;
		}
		return false;
	}

	public boolean handContains(Card card)
	{
		// See if the card is directly contained in the hand
		if (hand.containsKey(card.id))
			return true;
		// If not see if it is attached to something in the hand
		for (Card baseCard : hand.values()) {
			List<Card> attachments = baseCard.getAllAttachments();
			for (Card attachedCard : attachments)
				if (card.id == attachedCard.id)
					return true;
		}
		// Failed all tests. Must not be in hand
		return false;
	}	
	
	public void opponentConnect(int clientID, String name)
	{
		clientNames.put(clientID, name);
	}

	public void loadOpponentDeck(Map<Card, Integer> cardList, int clientID)
	{
		// Add each card in the card list the appropriate number of times to the correct deck
		// cardList is a mapping between each card in the deck's XML DB ID and the number of instances in the deck
		for (Map.Entry<Card, Integer> entry : cardList.entrySet())
		{
			int numCards = entry.getValue();
			for (int i = 0; i < numCards; i++)
			{
				if(!clientDecks.containsKey(clientID)) {
					clientDecks.put(clientID, new ArrayList<Card>());
				}
				clientDecks.get(clientID).add(entry.getKey());
			}
		}
	}

	public void setName(String name) {
		// TODO: Send out update to network
		this.name = name;
	}

	public void setGameAreaContents(GameArea area, int[] cardIDs, int playerID) {
		if(this.playerID == playerID) {
			// Clear out any existing cards in this zone
			List<Card> cardsList = null;
			if (area == GameArea.DynastyDeck) {
				cardsList = dynastyDeck;
			} else if (area == GameArea.DynastyDiscard) {
				cardsList = dynastyDiscard;
			} else if (area == GameArea.FateDeck) {
				cardsList = fateDeck;
			} else if (area == GameArea.FateDiscard) {
				cardsList = fateDiscard;
			}
			if(cardsList != null) {
				// Remove any existing cards in that game area from the game
				for(Card card : cardsList) {
					card.gameArea = GameArea.RemovedFromGame;
					removedFromGame.put(card.id, card);
				}
				cardsList.clear();
				// Add all cards into the game area, creating them if they don't yet exist
				for(int cardID : cardIDs) {
					Card card = getCard(cardID);
					if(card == null) {
						card = new Card(cardID, playerID);
					}
					card.gameArea = area;
					cardsList.add(card);
				}
			}
			Map<Integer, Card> cardsMap = null;
			if (area == GameArea.FocusPool) {
				//TODO: Handle focus pools
				throw new UnsupportedOperationException("Trying to set a focus pool zone, not programmed yet");
			} else if (area == GameArea.Hand) {
				cardsMap = hand;
			} else if (area == GameArea.Table) {
				cardsMap = table;
			}
			if(cardsMap != null) {
				// Remove any existing cards in that game area from the game
				for(Card card : cardsMap.values()) {
					card.gameArea = GameArea.RemovedFromGame;
					removedFromGame.put(card.id, card);
				}
				cardsMap.clear();
				// Add all cards into the game area, creating them if they don't yet exist
				for(int cardID : cardIDs) {
					Card card = getCard(cardID);
					if(card == null) {
						card = new Card(cardID, playerID);
					}
					card.gameArea = area;
					cardsMap.put(cardID, card);
				}
			}
		} else {
			opponentStates.get(playerID).setGameAreaContents(area, cardIDs, playerID);
		}
	}

	// Find a card by card ID in this or any child game states
	public Card getCard(int cardID) {
		if (table.containsKey(cardID)) 
			return table.get(cardID);
		if (hand.containsKey(cardID)) 
			return table.get(cardID);
		if (removedFromGame.containsKey(cardID)) 
			return table.get(cardID);
		for (Card card : dynastyDeck)
			if(cardID == card.id)
				return card;
		for (Card card : fateDeck)
			if(cardID == card.id)
				return card;
		for (Card card : dynastyDiscard)
			if(cardID == card.id)
				return card;
		for (Card card : fateDiscard)
			if(cardID == card.id)
				return card;
		for (GameState childState : opponentStates.values()) {
			Card card = childState.getCard(cardID);
			if(card != null)
				return card;
		}
		return null;
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
		GameState state = new GameState(playerID, clientNames.get(clientID));
		state.deck = clientDecks.get(clientID);
		opponentStates.put(playerID, state);
	}

	public void coinFlipped(boolean value, int playerID) {
		// TODO: report to chat
	}
	

	public void setCardProperty(int cardID, String property, boolean value, int playerID) {
		Card card = getCard(cardID);
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

	public void moveCard(int cardID, Double x, Double y, boolean faceUp,
			int moverPlayerID, GameArea destGameArea,
			int destOwnerPlayerID, boolean random,
			Boolean toTopOfDestGameArea) {
		Card movingCard = getCard(cardID);
		if(movingCard == null) {
			throw new IllegalArgumentException ("Couldn't find card with card ID " + cardID + " to move");
		}
		if(x != null && y != null) {
			movingCard.x = x;
			movingCard.y = y;
		}
		movingCard.faceUp = faceUp;
		movingCard.gameArea = destGameArea;
	}

	public void revealCard(int cardID, String xmlID) {
		Card card = getCard(cardID);
		if(card == null) {
			throw new IllegalArgumentException ("Couldn't find card with card ID " + cardID + " to reveal");
		}
		card.setXMLID(xmlID);
	}
}