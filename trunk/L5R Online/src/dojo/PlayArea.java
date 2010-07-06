package dojo;
// PlayArea.java
// Written by Brian Ouellette
// Part of Dojo
// This is the beefy part of the program. It controls the display for the game.

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import javax.swing.*;

import java.util.*;
import java.util.zip.*;
import java.io.*;
import java.net.*;

class PlayArea extends JPanel implements MouseListener, MouseMotionListener, ActionListener
{
	private static final long serialVersionUID = 1L;
	// The distance from where the card was clicked to the upper left corner of the base card
	// Used for movement logic
	private int distanceX, distanceY;
	// Size of PlayArea in pixels
	private int height, width;
	// The last card that was clicked on depending on whether it was attached to anything or not
	// If clickedAttachment != null then clickedCard is the base card of the unit
	private PlayableCard clickedCard;
	private PlayableCard clickedAttachment;
	// Tests used for launching context menus correctly
	private boolean cardClicked = false;
	private boolean attachmentClicked = false;
	private boolean provClicked = false;
	private boolean deckClicked = false;
	private boolean discardClicked = false;
	private boolean dynastyClicked = false;
	private boolean fateClicked = false;
	// Which of your provinces was clicked (left to right)
	private int numProvClicked;
	// Context (right-click) menus
	private JPopupMenu popupCard, popupProv, popupDeck, popupDiscard;
	
	// Provinces (left to right from 0->max)
	static ArrayList<Province> provinces;
	static Deck dynastyDeck, fateDeck, dynastyDiscard, fateDiscard;
	// The base cards of all units to be displayed. Attachments are fetched at display time and aren't present here
	static ArrayList<PlayableCard> displayedCards;
	// Current size of a single card
	static int cardWidth, cardHeight;
	// Reference height (height that the card is initially)
	static int baseCardHeight;
	// The percent of a card that attachments show above their base card
	static double attachmentHeight = .2;
	// Number of provinces
	//TODO: Support Ratling/Spirit or other starting sizes
	static int yourNumProv = 4;
	static int oppNumProv = 4;

	public PlayArea(int width, int height)
	{
		super();
		// Setting the default height as a fifth of the PlayArea height
		// This allows for vertically: two sets of provinces and two units with a few attachments
		cardHeight = height/5;
		// Normal sized cards are 2.5" wide and 3.5" tall
		cardWidth = (int)(cardHeight*(2.5/3.5));
		baseCardHeight = cardHeight;

		// Create a new ArrayList to hold the cards to display
		displayedCards = new ArrayList<PlayableCard>(30);

		//TODO: Remove these lines once testing is done
		addCard(new PlayableCard("CoB009"));
		PlayableCard test = new PlayableCard("CoB069");
		displayedCards.get(0).attach(test);
		displayedCards.get(0).attach(new PlayableCard("CoB070"));
		test.attach(new PlayableCard("IE096"));
		test.attach(new PlayableCard("TH142"));
		test.attach(new PlayableCard("IE097"));
		
		//TODO: Flesh out context menus		
		// Create context menu for card clicks
		popupCard = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem("Attach");
		menuItem.addActionListener(this);
		popupCard.add(menuItem);
		menuItem = new JMenuItem("Unattach");
		menuItem.addActionListener(this);
		popupCard.add(menuItem);

		// Create context menu for province clicks
		popupProv = new JPopupMenu();
		menuItem = new JMenuItem("Destroy");
		menuItem.addActionListener(this);
		popupProv.add(menuItem);
		menuItem = new JMenuItem("Add Province On Left");
		menuItem.addActionListener(this);
		popupProv.add(menuItem);
		menuItem = new JMenuItem("Add Province On Right");
		menuItem.addActionListener(this);
		popupProv.add(menuItem);

		// Create context menu for deck clicks
		popupDeck = new JPopupMenu();
		menuItem = new JMenuItem("Shuffle");
		menuItem.addActionListener(this);
		popupDeck.add(menuItem);
		menuItem = new JMenuItem("Search");
		menuItem.addActionListener(this);
		popupDeck.add(menuItem);

		// Create context menu for discard clicks
		popupDiscard = new JPopupMenu();
		menuItem = new JMenuItem("Search");
		menuItem.addActionListener(this);
		popupDiscard.add(menuItem);
		
		// Create decks, discards, and provinces
		//TODO: Add code elsewhere to set these up (after reading in deck file)
		dynastyDeck = new Deck();
		fateDeck = new Deck();
		dynastyDiscard = new Deck();
		fateDiscard = new Deck();
		
		provinces = new ArrayList<Province>(4);
		provinces.add(new Province());
		provinces.add(new Province());
		provinces.add(new Province());
		provinces.add(new Province());
		
		// Interaction is handled within the class
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	// Adds a new unit to be displayed on the screen
	private void addCard(PlayableCard card)
	{
		displayedCards.add(0, card);
	}

	// Removes a unit from the screen
	private void removeCard(PlayableCard card)
	{
		displayedCards.remove(card);
	}

	// Override the default JComponent paint method
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		// In case of resize we'll get the new height and width parameters
		height = getHeight();
		width = getWidth();

		// Create an area for you to keep your hand
		int sizeHand = (int)(cardWidth*1.5);
		int startHand = width - sizeHand;

		// Create fate discard
		g.setColor(Color.BLACK);
		g.fillRect(startHand - (2*(cardWidth+8)-2), height - (cardHeight+8), 2*(cardWidth+8), cardHeight+8);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(startHand - (cardWidth+6), height - (cardHeight+6), cardWidth+4, cardHeight+4);

		// Create fate deck
		g.fillRect(startHand - 2*(cardWidth+6), height - (cardHeight+6), cardWidth+4, cardHeight+4);

		// Create dynasty discard
		g.setColor(Color.BLACK);
		g.fillRect(0, height - (cardHeight+8), 2*(cardWidth+8)-2, cardHeight+8);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(2, height - (cardHeight+6), cardWidth+4, cardHeight+4);

		// Create dynasty deck
		g.fillRect(cardWidth+8, height - (cardHeight+6), cardWidth+4, cardHeight+4);

		// Create opponent's fate discard
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 2*(cardWidth+8)-2, cardHeight+8);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(2, 2, cardWidth+4, cardHeight+4);

		// Create opponent's fate deck
		g.fillRect(cardWidth+8, 2, cardWidth+4, cardHeight+4);

		// Create opponent's dynasty discard
		g.setColor(Color.BLACK);
		g.fillRect(startHand - (2*(cardWidth+8)-2), 0, 2*(cardWidth+8), cardHeight+8);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(startHand - (cardWidth+6), 2, cardWidth+4, cardHeight+4);

		// Create opponent's dynasty deck
		g.fillRect(startHand - 2*(cardWidth+6), 2, cardWidth+4, cardHeight+4);

		// Create fate hand
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(startHand, 0, sizeHand, height);
		g.setColor(Color.BLACK);
		g.fillRect(startHand-2, 0, 2, height);

		// Create your provinces
		int leftBorder = 2*(cardWidth+8)-2;
		int rightBorder = startHand - (2*(cardWidth+8)-2);
		int distanceBetween = (rightBorder - leftBorder)/(yourNumProv+1);
		for(int i = 1; i < yourNumProv+1; i++)
		{
			g.setColor(Color.BLACK);
			g.fillRect(leftBorder + i*distanceBetween - cardWidth/2, height - (cardHeight+6), cardWidth+12, cardHeight+6);
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(leftBorder + i*distanceBetween - cardWidth/2 + 2, height - (cardHeight+4), cardWidth+8, cardHeight+4);
		}

		// Create opponents provinces
		distanceBetween = (rightBorder - leftBorder)/(oppNumProv+1);
		for(int i = 1; i < oppNumProv+1; i++)
		{
			g.setColor(Color.BLACK);
			g.fillRect(leftBorder + i*distanceBetween - cardWidth/2, 0, cardWidth+12, cardHeight+6);
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(leftBorder + i*distanceBetween - cardWidth/2 + 2, 0, cardWidth+8, cardHeight+4);
		}

		// Now that we've drawn all the play surface draw all the cards
		ListIterator<PlayableCard> iterator = displayedCards.listIterator();
		while (iterator.hasNext())
		{
			PlayableCard element = iterator.next();
			displayCard(element, (Graphics2D)g);
		}
	}

	private void displayCard(PlayableCard card, Graphics2D g)
	{
		// Display all the attachments first. This will draw the top attachment first and at the farthest back
		ArrayList<PlayableCard> attachments = card.getAttachments();
		for(int i = attachments.size()-1; i >= 0; i--)
		{
			displayCard(attachments.get(i), g);
		}

		// Load in the image from the class, the file, or kamisasori.net
		BufferedImage displayImage = card.getImage();
		// If we have't loaded in an image for this yet
		if(displayImage == null)
		{
			// Go to the database to find out where the image should be located
			StoredCard databaseCard = Main.database.get(card.getID());
			String imageLocation = databaseCard.getImageLocation();
			// If there wasn't a valid file in the file system
			if(imageLocation == null)
			{
				System.err.print("** Card image missing. Attempting to get image pack for " + databaseCard.getImageEdition() + " from kamisasori.net: ");
				// Get image pack off kamisasori.net
				try {
					// Download image pack as zip via http
					URL url = new URL("http://www.kamisasori.net/files/imagepacks/" + databaseCard.getImageEdition() + ".zip");
					url.openConnection();
					InputStream is = url.openStream();
					FileOutputStream fos = new FileOutputStream("tmp-imagepack.zip");
					for (int c = is.read(); c != -1; c = is.read())
					{
						fos.write(c);
					}
					is.close();
					fos.close();
					System.err.println("success!");

					// Unzip image pack
					FileInputStream fis = new FileInputStream("tmp-imagepack.zip");
					ZipInputStream zis = new ZipInputStream(fis);
					ZipEntry ze;
					while((ze = zis.getNextEntry()) != null)
					{
						System.out.print("** Unzipping " + ze.getName() + ": ");
						// Make any directories as needed before unzipping
						File f = new File("images/cards/" + databaseCard.getImageEdition());
						f.mkdirs();
						fos = new FileOutputStream("images/cards/" + ze.getName());
						for (int c = zis.read(); c != -1; c = zis.read())
						{
							fos.write(c);
						}
						zis.closeEntry();
						fos.close();
						System.out.println("success!");
					}
					zis.close();

					// Delete leftover zip file
					System.out.print("** Deleting zip file after extraction: ");
					File f = new File("tmp-imagepack.zip");
					f.delete();
					System.out.println("success!");
					// Successfully got the files so we should have a valid imageLocation now
					imageLocation = databaseCard.getImageLocation();
				} catch (Throwable t) {
					System.err.println("failed. Check your internet connection.");
					File f = new File("tmp-imagepack.zip");
					if(f.exists())
					{
						f.delete();
					}
					//TODO: Display a placeholder card
				}
			}
			//TODO: Check to see if there are performance increases that can be done here
			if(imageLocation != null)
			{
				try
				{
					// We read in and resize the image once, after that it is stored in PlayableCard
					// Use GraphicsConfiguration.createCompatibleImage(w, h) if image isn't compatible
					BufferedImage tempImage = ImageIO.read(new File(imageLocation));
					BufferedImage cardImage = new BufferedImage(cardWidth, cardHeight, tempImage.getType());
					Graphics2D g2 = cardImage.createGraphics();

					// If downsizing image
					if(cardImage.getHeight() >= cardHeight)
					{
						// Using the old .getScaledInsteance instead of the helper method as it produces much better results. If speed becomes an issue
						// then they can be swapped.
						//Image cardImage2 = getScaledInstance(cardImage, cardWidth, cardHeight, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
						Image cardImage2 = tempImage.getScaledInstance(cardWidth, cardHeight, Image.SCALE_AREA_AVERAGING);
						//AffineTransform transform = new AffineTransform();
						//transform.setToTranslation(location[0], location[1]);

						g2.drawImage(cardImage2, 0, 0, null);
						g2.dispose();
					}
					// If enlarging image
					else
					{
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
		int[] location = card.getLocation();
		g.drawImage(card.getImage(), location[0], location[1], cardWidth, cardHeight, null);
	}

	// Pop up the correct context menu
	private void showPopup(MouseEvent e)
	{
		Point clickPoint = e.getPoint();
		int sizeHand = (int)(cardWidth*1.5);
		int startHand = width - sizeHand;

		// Check to see if click was inside fate discard
		Rectangle area = new Rectangle(startHand - (cardWidth+6), height - (cardHeight+6), cardWidth+4, cardHeight+4);
		if(area.contains(clickPoint))
		{
			fateClicked = true;
			discardClicked = true;
		}

		// Check to see if click was inside fate deck
		area = new Rectangle(startHand - 2*(cardWidth+6), height - (cardHeight+6), cardWidth+4, cardHeight+4);
		if(area.contains(clickPoint))
		{
			fateClicked = true;
			deckClicked = true;
		}

		// Check to see if click was inside dynasty discard
		area = new Rectangle(2, height - (cardHeight+6), cardWidth+4, cardHeight+4);
		if(area.contains(clickPoint))
		{
			dynastyClicked = true;
			discardClicked = true;
		}

		// Check to see if click was inside dynasty deck
		area = new Rectangle(cardWidth+8, height - (cardHeight+6), cardWidth+4, cardHeight+4);
		if(area.contains(clickPoint))
		{
			dynastyClicked = true;
			deckClicked = true;
		}
		
		// Check to see if click was inside one of your provinces
		int leftBorder = 2*(cardWidth+8)-2;
		int rightBorder = startHand - (2*(cardWidth+8)-2);
		int distanceBetween = (rightBorder - leftBorder)/(yourNumProv+1);
		for(int i = 1; i < yourNumProv+1; i++)
		{
			area = new Rectangle(leftBorder + i*distanceBetween - cardWidth/2 + 2, height - (cardHeight+4), cardWidth+8, cardHeight+4);
			if(area.contains(clickPoint))
			{
				provClicked = true;
				numProvClicked = i;
			}
		}
		
		// Now launch the right menu at the click location
		if(cardClicked)
		{
			popupCard.show(this, e.getX(), e.getY());
		}
		else if(provClicked)
		{
			popupProv.show(this, e.getX(), e.getY());
		}
		else if(deckClicked)
		{
			popupDeck.show(this, e.getX(), e.getY());
		}
		else if(discardClicked)
		{
			popupDiscard.show(this, e.getX(), e.getY());
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

	// On mouse click check to see if anything relevant was clicked
	public void mousePressed(MouseEvent e)
	{		
		Rectangle cardArea = new Rectangle();
		Rectangle attachmentArea = new Rectangle();
		Point clickPoint = e.getPoint();
		
		int index = 0;
		// Search first to see if any cards displayed on the JPanel were clicked
		//TODO: Test overlapping cards. Might need to reverse search through displayedCards
		while(!cardClicked && index < displayedCards.size())
		{
			clickedCard = displayedCards.get(index);
			int[] cardLocation = clickedCard.getLocation();	
			ArrayList<PlayableCard> attachments = clickedCard.getAllAttachments();
			int numAttachments = attachments.size();

			//TODO: Test with different chains of attached cards
			cardArea.setLocation(cardLocation[0], cardLocation[1]);
			cardArea.setSize(cardWidth, cardHeight);			
			attachmentArea.setLocation(cardLocation[0], cardLocation[1] - (int)(cardHeight*attachmentHeight*numAttachments));
			attachmentArea.setSize(cardWidth, (int)(cardHeight*attachmentHeight*numAttachments));

			if(cardArea.contains(clickPoint))
			{
				cardClicked = true;
				attachmentClicked = false;
				distanceX = (int)clickPoint.getX() - cardLocation[0];
				distanceY = (int)clickPoint.getY() - cardLocation[1];
				if(e.getButton() == MouseEvent.BUTTON1)
				{
					Main.cardBox.setCard(Main.database.get(clickedCard.getID()));
				}
			}
			else if(attachmentArea.contains(clickPoint))
			{
				cardClicked = true;
				attachmentClicked = true;
				distanceX = (int)clickPoint.getX() - cardLocation[0];
				distanceY = (int)clickPoint.getY() - cardLocation[1];
				clickedAttachment = attachments.get((int)(-distanceY/(cardHeight*attachmentHeight)));
				if(e.getButton() == MouseEvent.BUTTON1)
				{
					Main.cardBox.setCard(Main.database.get(clickedAttachment.getID()));
				}
			}
			else
			{
				index++;
			}
		}
		if(e.isPopupTrigger())
		{
			showPopup(e);
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		if(e.isPopupTrigger())
		{
			showPopup(e);
		}
		cardClicked = false;
		attachmentClicked = false;
		deckClicked = false;
		provClicked = false;
		discardClicked = false;
	}

	public void mouseDragged(MouseEvent e)
	{
		// e.getButton() doesn't work inside mouseDragged
		if(cardClicked && SwingUtilities.isLeftMouseButton(e))
		{
			Point clickPoint = e.getPoint();
			int newX = (int)clickPoint.getX() - distanceX;
			int newY = (int)clickPoint.getY() - distanceY;
			
			// Don't let them drag cards off the playing field entirely
			// TODO: modify for dragging into/out of hand when implemented
			if(newX < 10 - cardWidth)
			{
				newX = 10 - cardWidth;
			}
			else if(newX > getWidth() - 10)
			{
				newX = getWidth() - 10;
			}
			if(newY < 10 - cardHeight)
			{
				newY = 10 - cardHeight;
			}
			else if(newY > getHeight() - 10)
			{
				newY = getHeight() - 10;
			}

			clickedCard.setLocation(newX, newY);
			repaint();
		}
	}

	public void mouseMoved(MouseEvent e)
	{
	}

	public void actionPerformed(ActionEvent e)
	{
		String name = ((AbstractButton)e.getSource()).getText();
		//TODO: Fill out rest of context menu and actions
		if(name.equals("Destroy"))
		{
			provinces.get(numProvClicked).destroy();
			yourNumProv--;
			repaint();
		}
		else if(name.equals("Add Province On Left"))
		{
			provinces.add(numProvClicked, new Province());
			yourNumProv++;
			repaint();
		}
		else if(name.equals("Add Province On Right"))
		{
			provinces.add(numProvClicked+1, new Province());
			yourNumProv++;
			repaint();
		}
		else if(name.equals("Shuffle"))
		{
			if(dynastyClicked)
			{
				dynastyDeck.shuffle();
			}
			else if(fateClicked)
			{
				fateDeck.shuffle();
			}
		}
		else if(name.equals("Search"))
		{
			//TODO: Add search interface
		}
	}

	// A good fast high-quality image downscaling algorithm hasn't been implemented yet
	// in Graphics2D. This is a helper method to avoid using the old .getScaledInstance
	// which rescales the image multiple times using the standard Bilinear interpolation.
	// It was written by Chris Campbell and found here:
	// http://today.java.net/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html

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
