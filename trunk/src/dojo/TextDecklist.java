package dojo;

import java.util.ArrayList;

import javax.swing.JTextArea;

/**
 * TODO Put here a description of what this class does.
 *
 * @author Solus.
 *         Created Aug 27, 2010.
 */
public class TextDecklist extends JTextArea
{
	private static final long serialVersionUID = 1L;
	
	public TextDecklist()
	{
		super();
	}
	public void setText(ArrayList<StoredCard> dyn, ArrayList<StoredCard> fate)
	{
		String newline = "\n";
		String tab = "    ";
		String[] dynTypes = {"Events", "Holdings", "Personalities", "Regions"};
		String[] fateTypes = {"Actions", "Ancestors", "Followers", "Items", 
							  "Rings", "Senseis", "Spells", "Winds"};
		String shName = "";
		int counter, a = 1;

		setText("");

		for (int x = 0; x < dynTypes.length; x++)
		{
			counter=0;
			for (int i = 0; i < dyn.size();i++)
			{
				if (dyn.get(i).getType().equalsIgnoreCase(dynTypes[x]))
					counter++;
				if (dyn.get(i).getType().equalsIgnoreCase("Strongholds"))
					shName = dyn.get(i).getName();
			}

			if (x==0)
			{
				append("Stronghold: " + shName + newline + newline);
				append("Dynasty (" + dyn.size() + ")" + newline);
			}

			if (counter>0)
				append(tab + dynTypes[x] + " (" + counter + "):" + newline);

			for (int y = 0; y < dyn.size(); y++)
			{
				if(dyn.get(y).getType().equalsIgnoreCase(dynTypes[x]))
				{
					while(y + 1 < dyn.size())
					{
					if (dyn.get(y).getName().equals(dyn.get(y + 1).getName()))
						{
							a++;
							y++;
						}
						else
							break;
					}

					append(tab + tab + (a + "x " + dyn.get(y).getName())+ newline);
					a = 1;
				}
			}

		}
		append(newline);

		for (int x = 0; x < fateTypes.length; x++)
		{
			counter=0;
			for (int i = 0; i < fate.size();i++)
			{
				if (fate.get(i).getType().equalsIgnoreCase(fateTypes[x]))
					counter++;
			}

			if (x==0)
				append("Fate (" + fate.size() + ")" + newline);

			if (counter>0)
				append(tab + fateTypes[x] + " (" + counter + "):" + newline);

			for (int y = 0; y < fate.size(); y++)
			{
				if(fate.get(y).getType().equalsIgnoreCase(fateTypes[x]))
				{
					while(y + 1 < fate.size())
					{
					if (fate.get(y).getName().equals(fate.get(y+1).getName()))
						{
							a++;
							y++;
						}
						else
							break;
					}

					append(tab + tab + (a + "x " + fate.get(y).getName())+ newline);
					a = 1;
				}
			}
		}
	}
}
