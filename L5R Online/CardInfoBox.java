// CardInfoBox.java
// Written by Brian Ouellette
// Part of Dojo

package l5r;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import java.io.IOException;

class CardInfoBox extends JEditorPane
{
	Card card;

    public CardInfoBox()
    {
		setEditable(false);
		setContentType("text/html");
    }

    public CardInfoBox(Card card)
    {
		this();
		setCard(card);
	}

    public void setCard(Card card)
    {
		String cardHTML1, cardHTML2, cardHTML3, cardHTML4;

		cardHTML1 = "";
		cardHTML2 = "";
		cardHTML3 = "";
		cardHTML4 = "";

		if(card.getType().equals("actions") || card.getType().equals("kihos"))
		{
			//The card text in the window is created as an html table
			cardHTML1 = "<html><center><table><tr><td colspan=\"5\"><center><b>" + card.getName() + "</b></center></td></tr>";
			cardHTML2 = "<tr><td colspan = \"5\"><center>" + card.getCost() + " G</center></td><td></td></tr>";
			cardHTML3 = "<tr><td colspan = \"5\">" + card.getText() + "</td></tr>";
			cardHTML4 = "<tr><td colspan = \"5\"><center>" + card.getFocus() + " Focus</center></td></tr></table></center></html>";
		}
		//TODO: Use String.replaceAll and regexp to insert &middot instead of . in the traits section
		else if(card.getType().equals("ancestors") || card.getType().equals("followers"))
		{
			cardHTML1 = "<html><center><table><tr><td><center>" + card.getForce() + "F</center></td><td colspan=\"3\"><center><b>" + card.getName() + "</b></center></td><td><center>" + card.getChi() + "C</center></td></tr>";
			cardHTML2 = "<tr><td></td><td colspan = \"3\"><center>" + card.getHonorReq() + " / " + card.getCost() + " / " + card.getPersonalHonor() + "</center></td><td></td></tr>";
			cardHTML3 = "<tr><td></td><td colspan = \"3\"><center>HR / GC / PH<center></td><td></td></tr>";
			cardHTML4 = "<tr><td colspan = \"5\">" + card.getText() + "</td></tr><tr><td colspan = \"5\"><center>" + card.getFocus() + " Focus</center></td></tr></table></center></html>";
		}
		else if(card.getType().equals("events") || card.getType().equals("regions") || card.getType().equals("winds"))
		{
			cardHTML1 = "<html><center><table><tr><td colspan=\"5\"><center><b>" + card.getName() + "</b></center></td></tr>";
			cardHTML2 = "<tr><td colspan = \"5\">" + card.getText() + "</td></tr></table></html>";
		}
		else if(card.getType().equals("holdings"))
		{
			cardHTML1 = "<html><center><table><tr><td colspan=\"5\"><center><b>" + card.getName() + "</b></center></td></tr>";
			cardHTML2 = "<tr><td colspan = \"5\"><center>" + card.getCost() + " G</center></td><td></td></tr>";
			cardHTML3 = "<tr><td colspan = \"5\">" + card.getText() + "</td></tr></table></center></html>";
		}
		else if(card.getType().equals("items") || card.getType().equals("spells"))
		{
			cardHTML1 = "<html><center><table><tr><td><center>" + card.getForce() + "F</center></td><td colspan=\"3\"><center><b>" + card.getName() + "</b></center></td><td><center>" + card.getChi() + "C</center></td></tr>";
			cardHTML2 = "<tr><td></td><td colspan = \"3\"><center>" + card.getCost() + " G</center></td><td></td></tr>";
			cardHTML3 = "<tr><td></td><td colspan = \"5\">" + card.getText() + "</td><td></td></tr>";
			cardHTML4 = "<tr><td colspan = \"5\"><center>" + card.getFocus() + " Focus</center></td></tr></table></center></html>";
		}
		else if(card.getType().equals("personalities"))
		{
			cardHTML1 = "<html><center><table><tr><td><center>" + card.getForce() + "F</center></td><td colspan=\"3\"><center><b>" + card.getName() + "</b></center></td><td><center>" + card.getChi() + "C</center></td></tr>";
			cardHTML2 = "<tr><td></td><td colspan = \"3\"><center>" + card.getHonorReq() + " / " + card.getCost() + " / " + card.getPersonalHonor() + "</center></td><td></td></tr>";
			cardHTML3 = "<tr><td></td><td colspan = \"3\"><center>HR / GC / PH</center></td><td></td></tr>";
			cardHTML4 = "<tr><td colspan = \"5\">" + card.getText() + "</td></tr></table></center></html>";
		}
		else if(card.getType().equals("rings"))
		{
			cardHTML1 = "<html><center><table><tr><td colspan=\"5\"><center><b>" + card.getName() + "</b></center></td></tr>";
			cardHTML2 = "<tr><td colspan = \"5\">" + card.getText() + "</td></tr>";
			cardHTML3 = "<tr><td colspan = \"5\"><center>" + card.getFocus() + " Focus</center></td></tr></table></center></html>";
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