package dojo;
// DeckImporter.java
// Written by Brian Ouellette
// Imports both The Game/Gempukku style decks as well as Egg style

import java.io.*;
import java.util.ArrayList;
import java.util.List;

class DeckImporter
{
	public static void importDeck(File file)
	{
		String path = file.getAbsolutePath();
		// Grab the file extension
		String fileType = path.substring(path.length()-4);
		List<String> cards = new ArrayList<String>(100);
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

			//TODO: See about replacing this with a legit iterator
			for(int i = 0; i < cards.size(); i++)
			{
				StoredCard currentCard = Main.databaseName.get(cards.get(i));
				String type = currentCard.getType();
				if(type.equals("winds") || type.equals("strongholds"))
				{
					//TODO: Set location appropriately
					PlayArea.displayedCards.add(new PlayableCard(currentCard));
					//TODO: Decide whether to handle multiple strongholds/winds in a deck specially or not
					//TODO: If you find a sensei prompt to pull one out of your deck and automatically attach to stronghold (check faction), attach wind to stronghold in this same check
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
			TextActionListener.send(Main.userName + " loads a new deck.", "Action");
			fate.shuffle();
			dynasty.shuffle();
		
			Main.playArea.clearArea(dynasty, fate);
		}
	}

	private static List<String> importEggStyle(BufferedReader br) throws IOException
	{
		List<String> cards = new ArrayList<String>(100);
		String line;
		// Iterate over the entire file
		while ((line = br.readLine()) != null)
		{
			// As long as the line isn't blank or commented out
			if(!line.isEmpty() && line.charAt(0) != '#')
			{
				int count = 0;
				int num = 0;
				// First grab the number of cards
				while(Character.isDigit(line.charAt(count)))
				{
					num *= 10;
					num += Character.getNumericValue(line.charAt(count));;
					count++;
				}
				// Then the card itself
				String cardName = line.substring(count+1);
				// And add the correct number of copies
				for(int i = 0; i < num; i++)
				{
					cards.add(cardName);
				}
			}
		}
		return cards;
	}

	private static List<String> importGameStyle(BufferedReader br) throws IOException
	{
		List<String> cards = new ArrayList<String>(100);
		return cards;
	}
}
