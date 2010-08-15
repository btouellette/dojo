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
		String path = file.getAbsolutePath();
		// Grab the file extension
		String fileType = path.substring(path.length()-4);
		List<StoredCard> cards = new ArrayList<StoredCard>(100);
		try {
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
		// If import was successful create decks
		if(cards != null && !cards.isEmpty())
		{
			Deck dynasty = new Deck(true);
			Deck fate = new Deck(false);
			PlayableCard wind = null, stronghold = null;
			for(StoredCard currentCard : cards)
			{
				String type = currentCard.getType();
				if(type.equals("winds"))
				{
					wind = new PlayableCard(currentCard);
					PlayArea.displayedCards.add(wind);
					if(stronghold != null)
					{
						stronghold.attach(wind);
					}
				}
				if(type.equals("strongholds"))
				{
					stronghold = new PlayableCard(currentCard);
					PlayArea.displayedCards.add(stronghold);
					stronghold.setLocationSimple(PlayArea.cardWidth, PlayArea.height - PlayArea.cardHeight - 10);
					if(wind != null)
					{
						stronghold.attach(wind);
					}
					//TODO: If you find a sensei prompt to pull one out of your deck and automatically attach to stronghold (check faction)
				}
				else if(currentCard.isDynasty())
				{
					dynasty.add(currentCard);
				}
				else
				{
					fate.add(currentCard);
				}
			}
			// Shuffle decks and load them into the play area
			TextActionListener.send(Preferences.userName + " loads a new deck.", "Action");
			fate.shuffle();
			dynasty.shuffle();
		
			Main.playArea.clearArea(dynasty, fate);
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
