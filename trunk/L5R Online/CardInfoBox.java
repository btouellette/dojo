// CardInfoBox.java
// Written by Brian Ouellette
// Part of Dojo

//TODO: Add a JPopUpMenu for detaching the panel

package l5r;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import java.io.*;

class CardInfoBox extends JEditorPane
{
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
		String cardHTML1, cardHTML2, cardHTML3, cardHTML4, cardImageLoc = "";
		boolean imageExists = false, moreImages = true;
		int count = 0;


		/* Commented out until it's determined whether images should be present in the info box
		//Find the first valid image location
		while(!imageExists && moreImages)
		{
			cardImageLoc = card.getImageLocation(count);
			if(cardImageLoc.equals(""))
			{
				moreImages = false;
			}
			else
			{
				imageExists = (new File(cardImageLoc)).exists();
			}
			count++;
		}

		//Hack to get a non-relative file location as JEditorPane doesn't support relative pathnames in IMG tags
		//If used then <img src=\"" + cardImageLoc + "\"> is added to the HTML
		if(imageExists)
		{
			File temp = new File(cardImageLoc);
			cardImageLoc = "file:" + temp.getAbsolutePath();
		}
		*/

		cardHTML1 = "";
		cardHTML2 = "";
		cardHTML3 = "";
		cardHTML4 = "";

		if(card.getType().equals("actions") || card.getType().equals("kihos"))
		{
			//The card text in the window is created as an html table
			//Complete sytnax isn't necessary as setText() reformats it
			cardHTML1 = "<center><table><tr><td colspan=\"5\"><center><b>" + card.getName() + "</b></center></td></tr>";
			cardHTML2 = "<tr><td colspan = \"5\"><center>" + card.getCost() + " G</center></td><td></td></tr>";
			cardHTML3 = "<tr><td colspan = \"5\">" + card.getText() + "</td></tr>";
			cardHTML4 = "<tr><td colspan = \"5\"><center>" + card.getFocus() + " Focus</center></td></tr></table></center>";
		}
		//TODO: Use String.replaceAll and regexp to insert &middot instead of . in the traits section
		else if(card.getType().equals("ancestors") || card.getType().equals("followers"))
		{
			cardHTML1 = "<center><table><tr><td><center>" + card.getForce() + "F</center></td><td colspan=\"3\"><center><b>" + card.getName() + "</b></center></td><td><center>" + card.getChi() + "C</center></td></tr>";
			cardHTML2 = "<tr><td></td><td colspan = \"3\"><center>" + card.getHonorReq() + " / " + card.getCost() + " / " + card.getPersonalHonor() + "</center></td><td></td></tr>";
			cardHTML3 = "<tr><td></td><td colspan = \"3\"><center>HR / GC / PH<center></td><td></td></tr>";
			cardHTML4 = "<tr><td colspan = \"5\">" + card.getText() + "</td></tr><tr><td colspan = \"5\"><center>" + card.getFocus() + " Focus</center></td></tr></table></center>";
		}
		else if(card.getType().equals("events") || card.getType().equals("regions") || card.getType().equals("winds"))
		{
			cardHTML1 = "<center><table><tr><td colspan=\"5\"><center><b>" + card.getName() + "</b></center></td></tr>";
			cardHTML2 = "<tr><td colspan = \"5\">" + card.getText() + "</td></tr></table>";
		}
		else if(card.getType().equals("holdings"))
		{
			cardHTML1 = "<center><table><tr><td colspan=\"5\"><center><b>" + card.getName() + "</b></center></td></tr>";
			cardHTML2 = "<tr><td colspan = \"5\"><center>" + card.getCost() + " G</center></td><td></td></tr>";
			cardHTML3 = "<tr><td colspan = \"5\">" + card.getText() + "</td></tr></table></center>";
		}
		else if(card.getType().equals("items") || card.getType().equals("spells"))
		{
			cardHTML1 = "<center><table><tr><td><center>" + card.getForce() + "F</center></td><td colspan=\"3\"><center><b>" + card.getName() + "</b></center></td><td><center>" + card.getChi() + "C</center></td></tr>";
			cardHTML2 = "<tr><td></td><td colspan = \"3\"><center>" + card.getCost() + " G</center></td><td></td></tr>";
			cardHTML3 = "<tr><td></td><td colspan = \"5\">" + card.getText() + "</td><td></td></tr>";
			cardHTML4 = "<tr><td colspan = \"5\"><center>" + card.getFocus() + " Focus</center></td></tr></table></center>";
		}
		else if(card.getType().equals("personalities"))
		{
			cardHTML1 = "<center><table><tr><td><center>" + card.getForce() + "F</center></td><td colspan=\"3\"><center><b>" + card.getName() + "</b></center></td><td><center>" + card.getChi() + "C</center></td></tr>";
			cardHTML2 = "<tr><td></td><td colspan = \"3\"><center>" + card.getHonorReq() + " / " + card.getCost() + " / " + card.getPersonalHonor() + "</center></td><td></td></tr>";
			cardHTML3 = "<tr><td></td><td colspan = \"3\"><center>HR / GC / PH</center></td><td></td></tr>";
			cardHTML4 = "<tr><td colspan = \"5\">" + card.getText() + "</td></tr></table></center>";
		}
		else if(card.getType().equals("rings"))
		{
			cardHTML1 = "<center><table><tr><td colspan=\"5\"><center><b>" + card.getName() + "</b></center></td></tr>";
			cardHTML2 = "<tr><td colspan = \"5\">" + card.getText() + "</td></tr>";
			cardHTML3 = "<tr><td colspan = \"5\"><center>" + card.getFocus() + " Focus</center></td></tr></table></center>";
		}
		else if(card.getType().equals("senseis"))
		{

		}
		else if(card.getType().equals("strongholds"))
		{

		}

		//Set the card window to show the html table for the card
		setText(cardHTML1 + cardHTML2 + cardHTML3 + cardHTML4);
		//Scroll the window back up to the top
		setCaretPosition(0);
	}
}