package dojo;

public class Card {
	// Unique identifier for this card (cgid in Egg terminology)
	int id;
	// Card properties
	boolean bowed, faceUp, dishonored;
	// Card location in-game
	public enum Location { Table, DynastyDeck, FateDeck, DynastyDiscard, FateDiscard, Hand, RemovedFromGame, FocusPool }
	Location location;
	// PlayerID of the owner of this card
	int ownerID;
	
	public Card(int id, int ownerID) {
		this.id = id;
		this.ownerID = ownerID;
		bowed = false;
		faceUp = false;
		dishonored = false;
	}
}
