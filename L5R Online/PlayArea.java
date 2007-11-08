// PlayArea.java
// Written by Brian Ouellette
// Part of Dojo
// This is the beefy part of the program. It controls the display for the game.

package l5r;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.util.*;
import java.io.*;

class PlayArea extends JPanel implements MouseListener, MouseMotionListener
{
	Dimension dimension;
	static int cardWidth, cardHeight;
	//Reference height (height that the card is initially)
	static int baseCardHeight;
	static ArrayList<PlayableCard> displayedCards;
	boolean cardClicked;
	//The percent of a card that attachments show
	static double attachmentHeight = .2;
	PlayableCard clickedCard;

    public PlayArea(int width, int height)
    {
		cardHeight = height/5;
		cardWidth = (int)(cardHeight*(2.5/3.5));

		baseCardHeight = cardHeight;

		//Create a new ArrayList to hold the cards to display and
		displayedCards = new ArrayList<PlayableCard>(40);

		addCard(new PlayableCard("WoE091"));

		addMouseListener(this);
		addMouseMotionListener(this);
    }

    public void setCardSize(int cardHeight)
    {
		this.cardHeight = cardHeight;
		this.cardWidth = cardWidth = (int)(cardHeight*(2.5/3.5));
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
			System.out.println(element.getLocation());
			displayCard(element, (Graphics2D)g);
    	}
    }

	public void displayCard(PlayableCard card, Graphics2D g)
	{

		int[] location = card.getLocation();

		//Recursively show all the attachments on the current card
		//Will also show all the attachments on attached cards in correct order.

		/* Example of intended use
		 * 1 has 2 and 4 attached to it
		 * 2 has 3 attached to it
		 * 4 has 5 attached to it
		 *
		 * This starts at the last attachment of the current card
		 * which will be 4 as 1 is the only card stored in displayedCards
		 * (attachments are displayed by default)
		 * First the attachments of 4 are displayed. Then 4 itself is displayed.
		 * Then the next to last attachment on 1 is called. It's attachments are
		 * displayed so 3 gets put out. Then 2 and finally 1. So 1 will be "on top"
		 * with 2-3-4-5 in that order stacked "underneath" it.
		 */
		ArrayList<PlayableCard> attachments = card.getAttachments();
		for(int i = attachments.size()-1; i >= 0; i--)
		{
			displayCard(attachments.get(i), g);
		}

		BufferedImage displayImage = card.getImage();
		if(displayImage == null)
		{
			StoredCard databaseCard = Main.database.get(card.getID());
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

					//We read in and resize the image once, after that it is stored in PlayableCard
					BufferedImage tempImage = ImageIO.read(new File(imageLocation));
					BufferedImage cardImage = new BufferedImage(cardWidth, cardHeight, tempImage.getType());
					Graphics2D g2 = cardImage.createGraphics();

					if(cardImage.getHeight() >= cardHeight)
					{
						//if downsizing image
						//Using the old .getScaledInsteance instead of the helper method as it produces much better results. If speed becomes an issue
						//then they can be swapped.
						//Image cardImage2 = getScaledInstance(cardImage, cardWidth, cardHeight, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
						Image cardImage2 = tempImage.getScaledInstance(cardWidth, cardHeight, Image.SCALE_AREA_AVERAGING);
						//AffineTransform transform = new AffineTransform();
						//transform.setToTranslation(location[0], location[1]);

						g2.drawImage(cardImage2, 0, 0, null);
						g2.dispose();
					}
					else
					{
						//if enlarging image
						g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
						//supposedly better quality, slower
						//g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
						//g2.drawImage(cardImage, location[0], location[1], cardWidth, cardHeight, null);
						//g2.dispose();
					}

					card.setImage(cardImage);

				} catch(IOException io) {
					System.err.println(io);
				}
			}
		}

		g.drawImage(card.getImage(), location[0], location[1], cardWidth, cardHeight, null);
	}

	public ArrayList<PlayableCard> getAllAttachments(PlayableCard card)
	{
		//TODO: Test to be sure this returns all the attachments and in the correct order
		ArrayList<PlayableCard> attachments = card.getAttachments();
		ArrayList<PlayableCard> allAttachments = new ArrayList<PlayableCard>();

		int index = 0;
		while(index < attachments.size())
		{
			allAttachments.addAll(getAllAttachments(attachments.get(index)));
		}

		return allAttachments;
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
		System.out.println("mousePressed");
		Rectangle cardArea = new Rectangle();
		Point clickPoint = e.getPoint();
		System.out.println(clickPoint);
		int index = 0;

		while(!cardClicked && index < displayedCards.size())
		{
			clickedCard = displayedCards.get(index);
			int[] cardLocation = clickedCard.getLocation();

			int numAttachments = clickedCard.getAttachments().size();

			//TODO: Make this work properly with attachments. It should realize that it is clicking an attachment.
			cardArea.setLocation(cardLocation[0], cardLocation[1] - (int)(cardHeight*attachmentHeight*numAttachments));
			cardArea.setSize(cardWidth, cardHeight + (int)(cardHeight*attachmentHeight*numAttachments));

			if(cardArea.contains(clickPoint))
			{
				cardClicked = true;
			}
			else
			{
				index++;
			}
		}
    }
 	public void mouseReleased(MouseEvent e)
 	{
		cardClicked = false;
    }

    public void mouseDragged(MouseEvent e)
    {
		if(cardClicked)
		{
			Point clickPoint = e.getPoint();
			clickedCard.setLocation((int)clickPoint.getX(), (int)clickPoint.getY());
			repaint();
		}
	}

    public void mouseMoved(MouseEvent e)
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