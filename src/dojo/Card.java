package dojo;

public class Card {
	// Unique identifier for this card (cgid in Egg terminology)
	int id;
	boolean bowed, faceUp, dishonored;
	public enum Location { Table, DynastyDeck, FateDeck, DynastyDiscard, FateDiscard, Hand, RemovedFromGame, FocusPool }	
	// Where the card is
	Location location;
	// PlayerID of the owner of this card
	int ownerPlayerID;
	
	public Card(int id) {
		this.id = id;
		bowed = false;
		faceUp = false;
		dishonored = false;
	}
}
