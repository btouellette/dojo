package dojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JTextArea;

/**
 * TODO Put here a description of what this class does.
 * 
 * @author Solus. Created Aug 27, 2010.
 */
class TextDecklist extends JTextArea
{
	private static final long serialVersionUID = 1L;

	public TextDecklist()
	{
		super();
	}

	public void setText(List<StoredCard> dynDeck, List<StoredCard> fateDeck)
	{
		String newline = "\n";
		String tab = "    ";
		String shName = "";
		int counter, num = 1;

		List<String> dynTypes = new ArrayList<String>();
		List<String> fateTypes = new ArrayList<String>();
		List<String> dynMod = new ArrayList<String>();
		List<String> fateMod = new ArrayList<String>();

		setText("");

		// Create the dynasty types and set the Stronghold if it is known
		for (int x = 0; x < dynDeck.size(); x++) {
			StoredCard currentCard = dynDeck.get(x);

			if (!dynTypes.contains(currentCard.getType()))
				if (!currentCard.getType().equals("stronghold"))
					dynTypes.add(currentCard.getType());

			if (dynDeck.get(x).getType().equals("stronghold"))
				shName = dynDeck.get(x).getName();
		}

		// Create the fate types
		for (int x = 0; x < fateDeck.size(); x++) {
			StoredCard currentCard = fateDeck.get(x);
			if (!fateTypes.contains(currentCard.getType()))
				fateTypes.add(currentCard.getType());
		}

		Collections.sort(dynTypes);
		Collections.sort(fateTypes);

		// Clean up dyn types, add capitalization and word fixes, and store to new list
		for (int i = 0; i < dynTypes.size(); i++) {
			String temp = dynTypes.get(i).substring(0, 1).toUpperCase() + dynTypes.get(i).substring(1);

			if (dynTypes.get(i).charAt(dynTypes.get(i).length() - 1) == 'y')
				temp = temp.substring(0, temp.length() - 1) + "ies";
			else
				temp = temp + "s";
			dynMod.add(temp);
		}

		// Clean up fate types, add capitalization and word fixes, and store to new list
		for (int i = 0; i < fateTypes.size(); i++) {
			String temp = fateTypes.get(i).substring(0, 1).toUpperCase() + fateTypes.get(i).substring(1);

			if (temp.equals("Action"))
				fateMod.add("Strategies");
			else {
				if (fateTypes.get(i).charAt(fateTypes.get(i).length() - 1) == 'y')
					temp = temp.substring(0, temp.length() - 1) + "ies";
				else
					temp = temp + "s";
				fateMod.add(temp);
			}
		}

		append("Stronghold: " + shName + newline + newline);
		int size = (shName == "") ? (dynDeck.size()) : (dynDeck.size() - 1);
		append("Dynasty (" + size + ")" + newline);

		for (int x = 0; x < dynTypes.size(); x++) {
			// The first part adds the types to the text area
			counter = 0;

			for (int i = 0; i < dynDeck.size(); i++)
				if (dynDeck.get(i).getType().equals(dynTypes.get(x)))
					counter++;

			if (counter > 0)
				append(tab + dynMod.get(x) + " (" + counter + ")" + newline);

			// Then adds the actual cards to the area
			for (int y = 0; y < dynDeck.size(); y++) {
				if (dynDeck.get(y).getType().equals(dynTypes.get(x))) {
					// This counts the number of same cards before printing that amount
					while (y + 1 < dynDeck.size()) {
						if (dynDeck.get(y).getName().equals(dynDeck.get(y + 1).getName())) {
							num++;
							y++;
						} else
							break;
					}

					append(tab + tab + (num + "x " + dynDeck.get(y).getName()) + newline);
					num = 1;
				}
			}
		}

		append(newline);
		append("Fate (" + fateDeck.size() + ")" + newline);

		for (int x = 0; x < fateTypes.size(); x++) {
			// The first part adds the types to the text area
			counter = 0;

			for (int i = 0; i < fateDeck.size(); i++)
				if (fateDeck.get(i).getType().equals(fateTypes.get(x)))
					counter++;

			if (counter > 0)
				append(tab + fateMod.get(x) + " (" + counter + ")" + newline);

			// Then adds the actual cards to the area
			for (int y = 0; y < fateDeck.size(); y++) {
				if (fateDeck.get(y).getType().equalsIgnoreCase(fateTypes.get(x))) {
					// This counts the number of same cards before printing that amount
					while (y + 1 < fateDeck.size()) {
						if (fateDeck.get(y).getName().equals(fateDeck.get(y + 1).getName())) {
							num++;
							y++;
						} else
							break;
					}

					append(tab + tab + (num + "x " + fateDeck.get(y).getName()) + newline);
					num = 1;
				}
			}
		}
	}
}
