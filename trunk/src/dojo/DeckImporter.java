package dojo;
// DeckImporter.java
// Written by Brian Ouellette
// Imports both The Game/Gempukku style decks as well as Egg style

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

class DeckImporter
{
	public static void importDeck(File file)
	{
		// Grab the file path
		String path = file.getAbsolutePath();
		// So we can grab the file extension
		String fileType = path.substring(path.length()-4);
		// Default storage for 100 cards (includes both decks and stronghold/wind)
		List<StoredCard> cards = new ArrayList<StoredCard>(100);
		try {
			// Egg decks use *.l5d and Game and Gempukku decks use *.dck file extension
			BufferedReader br = new BufferedReader(new FileReader(path));
			if(fileType.equals(".l5d"))
			{
				cards = importEggStyle(br);
			}
			else if(fileType.equals(".dck"))
			{
				cards = importGameStyle(br);
			}
		} catch (IOException err) {
			TextActionListener.send("Failed to read in deck.\n", "Error");
			err.printStackTrace();
		}
		// If import was successful and we now have cards to work with create decks
		if(cards != null && !cards.isEmpty())
		{
			// Make two new decks to sort into
			Deck dynasty = new Deck(true);
			Deck fate = new Deck(false);
			// Treat strongholds and winds specially
			PlayableCard wind = null, stronghold = null;
			// For every card we imported
			for(StoredCard currentCard : cards)
			{
				// Check the type
				String type = currentCard.getType();
				// If the card is a wind put it on the table instead of in a deck and if there is a stronghold on the table attach the wind to it
				if(type.equals("wind"))
				{
					wind = new PlayableCard(currentCard);
					wind.setFaceUp();
				}
				// If the card is a stronghold put it on the table in the lower left and attach any found wind to it 
				else if(type.equals("stronghold"))
				{
					stronghold = new PlayableCard(currentCard);
					stronghold.setFaceUp();
					stronghold.setLocationSimple(Main.playArea.getCardWidth(), Main.playArea.getHeight() - 2*Main.playArea.getCardHeight() - 20);	
				}
				// If it isn't a special type of card just add it to the appropriate deck
				else if(currentCard.isDynasty())
				{
					dynasty.add(currentCard);
				}
				else
				{
					fate.add(currentCard);
				}
			}
			// Shuffle decks and load them into the current game state
			TextActionListener.send(Preferences.userName + " loads a new deck.", "Action");
			fate.shuffle();
			dynasty.shuffle();
		
			Main.state.resetState(dynasty, fate);
			// After clearing the table put out the stronghold and wind
			if(stronghold != null)
			{
				Main.state.addToTable(stronghold);
				if(wind != null)
				{
					stronghold.attach(wind);
				}
				//TODO: If you find a sensei prompt to pull one out of your deck and automatically attach to stronghold (check faction)
			}
			Main.playArea.redrawBackground();
			Main.playArea.repaint();
		}
	}

	private static List<StoredCard> importEggStyle(BufferedReader br) throws IOException
	{
		List<StoredCard> cards = new ArrayList<StoredCard>(100);
		String line;
		// Iterate over the entire file
		while((line = br.readLine()) != null)
		{
			// As long as the line isn't blank or commented out
			if(!line.isEmpty() && line.charAt(0) != '#')
			{
				int count = 0, num = 0;
				// First grab the number of cards
				while(Character.isDigit(line.charAt(count)))
				{
					num *= 10;
					num += Character.getNumericValue(line.charAt(count));
					count++;
				}
				// Then the card itself
				String cardName = line.substring(count+1);
				// And add the correct number of copies
				StoredCard currentCard = Main.databaseName.get(cardName);
				for(int i = 0; i < num; i++)
				{
					cards.add(currentCard);
				}
			}
		}
		return cards;
	}

	private static List<StoredCard> importGameStyle(BufferedReader br) throws IOException
	{
		// Game-style decks are only one line of card ID's separated by '|'s
		// Pull the string in and tokenize it
		List<StoredCard> cards = new ArrayList<StoredCard>(100);
		String deck = br.readLine();
		StringTokenizer st = new StringTokenizer(deck, "|");
		while(st.hasMoreTokens())
		{
			cards.add(Main.databaseID.get(st.nextToken()));
		}
		return cards;
	}
}
