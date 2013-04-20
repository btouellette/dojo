package dojo;

// TODO: find and replace all instances of Stored and Playable Cards with this class
public class Card {
	public enum GameArea { Table, DynastyDeck, FateDeck, DynastyDiscard, FateDiscard, Hand, RemovedFromGame, FocusPool }
	
	// Unique identifier for this game card (cgid in Egg terminology)
	int id;
	// Card properties
	boolean bowed, faceUp, dishonored;
	// Card location in-game (which game area it currently lives in)
	GameArea gameArea;
	// PlayerID of the owner of this card
	int ownerID;
	// Card ID from XML DB
	String xmlID;
	// Card x and y locations
	double x, y;
	
	public Card(int id, int ownerID) {
		this.id = id;
		this.ownerID = ownerID;
		bowed = false;
		faceUp = false;
		dishonored = false;
	}
	
	public Card(String xmlID) {
		this.xmlID = xmlID;
	}
}
