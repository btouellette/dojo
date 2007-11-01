// PlayArea.java
// Written by Brian Ouellette
// Part of Dojo

package l5r;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.util.*;
import java.io.*;

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

		addCard(new PlayableCard(Main.database.get("TTT088").getID()));
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
		displayedCards.add(0, card);
	}

	public void removeCard(PlayableCard card)
	{
		displayedCards.remove(card);
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
			displayCard(element, (Graphics2D)g);
    	}
    }

	public void displayCard(PlayableCard card, Graphics2D g)
	{
		StoredCard databaseCard = Main.database.get(card.getID());

		int[] location = card.getLocation();

		String imageLocation = databaseCard.getImageLocation();
		if(imageLocation == null)
		{
			//Display a placeholder card
		}
		else
		{
			try
			{
				//TODO: Check to see if there are performance increases that can be done here
				//Use GraphicsConfiguration.createCompatibleImage(w, h) if image isn't compatible
				BufferedImage cardImage = ImageIO.read(new File(imageLocation));

				if(cardImage.getHeight() >= cardHeight)
				{
					//if downsizing image

					//Using the old .getScaledInsteance instead of the helper method as it produces much better results. If speed becomes an issue
					//then they can be swapped.
					//Image cardImage2 = getScaledInstance(cardImage, cardWidth, cardHeight, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
					Image cardImage2 = cardImage.getScaledInstance(cardWidth, cardHeight, Image.SCALE_AREA_AVERAGING);
					g.drawImage(cardImage2, null, null);
					System.out.println("downsizing");
				}
				else
				{
					//if enlarging image
					g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
					//supposedly better quality, slower
					//g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
					g.drawImage(cardImage, location[0], location[1], cardWidth, cardHeight, null);
					System.out.println("upsizing");
				}

				//cardImage = gc.createCompatibleImage(cardHeight, cardWidth, Transparency.OPAQUE), RenderingHints.VALUE_INTERPOLATION_BILINEAR);

			} catch(IOException io) {
				System.err.println(io);
			}
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

    //A good fast high-quality image downscaling algorithm hasn't been implemented yet
    //in Graphics2D. This is a helper method to avoid using the old .getScaledInstance
    //which rescales the image multiple times using the standard Bilinear interpolation.
    //It was written by Chris Campbell and found here: http://today.java.net/lpt/a/362#perfnotes

    /**
	 * Convenience method that returns a scaled instance of the
	 * provided {@code BufferedImage}.
	 *
	 * @param img the original image to be scaled
	 * @param targetWidth the desired width of the scaled instance,
	 *    in pixels
	 * @param targetHeight the desired height of the scaled instance,
	 *    in pixels
	 * @param hint one of the rendering hints that corresponds to
	 *    {@code RenderingHints.KEY_INTERPOLATION} (e.g.
	 *    {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
	 *    {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
	 *    {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
	 * @param higherQuality if true, this method will use a multi-step
	 *    scaling technique that provides higher quality than the usual
	 *    one-step technique (only useful in downscaling cases, where
	 *    {@code targetWidth} or {@code targetHeight} is
	 *    smaller than the original dimensions, and generally only when
	 *    the {@code BILINEAR} hint is specified)
	 * @return a scaled version of the original {@code BufferedImage}
	 */
	/*public BufferedImage getScaledInstance(BufferedImage img,
										   int targetWidth,
										   int targetHeight,
										   Object hint,
										   boolean higherQuality)
	{
		int type = (img.getTransparency() == Transparency.OPAQUE) ?
			BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
		BufferedImage ret = (BufferedImage)img;
		int w, h;
		if (higherQuality) {
			// Use multi-step technique: start with original size, then
			// scale down in multiple passes with drawImage()
			// until the target size is reached
			w = img.getWidth();
			h = img.getHeight();
		} else {
			// Use one-step technique: scale directly from original
			// size to target size with a single drawImage() call
			w = targetWidth;
			h = targetHeight;
		}

		do {
			if (higherQuality && w > targetWidth) {
				w /= 2;
				if (w < targetWidth) {
					w = targetWidth;
				}
			}

			if (higherQuality && h > targetHeight) {
				h /= 2;
				if (h < targetHeight) {
					h = targetHeight;
				}
			}

			BufferedImage tmp = new BufferedImage(w, h, type);
			Graphics2D g2 = tmp.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
			g2.drawImage(ret, 0, 0, w, h, null);
			g2.dispose();

			ret = tmp;
		} while (w != targetWidth || h != targetHeight);

		return ret;
	}*/
}