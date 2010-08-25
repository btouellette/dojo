package dojo;
// StoredCard.java
// Written by Brian Ouellette
// Format in which cards are stored in the database after being read in from XML.
// Use PlayableCard for anything outside the database and decks

import java.util.List;
import java.util.ArrayList;
import java.io.File;

class StoredCard extends Card
{
	private String type;
	private String name;
	//TODO: Update to only be MRP in new XML type
	private List<String> imageLocation, imageEdition;
	private List<String> legal, clan, rulings;
	private String edition, text, cost, focus;
	private String provinceStrength, goldProduction, startingHonor;
	private String force, chi, personalHonor, honorReq;
	private String rarity, flavor, artist;

	public StoredCard(String id)
	{
		super(id);
		imageLocation = new ArrayList<String>();
		imageEdition = new ArrayList<String>();
		legal = new ArrayList<String>();
		clan = new ArrayList<String>();
		rulings = new ArrayList<String>();
		//Avoid null values in the text field (it isn't guaranteed present in cards.xml)
		text = "";
	}

	public void setType(String type)
	{
		this.type = type;
		// Set appropriate dynasty field
		if(type.equals("strategy") || type.equals("kiho")     || type.equals("spell") ||
		   type.equals("ancestor") || type.equals("follower") || type.equals("item")  ||
		   type.equals("ring")     || type.equals("sensei")   || type.equals("wind"))
		{
			isDynasty = false;
		}
		else
		{
			// True for: celestials, events, regions, holdings, personalities, strongholds
			isDynasty = true;
		}
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setImageLocation(String imageLocation)
	{
		//Always add it at the end of the list
		//MRP will be the last card and we want that in the first position
		this.imageLocation.add(imageLocation);
	}

	public void setImageEdition(String imageEdition)
	{
		//Always add it at the end of the list
		//MRP will be the last card and we want that in the first position
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

		// Find the first valid image location
		while(count < imageLocation.size())
		{
			cardImageLoc = imageLocation.get(count);
			// Only return it if the file exists
			if((new File(cardImageLoc)).exists())
			{
				return cardImageLoc;
			}
			count++;
		}
		return null;
	}

	//TODO: Make this smarter. Try to download multiple editions if possible
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

	public List<String> getClan()
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
	
	public List<String> getLegal()
	{
		return legal;
	}

	public void setRarity(String rarity)
	{
		this.rarity = rarity;
	}

	public String getRarity()
	{
		return rarity;
	}

	public void setFlavor(String flavor)
	{
		this.flavor = flavor;
	}

	public String getFlavor()
	{
		return flavor;
	}

	public void setArtist(String artist)
	{
		this.artist = artist;
	}

	public String getArtist()
	{
		return artist;
	}

	public void setRulings(String rulings)
	{
		this.rulings.add(rulings);
	}

	public List<String> getRulings()
	{
		return rulings;
	}
}
