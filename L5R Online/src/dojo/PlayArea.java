package dojo;
// PlayArea.java
// Written by Brian Ouellette
// Part of Dojo
// This is the beefy part of the program. It controls the display for the game.

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import javax.swing.AbstractButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

//TODO: Split logic and display into separate classes (potentially menu stuff too)
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
	private JPopupMenu popupCard, popupAttachment, popupProv, popupDeck, popupDiscard;
	// Provinces (left to right from 0->max)
	private List<Province> provinces;
	//TODO: Support Ratling/Spirit or other starting sizes
	// Number of provinces
	private int yourNumProv = 4;
	private int oppNumProv = 4;
	
	static Deck dynastyDeck, fateDeck;
	static Discard dynastyDiscard, fateDiscard;
	// The base cards of all units to be displayed. Attachments are fetched at display time and aren't present here
	static List<PlayableCard> displayedCards;
	// Current size of a single card
	static int cardWidth, cardHeight;
	// Reference height (height that the card is initially)
	static int baseCardHeight;
	// The percent of a card that attachments show above their base card
	static double attachmentHeight = .2;

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
		//Takuji
		displayedCards.add(0, new PlayableCard("CoB009"));;
		PlayableCard test = new PlayableCard("CoB069");
		displayedCards.get(0).attach(test);
		displayedCards.get(0).attach(new PlayableCard("DJH047"));
		test.attach(new PlayableCard("IE096"));
		test.attach(new PlayableCard("TH142"));
		test.attach(new PlayableCard("IE097"));
		
		//TODO: Flesh out context menus		
		popupCard = new JPopupMenu();
		popupAttachment = new JPopupMenu();
		popupProv = new JPopupMenu();
		popupDeck = new JPopupMenu();
		popupDiscard = new JPopupMenu();

		JMenuItem menuItem = new JMenuItem("Destroy");
		menuItem.addActionListener(this);
		popupCard.add(menuItem);
		popupAttachment.add(menuItem);
		popupProv.add(menuItem);
		
		menuItem = new JMenuItem("Attach");
		menuItem.addActionListener(this);
		popupCard.add(menuItem);
		
		menuItem = new JMenuItem("Unattach");
		menuItem.addActionListener(this);
		popupAttachment.add(menuItem);

		menuItem = new JMenuItem("Add Province On Left");
		menuItem.addActionListener(this);
		popupProv.add(menuItem);
		menuItem = new JMenuItem("Add Province On Right");
		menuItem.addActionListener(this);
		popupProv.add(menuItem);

		menuItem = new JMenuItem("Shuffle");
		menuItem.addActionListener(this);
		popupDeck.add(menuItem);
		
		menuItem = new JMenuItem("Search");
		menuItem.addActionListener(this);
		popupDeck.add(menuItem);
		popupDiscard.add(menuItem);
		
		// Create decks, discards, and provinces
		dynastyDeck = new Deck(true);
		fateDeck = new Deck(false);
		dynastyDiscard = new Discard();
		fateDiscard = new Discard();
		
		provinces = new ArrayList<Province>(4);
		provinces.add(new Province());
		provinces.add(new Province());
		provinces.add(new Province());
		provinces.add(new Province());
		
		// Interaction is handled within the class
		addMouseListener(this);
		addMouseMotionListener(this);
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
		while(iterator.hasNext())
		{
			PlayableCard element = iterator.next();
			displayCard(element, (Graphics2D)g);
		}
		
		BufferedImage currentImage = dynastyDeck.getImage();
		if(currentImage != null)
		{
			g.drawImage(currentImage, cardWidth+10, height - (cardHeight+4), cardWidth, cardHeight, null);
		}
		currentImage = dynastyDiscard.getImage();
		if(currentImage != null)
		{
			g.drawImage(currentImage, 5, height - (cardHeight+4), cardWidth, cardHeight, null);
		}
		currentImage = fateDeck.getImage();
		if(currentImage != null)
		{
			g.drawImage(currentImage, startHand - 2*(cardWidth+5), height - (cardHeight+4), cardWidth, cardHeight, null);
		}
		currentImage = fateDiscard.getImage();
		if(currentImage != null)
		{
			g.drawImage(currentImage, startHand - (cardWidth+4), height - (cardHeight+4), cardWidth, cardHeight, null);
		}
	}

	private void displayCard(PlayableCard card, Graphics2D g)
	{
		// Display all the attachments first. This will draw the top attachment first and at the farthest back
		List<PlayableCard> attachments = card.getAttachments();
		for(int i = attachments.size()-1; i >= 0; i--)
		{
			displayCard(attachments.get(i), g);
		}

		int[] location = card.getLocation();
		g.drawImage(card.getImage(), location[0], location[1], cardWidth, cardHeight, null);
	}
	
	//TODO: Resolve the public/static nonsense going on in this class
	public void clearArea(Deck dynasty, Deck fate)
	{
		displayedCards.clear();
		fateDiscard.removeAll();
		dynastyDiscard.removeAll();
		dynastyDeck = dynasty;
		fateDeck = fate;
		repaint();
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
		if(attachmentClicked)
		{
			popupAttachment.show(this, e.getX(), e.getY());
		}
		else if(cardClicked)
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
			List<PlayableCard> attachments = clickedCard.getAllAttachments();
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
					Main.cardBox.setCard(Main.databaseID.get(clickedCard.getID()));
				}
			}
			else if(attachmentArea.contains(clickPoint))
			{
				cardClicked = true;
				attachmentClicked = true;
				distanceX = (int)clickPoint.getX() - cardLocation[0];
				distanceY = (int)clickPoint.getY() - cardLocation[1];
				// double->int cast loses precision which means around boundaries clicks can be problematic
				// Ensure we don't try to get an attachment outside our array range
				int attachment = (int)(-distanceY/(cardHeight*attachmentHeight));
				if(attachment > numAttachments -1)
				{
					clickedAttachment = attachments.get(numAttachments-1);
				}
				else
				{
					clickedAttachment = attachments.get(attachment);	
				}
				if(e.getButton() == MouseEvent.BUTTON1)
				{
					Main.cardBox.setCard(Main.databaseID.get(clickedAttachment.getID()));
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
		/********** Province Menu Items **********/
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
		/********** Deck Menu Items **********/
		else if(name.equals("Shuffle"))
		{
			if(dynastyClicked)
			{
				dynastyDeck.shuffle();
				TextActionListener.send(Main.userName + " shuffles " + Main.gender + " dynasty deck.", "Action");
			}
			else if(fateClicked)
			{
				fateDeck.shuffle();
				TextActionListener.send(Main.userName + " shuffles " + Main.gender + " fate deck.", "Action");
			}
		}
		else if(name.equals("Search"))
		{
			//TODO: Add search interface
		}
		/********** Card Menu Items **********/
		else if(name.equals("Unattach"))
		{
			clickedCard.unattach(clickedAttachment);
		}
	}
}
