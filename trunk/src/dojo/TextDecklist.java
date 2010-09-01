package dojo;

import java.util.ArrayList;
import java.util.Collections;

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
		String shName = "";
		int counter, num = 1;

		ArrayList<String> dynTypes = new ArrayList<String>();
		ArrayList<String> fateTypes = new ArrayList<String>();
		ArrayList<String> dynMod = new ArrayList<String>();
		ArrayList<String> fateMod = new ArrayList<String>();
		
		setText("");		
		
		//Create the dynasty types and set the Stronghold if it is known
		for ( int x = 0; x < dyn.size(); x++)
		{
			StoredCard currentCard = dyn.get(x);
			
			if(!dynTypes.contains(currentCard.getType()))
					dynTypes.add(currentCard.getType());
			
			if (dyn.get(x).getType().equals("stronghold"))
				shName = dyn.get(x).getName();
		}
		
		//Create the fate types
		for (int x = 0; x < fate.size(); x++)
		{
			StoredCard currentCard = fate.get(x);
			if(!fateTypes.contains(currentCard.getType()))
				fateTypes.add(currentCard.getType());
		}

		Collections.sort(dynTypes);
		Collections.sort(fateTypes);
		
		//Clean up dyn types, add capitalization and word fixes, and store to new list
		for(int i = 0; i < dynTypes.size(); i++)
		{
			String temp = dynTypes.get(i).substring(0,1).toUpperCase() + dynTypes.get(i).substring(1);
			
			if (dynTypes.get(i).charAt(dynTypes.get(i).length()-1) == 'y')
				temp = temp.substring(0,temp.length() - 1) + "ies";
			else
				temp = temp + "s";
			dynMod.add(temp);
		}
		
		//Clean up fate types, add capitalization and word fixes, and store to new list
		for(int i = 0; i < fateTypes.size(); i++)
		{
			String temp = fateTypes.get(i).substring(0,1).toUpperCase() + fateTypes.get(i).substring(1);
			
			if (temp.equals("Action"))
				fateMod.add("Strategies");
			else
			{
				if (fateTypes.get(i).charAt(fateTypes.get(i).length()-1) == 'y')
					temp = temp.substring(0,temp.length() - 1) + "ies";
				else
					temp = temp + "s";
				fateMod.add(temp);
			}
		}
		
		append("Stronghold: " + shName + newline + newline);
		append("Dynasty (" + dyn.size() + ")" + newline);
		
		for (int x = 0; x < dynTypes.size(); x++)
		{
			//The first part adds the types to the text area
			counter = 0;
			
			for (int i = 0; i < dyn.size();i++)
				if (dyn.get(i).getType().equals(dynTypes.get(x)))
					counter++;

			if (counter>0)
				append(tab + dynMod.get(x) + " (" + counter + "):" + newline);
			
			//Then adds the actual cards to the area
			for (int y = 0; y < dyn.size(); y++)
			{
				if(dyn.get(y).getType().equals(dynTypes.get(x)))
				{
					//This counts the number of same cards before printing that amount
					while(y + 1 < dyn.size())
					{
					if (dyn.get(y).getName().equals(dyn.get(y + 1).getName()))
						{
							num++;
							y++;
						}
						else
							break;
					}

					append(tab + tab + (num + "x " + dyn.get(y).getName())+ newline);
					num = 1;
				}
			}
		}
		
		append(newline);
		append("Fate (" + fate.size() + ")" + newline);

		for (int x = 0; x < fateTypes.size(); x++)
		{
			//The first part adds the types to the text area
			counter = 0;
			
			for (int i = 0; i < fate.size();i++)
				if (fate.get(i).getType().equals(fateTypes.get(x)))
					counter++;
			
			if (counter > 0)
				append(tab + fateMod.get(x) + " (" + counter + "):" + newline);

			//Then adds the actual cards to the area
			for (int y = 0; y < fate.size(); y++)
			{
				if(fate.get(y).getType().equalsIgnoreCase(fateTypes.get(x)))
				{
					//This counts the number of same cards before printing that amount
					while(y + 1 < fate.size())
					{
					if (fate.get(y).getName().equals(fate.get(y+1).getName()))
						{
							num++;
							y++;
						}
						else
							break;
					}

					append(tab + tab + (num + "x " + fate.get(y).getName())+ newline);
					num = 1;
				}
			}
		}
	}
}
