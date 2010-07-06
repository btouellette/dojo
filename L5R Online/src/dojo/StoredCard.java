package dojo;
// StoredCard.java
// Written by Brian Ouellette
// Part of Dojo
// Format in which cards are stored in the database after being read in from XML.

import java.util.ArrayList;
import java.io.File;

class StoredCard
{
	//Info
	private String id, type;
	private String name;
	private ArrayList<String> imageLocation, imageEdition;
	private ArrayList<String> legal, clan;
	private String edition, text, cost, focus;
	private String provinceStrength, goldProduction, startingHonor;
	private String force, chi, personalHonor, honorReq;

	public StoredCard(String id)
	{
		this.id = id;
		imageLocation = new ArrayList<String>();
		imageEdition = new ArrayList<String>();
		legal = new ArrayList<String>();
		clan = new ArrayList<String>();
		//Avoid null values in the text field (it doesn't have to be set)
		text = "";
		//Wait for the card to be filled with info from the parser
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setImageLocation(String imageLocation)
	{
		//Always add it at the beginning of the list
		//MRP will be the last card and we want that to be the default return
		this.imageLocation.add(imageLocation);
	}

	public void setImageEdition(String imageEdition)
	{
		//Always add it at the beginning of the list
		//MRP will be the last card and we want that to be the default return
		this.imageEdition.add(imageEdition);
	}

	public void setEdition(String edition)
	{
		this.edition = edition;
	}

	public void setLegal(String legal)
	{
		this.legal.add(legal);
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public void setCost(String cost)
	{
		this.cost = cost;
	}

	public void setFocus(String focus)
	{
		this.focus = focus;
	}

	public void setClan(String clan)
	{
		this.clan.add(clan);
	}

	public void setProvinceStrength(String provinceStrength)
	{
		this.provinceStrength = provinceStrength;
	}

	public void setGoldProduction(String goldProduction)
	{
		this.goldProduction = goldProduction;
	}

	public void setStartingHonor(String startingHonor)
	{
		this.startingHonor = startingHonor;
	}

	public void setForce(String force)
	{
		this.force = force;
	}

	public void setChi(String chi)
	{
		this.chi = chi;
	}

	public void setPersonalHonor(String personalHonor)
	{
		this.personalHonor = personalHonor;
	}

	public void setHonorReq(String honorReq)
	{
		this.honorReq = honorReq;
	}

	public String getID()
	{
		return id;
	}

	public String getType()
	{
		return type;
	}

	public String getName()
	{
		return name;
	}

	public String getImageLocation()
	{
		String cardImageLoc;
		int count = 0;

		//Find the first valid image location
		while(count < imageLocation.size())
		{
			cardImageLoc = imageLocation.get(count);
			if((new File(cardImageLoc)).exists())
				return cardImageLoc;
			count++;
		}

		return null;
	}

	public String getImageEdition()
	{
		if(!imageEdition.isEmpty())
		{
			// Just return the MRP edition
			return imageEdition.get(0);
		}

		return null;
	}

	public String getEdition()
	{
		return edition;
	}

	public String getText()
	{
		return text;
	}

	public String getCost()
	{
		return cost;
	}

	public String getFocus()
	{
		return focus;
	}

	public ArrayList<String> getClan()
	{
		return clan;
	}

	public String getProvinceStrength()
	{
		return provinceStrength;
	}

	public String getGoldProduction()
	{
		return goldProduction;
	}

	public String getStartingHonor()
	{
		return startingHonor;
	}

	public String getForce()
	{
		return force;
	}

	public String getChi()
	{
		return chi;
	}

	public String getPersonalHonor()
	{
		return personalHonor;
	}

	public String getHonorReq()
	{
		return honorReq;
	}

	public String toString()
	{
		return name;
	}
	public ArrayList<String> getLegal()
	{
		return legal;
	}
}
