// CardInfoBox.java
// Written by Brian Ouellette
// Part of Dojo
// Box for showing the current selected cards information.

//TODO: Add a JPopUpMenu for detaching the panel

import javax.swing.*;

class CardInfoBox extends JEditorPane
{
	private static final long serialVersionUID = 1L;
	StoredCard card;

	public CardInfoBox()
	{
		setEditable(false);
		setContentType("text/html");
	}

	public CardInfoBox(StoredCard card)
	{
		this();
		setCard(card);
	}

	public void setCard(StoredCard card)
	{
		String cardHTML1, cardHTML2, cardHTML3, cardHTML4;
		
		/* Commented out until it's determined whether images should be present in the info box
		String cardImageLoc = card.getImageLocation();

		//Hack to get a non-relative file location as JEditorPane doesn't support relative pathnames in IMG tags
		//If used then <img src=\"" + cardImageLoc + "\"> is added to the HTML
		if(cardImageLoc != null)
		{
			File temp = new File(cardImageLoc);
			cardImageLoc = "file:" + temp.getAbsolutePath();
		}
		*/

		cardHTML1 = "";
		cardHTML2 = "";
		cardHTML3 = "";
		cardHTML4 = "";

		//TODO: Deal with null values in fields like text properly
		if(card.getType().equals("actions") || card.getType().equals("kihos") || card.getType().equals("spells"))
		{
			//The card text in the window is created as an html table
			//Complete sytnax isn't necessary as setText() reformats it
			cardHTML1 = "<center><table><tr><td colspan=\"5\" align=center><b>" + card.getName() + "</b></td></tr>";
			cardHTML2 = "<tr><td colspan = \"5\" align=center>" + card.getCost() + " G</td><td></td></tr>";
			cardHTML3 = "<tr><td colspan = \"5\">" + card.getText() + "</td></tr>";
			cardHTML4 = "<tr><td colspan = \"5\" align=center>" + card.getFocus() + " Focus</td></tr></table></center>";
		}
		//TODO: Use String.replaceAll and regexp to insert &middot instead of . in the traits section
		else if(card.getType().equals("ancestors") || card.getType().equals("followers"))
		{
			cardHTML1 = "<center><table><tr><td align=center>" + card.getForce() + "F</td><td colspan=\"3\" align=center><b>" + card.getName() + "</b></td><td align=center>" + card.getChi() + "C</td></tr>";
			cardHTML2 = "<tr><td></td><td colspan = \"3\" align=center>" + card.getHonorReq() + " / " + card.getCost() + " / " + card.getPersonalHonor() + "</td><td></td></tr>";
			cardHTML3 = "<tr><td></td><td colspan = \"3\" align=center>HR / GC / PH</td><td></td></tr>";
			cardHTML4 = "<tr><td colspan = \"5\">" + card.getText() + "</td></tr><tr><td colspan = \"5\" align=center>" + card.getFocus() + " Focus</td></tr></table></center>";
		}
		else if(card.getType().equals("events") || card.getType().equals("regions") || card.getType().equals("winds"))
		{
			cardHTML1 = "<center><table><tr><td colspan=\"5\" align=center><b>" + card.getName() + "</b></td></tr>";
			cardHTML2 = "<tr><td colspan = \"5\">" + card.getText() + "</td></tr></table></center>";
		}
		else if(card.getType().equals("holdings"))
		{
			cardHTML1 = "<center><table><tr><td colspan=\"5\" align=center><b>" + card.getName() + "</b></td></tr>";
			cardHTML2 = "<tr><td colspan = \"5\" align=center>" + card.getCost() + " G</td><td></td></tr>";
			cardHTML3 = "<tr><td colspan = \"5\">" + card.getText() + "</td></tr></table></center>";
		}
		else if(card.getType().equals("items"))
		{
			cardHTML1 = "<center><table><tr><td><center>" + card.getForce() + "F</center></td><td colspan=\"3\" align=center><b>" + card.getName() + "</b></td><td align=center>" + card.getChi() + "C</td></tr>";
			cardHTML2 = "<tr><td></td><td colspan = \"3\" align=center>" + card.getCost() + " G</td><td></td></tr>";
			cardHTML3 = "<tr><td></td><td colspan = \"5\">" + card.getText() + "</td><td></td></tr>";
			cardHTML4 = "<tr><td colspan = \"5\" align=center>" + card.getFocus() + " Focus</td></tr></table></center>";
		}
		else if(card.getType().equals("personalities"))
		{
			cardHTML1 = "<center><table><tr><td align=center>" + card.getForce() + "F</center></td><td colspan=\"3\"><center><b>" + card.getName() + "</b></td><td align=center>" + card.getChi() + "C</td></tr>";
			cardHTML2 = "<tr><td></td><td colspan = \"3\" align=center>" + card.getHonorReq() + " / " + card.getCost() + " / " + card.getPersonalHonor() + "</td><td></td></tr>";
			cardHTML3 = "<tr><td></td><td colspan = \"3\" align=center>HR / GC / PH</td><td></td></tr>";
			cardHTML4 = "<tr><td colspan = \"5\">" + card.getText() + "</td></tr></table></center>";
		}
		else if(card.getType().equals("rings"))
		{
			cardHTML1 = "<center><table><tr><td colspan=\"5\" align=center><b>" + card.getName() + "</b></td></tr>";
			cardHTML2 = "<tr><td colspan = \"5\">" + card.getText() + "</td></tr>";
			cardHTML3 = "<tr><td colspan = \"5\" align=center>" + card.getFocus() + " Focus</td></tr></table></center>";
		}
		else if(card.getType().equals("senseis") || card.getType().equals("strongholds"))
		{
			cardHTML1 = "<center><table><tr><td colspan=\"5\" align=center><b>" + card.getName() + "</b></td></tr>";
			cardHTML2 = "<tr><td colspan = \"4\" align=right>Province Strength: </td><td colspan = \"2\" align=center>" + card.getProvinceStrength() + "</td></tr>";
			cardHTML3 = "<tr><td colspan = \"4\" align=right>Gold Production: </td><td colspan = \"2\" align=center>" + card.getGoldProduction() + "</td></tr>";
			cardHTML3 = cardHTML3 + "<tr><td colspan = \"4\" align=right>Starting Honor: </td><td colspan = \"2\" align=center>" + card.getStartingHonor() + "</td></tr>";
			cardHTML4 = "<tr><td colspan = \"5\">" + card.getText() + "</td></tr></center>";
		}

		//Set the card window to show the html table for the card
		setText(cardHTML1 + cardHTML2 + cardHTML3 + cardHTML4);
		//Scroll the window back up to the top
		setCaretPosition(0);
	}
}
