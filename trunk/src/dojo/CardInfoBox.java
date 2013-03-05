package dojo;

// CardInfoBox.java
// Written by Brian Ouellette
// Box for showing the current selected cards information.

import javax.swing.*;

// TODO: Add a JPopUpMenu for detaching the panel
// TODO: Try to get this to handle resizing better (redraw table or use CSS to set width dynamically)
class CardInfoBox extends JEditorPane
{
	private static final long serialVersionUID = 1L;
	private StoredCard card;

	public CardInfoBox()
	{
		super();
		// Don't allow user to edit info box
		setEditable(false);
		// Using html tables to display card so set appropriate type
		setContentType("text/html");
	}

	public CardInfoBox(StoredCard card)
	{
		// Creates the info box with a card already loaded into it
		this();
		setCard(card);
	}

	public void setCard(StoredCard card)
	{
		// Only recreate table if card changes
		if (this.card != card) {
			this.card = card;
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

			int width = getWidth() - 30;
			String type = card.getType();
			String cardHTML = "";
			// HTML tables created differently for different types of cards
			if (type.equals("strategy") || type.equals("kiho") || type.equals("spell")) {
				// The card text in the window is created as an html table
				// Complete sytnax isn't necessary as setText() reformats it
				cardHTML = "<center><table width=" + width + "><tr><td colspan=\"5\" align=center><b>" + card.getName() + "</b></td></tr>" + "<tr><td colspan = \"5\" align=center>" + card.getCost() + " G</td><td></td></tr>" + "<tr><td colspan = \"5\">" + card.getText() + "</td></tr>" + "<tr><td colspan = \"5\" align=center>" + card.getFocus() + " Focus</td></tr></table></center>";
			} else if (type.equals("ancestor") || type.equals("follower")) {
				cardHTML = "<center><table width=" + width + "><tr><td align=center>" + card.getForce() + "F</td><td colspan=\"3\" align=center><b>" + card.getName() + "</b></td><td align=center>" + card.getChi() + "C</td></tr>" + "<tr><td></td><td colspan = \"3\" align=center>" + card.getHonorReq() + " / " + card.getCost() + " / " + card.getPersonalHonor() + "</td><td></td></tr>" + "<tr><td></td><td colspan = \"3\" align=center>HR / GC / PH</td><td></td></tr>" + "<tr><td colspan = \"5\">" + card.getText() + "</td></tr><tr><td colspan = \"5\" align=center>" + card.getFocus() + " Focus</td></tr></table></center>";
			} else if (type.equals("celestial") || type.equals("event") || type.equals("region") || type.equals("wind")) {
				cardHTML = "<center><table width=" + width + "><tr><td colspan=\"5\" align=center><b>" + card.getName() + "</b></td></tr>" + "<tr><td colspan = \"5\">" + card.getText() + "</td></tr></table></center>";
			} else if (type.equals("holding")) {
				cardHTML = "<center><table width=" + width + "><tr><td colspan=\"5\" align=center><b>" + card.getName() + "</b></td></tr>" + "<tr><td colspan = \"5\" align=center>" + card.getCost() + " G</td><td></td></tr>" + "<tr><td colspan = \"5\">" + card.getText() + "</td></tr></table></center>";
			} else if (type.equals("item")) {
				cardHTML = "<center><table width=" + width + "><tr><td><center>" + card.getForce() + "F</center></td><td colspan=\"3\" align=center><b>" + card.getName() + "</b></td><td align=center>" + card.getChi() + "C</td></tr>" + "<tr><td></td><td colspan = \"3\" align=center>" + card.getCost() + " G</td><td></td></tr>" + "<tr><td></td><td colspan = \"5\">" + card.getText() + "</td><td></td></tr>" + "<tr><td colspan = \"5\" align=center>" + card.getFocus() + " Focus</td></tr></table></center>";
			} else if (type.equals("personality")) {
				cardHTML = "<center><table width=" + width + "><tr><td align=center>" + card.getForce() + "F</center></td><td colspan=\"3\"><center><b>" + card.getName() + "</b></td><td align=center>" + card.getChi() + "C</td></tr>" + "<tr><td></td><td colspan = \"3\" align=center>" + card.getHonorReq() + " / " + card.getCost() + " / " + card.getPersonalHonor() + "</td><td></td></tr>" + "<tr><td></td><td colspan = \"3\" align=center>HR / GC / PH</td><td></td></tr>" + "<tr><td colspan = \"5\">" + card.getText() + "</td></tr></table></center>";
			} else if (type.equals("ring")) {
				cardHTML = "<center><table width=" + width + "><tr><td colspan=\"5\" align=center><b>" + card.getName() + "</b></td></tr>" + "<tr><td colspan = \"5\">" + card.getText() + "</td></tr>" + "<tr><td colspan = \"5\" align=center>" + card.getFocus() + " Focus</td></tr></table></center>";
			} else if (type.equals("sensei") || type.equals("stronghold")) {
				cardHTML = "<center><table width=" + width + "><tr><td colspan=\"5\" align=center><b>" + card.getName() + "</b></td></tr>" + "<tr><td colspan = \"4\" align=right>Province Strength: </td><td colspan = \"2\" align=center>" + card.getProvinceStrength() + "</td></tr>" + "<tr><td colspan = \"4\" align=right>Gold Production: </td><td colspan = \"2\" align=center>" + card.getGoldProduction() + "</td></tr>" + "<tr><td colspan = \"4\" align=right>Starting Honor: </td><td colspan = \"2\" align=center>" + card.getStartingHonor() + "</td></tr>" + "<tr><td colspan = \"5\">" + card.getText() + "</td></tr></center>";
			}

			// Set the card window to show the html table for the card
			setText(cardHTML);
			// Scroll the window back up to the top
			setCaretPosition(0);
		}
	}
}
