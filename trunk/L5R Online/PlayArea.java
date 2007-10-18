/**
 * @(#)PlayArea.java
 *
 *
 * @author
 * @version 1.00 2007/10/5
 */

package l5r;
// PlayArea.java
// Written by Brian Ouellette
// Part of Dojo

//package l5r;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;

class PlayArea extends JPanel implements MouseListener
{
	Dimension dimension;
	int cardWidth, cardHeight, baseCardHeight;

    public PlayArea(int width, int height)
    {
		//dimension = this.getSize();

		cardHeight = (int)(height/5);
		cardWidth = (int)(cardHeight*(2.5/3.5));

		baseCardHeight = cardHeight;
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

	//Override the default JPanel paint method
    public void paintComponent(Graphics g)
    {
		super.paintComponent(g);

		dimension = this.getSize();
		double height = dimension.getHeight();
		double width = dimension.getWidth();

		//Create dynasty discard
		g.setColor(Color.BLACK);
		g.fillRect((int)(width - (2*(cardWidth+8)-2)), (int)(height - (cardHeight+8)), 2*(cardWidth+8), 2*(cardHeight+8));
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect((int)(width - (cardWidth+6)), (int)(height - (cardHeight+6)), (cardWidth+4), (cardHeight+4));

		//Create dynasty deck
		g.fillRect((int)(width - 2*(cardWidth+6)), (int)(height - (cardHeight+6)), (cardWidth+4), (cardHeight+4));

		//Create fate discard
		g.setColor(Color.BLACK);
		g.fillRect(0, (int)(height - (cardHeight+8)), 2*(cardWidth+8)-2, 2*(cardHeight+8));
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(2, (int)(height - (cardHeight+6)), (cardWidth+4), (cardHeight+4));

		//Create fate deck
		g.fillRect((cardWidth+8), (int)(height - (cardHeight+6)), (cardWidth+4), (cardHeight+4));

		//Create opponent's dynasty discard
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 2*(cardWidth+8)-2, (cardHeight+8));
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(2, 2, (cardWidth+4), (cardHeight+4));

		//Create opponent's dynasty deck
		g.fillRect((cardWidth+8), 2, (cardWidth+4), (cardHeight+4));

		//Create opponent's fate discard
		g.setColor(Color.BLACK);
		g.fillRect((int)(width - (2*(cardWidth+8)-2)), 0, 2*(cardWidth+8), (cardHeight+8));
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect((int)(width - (cardWidth+6)), 2, (cardWidth+4), (cardHeight+4));

		//Create opponent's fate deck
		g.fillRect((int)(width - 2*(cardWidth+6)), 2, (cardWidth+4), (cardHeight+4));
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