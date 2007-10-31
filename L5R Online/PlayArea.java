// PlayArea.java
// Written by Brian Ouellette
// Part of Dojo

package l5r;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class PlayArea extends JPanel implements MouseListener
{
	Dimension dimension;
	int cardWidth, cardHeight, baseCardHeight;
	ArrayList<PlayableCard> displayedCards;

    public PlayArea(int width, int height)
    {
		cardHeight = height/5;
		cardWidth = (int)(cardHeight*(2.5/3.5));

		baseCardHeight = cardHeight;

		//Create a new ArrayList to hold the cards to display and
		displayedCards = new ArrayList<PlayableCard>(40);
    }

    public void setCardSize(int cardHeight)
    {
		this.cardHeight = cardHeight;
		this.cardWidth = cardWidth = (int)(cardHeight*(2.5/3.5));
	}

	public int getBaseCardSize()
	{
		return baseCardHeight;
	}

	public void addCard(PlayableCard card)
	{
		//if(!displayedCards.contains(card))
		//{
			displayedCards.add(0, card);
		//}
	}

	public void displayCard(PlayableCard card)
	{

	}

	//Override the default JPanel paint method
    public void paintComponent(Graphics g)
    {
		super.paintComponent(g);

		dimension = this.getSize();
		int height = (int)dimension.getHeight();
		int width = (int)dimension.getWidth();

		int sizeHand = (int)(cardWidth*1.5);
		int startHand = width - sizeHand;

		//Create dynasty discard
		g.setColor(Color.BLACK);
		g.fillRect(startHand - (2*(cardWidth+8)-2), height - (cardHeight+8), 2*(cardWidth+8), 2*(cardHeight+8));
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(startHand - (cardWidth+6), height - (cardHeight+6), cardWidth+4, cardHeight+4);

		//Create dynasty deck
		g.fillRect(startHand - 2*(cardWidth+6), height - (cardHeight+6), cardWidth+4, cardHeight+4);

		//Create fate discard
		g.setColor(Color.BLACK);
		g.fillRect(0, height - (cardHeight+8), 2*(cardWidth+8)-2, 2*(cardHeight+8));
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(2, height - (cardHeight+6), cardWidth+4, cardHeight+4);

		//Create fate deck
		g.fillRect(cardWidth+8, height - (cardHeight+6), cardWidth+4, cardHeight+4);

		//Create opponent's fate discard
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 2*(cardWidth+8)-2, cardHeight+8);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(2, 2, cardWidth+4, cardHeight+4);

		//Create opponent's fate deck
		g.fillRect(cardWidth+8, 2, cardWidth+4, cardHeight+4);

		//Create opponent's dynasty discard
		g.setColor(Color.BLACK);
		g.fillRect(startHand - (2*(cardWidth+8)-2), 0, 2*(cardWidth+8), cardHeight+8);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(startHand - (cardWidth+6), 2, cardWidth+4, cardHeight+4);

		//Create opponent's dynasty deck
		g.fillRect(startHand - 2*(cardWidth+6), 2, cardWidth+4, cardHeight+4);

		//Create fate hand
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(startHand, 0, sizeHand, height);
		g.setColor(Color.BLACK);
		g.fillRect(startHand-2, 0, 2, height);

		ListIterator<PlayableCard> iterator = displayedCards.listIterator();
		while (iterator.hasNext())
		{
			PlayableCard element = iterator.next();
			displayCard(element);
    	}
    }

    public void mouseClicked(MouseEvent e)
    {
    }
 	public void mouseEntered(MouseEvent e)
 	{
    }
 	public void mouseExited(MouseEvent e)
 	{
    }
 	public void mousePressed(MouseEvent e)
 	{
    }
 	public void mouseReleased(MouseEvent e)
 	{
    }
}