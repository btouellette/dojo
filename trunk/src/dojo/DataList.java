package dojo;

import java.util.ArrayList;

/**
 * Creates and maintains search list for Deckbuilder
 * 
 * @author Solus. Created Aug 27, 2010.
 */
class DataList extends ArrayList<StoredCard>
{
	private static final long serialVersionUID = 1L;
	Object[] p;

	public DataList()
	{
		super();
		p = Main.databaseID.keySet().toArray();
		resetList();
	}

	private void resetList()
	{
		clear();
		for (int x = 0; x < p.length; x++)
			add(Main.databaseID.get(p[x]));
	}

	public void filterList(String legal, String clan, String type, String title, String text, String goldMin, String goldMax, String forceMin, String forceMax, String chiMin, String chiMax, String honorMin, String honorMax, String phonorMin, String phonorMax, String focusMin, String focusMax)
	{
		int max, index;

		resetList();

		max = size();
		index = 0;

		do {
			try {
				if (!get(index).getLegal().contains(legal))
					remove(index);
				else if (!type.equals("") && !get(index).getType().equals(type))
					remove(index);
				else if (!clan.equals("") && !get(index).getClan().contains(clan))
					remove(index);
				else if (get(index).getName().toLowerCase().indexOf(title) < 0)
					remove(index);
				else if (get(index).getText().toLowerCase().indexOf(text) < 0)
					remove(index);
				else if (checkDigit(goldMin) && (!checkDigit(get(index).getCost()) || Integer.parseInt(get(index).getCost()) < Integer.parseInt(goldMin)))
					remove(index);
				else if (checkDigit(goldMax) && (!checkDigit(get(index).getCost()) || Integer.parseInt(get(index).getCost()) > Integer.parseInt(goldMax)))
					remove(index);
				else if (checkDigit(forceMin) && (!checkDigit(get(index).getForce()) || Integer.parseInt(get(index).getForce()) < Integer.parseInt(forceMin)))
					remove(index);
				else if (checkDigit(forceMax) && (!checkDigit(get(index).getForce()) || Integer.parseInt(get(index).getForce()) > Integer.parseInt(forceMax)))
					remove(index);
				else if (checkDigit(chiMin) && (!checkDigit(get(index).getChi()) || Integer.parseInt(get(index).getChi()) < Integer.parseInt(chiMin)))
					remove(index);
				else if (checkDigit(chiMax) && (!checkDigit(get(index).getChi()) || Integer.parseInt(get(index).getChi()) > Integer.parseInt(chiMax)))
					remove(index);
				else if (checkDigit(honorMin) && (!checkDigit(get(index).getHonorReq()) || Integer.parseInt(get(index).getHonorReq()) < Integer.parseInt(honorMin)))
					remove(index);
				else if (checkDigit(honorMax) && (!checkDigit(get(index).getHonorReq()) || Integer.parseInt(get(index).getHonorReq()) > Integer.parseInt(honorMax)))
					remove(index);
				else if (checkDigit(phonorMin) && (!checkDigit(get(index).getPersonalHonor()) || Integer.parseInt(get(index).getPersonalHonor()) < Integer.parseInt(phonorMin)))
					remove(index);
				else if (checkDigit(phonorMax) && (!checkDigit(get(index).getPersonalHonor()) || Integer.parseInt(get(index).getPersonalHonor()) > Integer.parseInt(phonorMax)))
					remove(index);
				else if (checkDigit(focusMin) && (!checkDigit(get(index).getFocus()) || Integer.parseInt(get(index).getFocus()) < Integer.parseInt(focusMin)))
					remove(index);
				else if (checkDigit(focusMax) && (!checkDigit(get(index).getFocus()) || Integer.parseInt(get(index).getFocus()) > Integer.parseInt(focusMax)))
					remove(index);
				else
					index++;
			} catch (NumberFormatException e) {
			}

			max = size();
		} while (index < max);
	}

	public StoredCard getCard(int index)
	{
		return get(index);
	}

	private boolean checkDigit(String val)
	{
		if (val == null)
			return false;
		else if (val.length() == 0)
			return false;
		for (int x = 0; x < val.length(); x++)
			if (!Character.isDigit(val.charAt(0)))
				return false;
		return true;
	}
}
