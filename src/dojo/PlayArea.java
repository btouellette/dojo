package dojo;
// PlayArea.java
// Written by Brian Ouellette
// This is the beefy part of the program. It controls the display for the game.

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

class PlayArea extends JPanel implements MouseListener, MouseMotionListener, ActionListener
{
	private static final long serialVersionUID = 1L;
	// The distance from where the card was clicked to the upper left corner of the base card
	// Used for movement logic
	private int distanceX, distanceY;
	// The last card that was clicked on depending on whether it was attached to anything or not
	// If clickedAttachment != null then clickedCard is the base card of the unit
	private PlayableCard clickedCard, clickedAttachment, attachingCard;
	// Tests used for launching context menus correctly
	private boolean cardClicked, attachmentClicked, provClicked;
	private boolean deckClicked, discardClicked;
	private boolean dynastyClicked, fateClicked;
	// Background of the play area (province/deck/hand outlines)
	private BufferedImage background;
	// Which of your provinces was clicked (left to right)
	private int numProvClicked;
	// Context (right-click) menus
	private JPopupMenu popupCard, popupAttachment, popupProv, popupDeck, popupDiscard;
	// Number of provinces
	private int oppNumProv = 4;
	// Size of PlayArea in pixels
	private int height, width;
	// Current size of a single card
	private int cardWidth, cardHeight;
	// Reference height (height that the card is initially)
	private int baseCardHeight;
	// The percent of a card that attachments show above their base card
	private float attachmentHeight = 0.2f;
	// State of the game (decks/cards on table/provinces/etc)
	private GameState state;
	
	public PlayArea(GameState state, int width, int height)
	{
		super();
		setPreferredSize(new Dimension(width, height));
		this.width = width;
		this.height = height;
		this.state = state;

		if(Preferences.cardHeight != 0)
		{
			// Setting the height as the saved height
			cardHeight = Preferences.cardHeight;
		}
		else
		{
			// Default to 1/5 of the PlayArea height
			// This allows for vertically: two sets of provinces and two units with a few attachments
			cardHeight = height/5;
		}
		
		// Normal sized cards are 2.5" wide and 3.5" tall
		cardWidth = (int)(cardHeight*(2.5/3.5));
		baseCardHeight = cardHeight;

		//TODO: Allow for custom backgrounds
		// Set up background image
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		background = new BufferedImage((int)screenSize.getWidth(), (int)screenSize.getHeight(), BufferedImage.TYPE_INT_ARGB);
		redrawBackground();

		// Interaction is handled within the class
		addMouseListener(this);
		addMouseMotionListener(this);
		
		createMenu();
	}
	
	private void createMenu()
	{
		// Creates the context menus used in the application
		//TODO: Flesh out context menus
		//TODO: Generate dynamically depending on what is clicked (add certain items for attached cards/dishonored cards/etc)
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
	}

	// Override the default JComponent paint method
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		// if the parameters changed since last repaint update the preferences and recreate the background
		if(height != getHeight() || width != getWidth())
		{
			height = getHeight();
			width = getWidth();
			redrawBackground();
		}
		
		List<Province> provinces = state.getProvinces();
		BufferedImage currentImage;
		// Draw province attachments before background so they show up behind provinces
		for(Province currentProvince : provinces)
		{
			List<PlayableCard> attachments = currentProvince.getAttachments();
			for(int j = attachments.size() - 1; j >= 0; j--)
			{
				PlayableCard currentCard = attachments.get(j);
				int[] location = currentCard.getLocation();
				currentImage = currentCard.getImage();
				if(currentImage != null)
				{
					g.drawImage(currentImage, location[0], location[1], null);
				}
			}
		}

		// Draw background image (hand/deck/discard/province areas)
		g.drawImage(background, 0, 0, null);
		
		// Draw cards in provinces
		for(Province currentProvince : provinces)
		{
			// Location of the card in the province
			int[] location = currentProvince.getLocation();
			currentImage = currentProvince.getImage();
			if(currentImage != null)
			{
				g.drawImage(currentImage, location[0], location[1], null);
			}
		}

		int startHand = width - (int)(cardWidth*1.5);

		// Draw fate and dynasty decks and discards if they have an image
		currentImage = state.getDynastyDeck().getImage();
		if(currentImage != null)
		{
			g.drawImage(currentImage, cardWidth+10, height - (cardHeight+4), null);
		}
		currentImage = state.getDynastyDiscard().getImage();
		if(currentImage != null)
		{
			g.drawImage(currentImage, 4, height - (cardHeight+4), null);
		}
		currentImage = state.getFateDeck().getImage();
		if(currentImage != null)
		{
			g.drawImage(currentImage, startHand - 2*(cardWidth+5), height - (cardHeight+4), null);
		}
		currentImage = state.getFateDiscard().getImage();
		if(currentImage != null)
		{
			g.drawImage(currentImage, startHand - (cardWidth+4), height - (cardHeight+4), null);
		}

		// Now that we've drawn all the play surface draw all the cards on the table
		List<PlayableCard> cards = state.getAllCards();
		for(PlayableCard card : cards)
		{
			displayCard(card, (Graphics2D)g);
		}
	}

	public void redrawBackground()
	{
		// Recreate background image with deck/province/hand/discard holders
		Graphics2D g = background.createGraphics();
		// Clear out the last background drawn onto this image with a transparent color
		g.setBackground(new Color(255, 255, 255, 0));
		g.clearRect(0, 0, background.getWidth(), background.getHeight());
		// Create an area for you to keep your hand
		int sizeHand = (int)(cardWidth*1.5);
		int startHand = width - sizeHand;
		int location[] = new int[2];

		// Create fate discard
		location[0] = startHand - (cardWidth+4);
		// location[1] remains same throughout since all decks/provinces are on same level
		location[1] = height - (cardHeight+4);
		state.getFateDiscard().setLocation(location);
		g.setColor(Color.BLACK);
		g.fillRect(location[0] - (cardWidth+10), location[1] - 4, 2*(cardWidth+8), cardHeight+8);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(location[0] - 2, location[1] - 2, cardWidth+4, cardHeight+4);

		// Create fate deck
		location[0] = startHand - 2*(cardWidth+5);
		state.getFateDeck().setLocation(location);
		g.fillRect(location[0] - 2, location[1] - 2, cardWidth+4, cardHeight+4);

		// Create dynasty discard
		location[0] = 4;
		state.getDynastyDiscard().setLocation(location);
		g.setColor(Color.BLACK);
		g.fillRect(location[0] - 4, location[1] - 4, 2*(cardWidth+8)-2, cardHeight+8);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(location[0] - 2, location[1] - 2, cardWidth+4, cardHeight+4);

		// Create dynasty deck
		location[0] = cardWidth+10;
		state.getDynastyDeck().setLocation(location);
		g.fillRect(location[0] - 2, location[1] - 2, cardWidth+4, cardHeight+4);

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

		// Create your provinces and draw the cards in them
		int leftBorder = 2*(cardWidth+8)-2;
		int rightBorder = startHand - (2*(cardWidth+8)-2);
		List<Province> provinces = state.getProvinces();
		int yourNumProv = provinces.size();
		int distanceBetween = (rightBorder - leftBorder)/(yourNumProv+1);
		for(int i = 1; i < yourNumProv+1; i++)
		{
			// Location of the card in the province
			location[0] = leftBorder + i*distanceBetween - cardWidth/2 + 4;
			location[1] = height - (cardHeight+2); 
			provinces.get(i-1).setLocation(location);
			// Draw province outline
			g.setColor(Color.BLACK);
			g.fillRect(location[0] - 4, location[1] - 4, cardWidth+8, cardHeight+8);
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(location[0] - 2, location[1] - 2, cardWidth+4, cardHeight+4);
		}

		// Create opponents provinces
		distanceBetween = (rightBorder - leftBorder)/(oppNumProv+1);
		for(int i = 1; i < oppNumProv+1; i++)
		{
			g.setColor(Color.BLACK);
			g.fillRect(leftBorder + i*distanceBetween - cardWidth/2, 0, cardWidth+8, cardHeight+6);
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(leftBorder + i*distanceBetween - cardWidth/2 + 2, 0, cardWidth+4, cardHeight+4);
		}
		g.dispose();
	}

	public void rescale()
	{
		// Rescale the PlayArea appropriately on card size change
		// Update card height and width
		cardHeight = Preferences.sliderValue*baseCardHeight/50;
		cardWidth = (int)(cardHeight*(2.5/3.5));
		//TODO: Update opponents cards too
		// Update background image
		redrawBackground();
	}

	private void displayCard(PlayableCard card, Graphics2D g)
	{
		// Display all the attachments first. This will draw the top attachment first which will be the farthest back on the draw stack
		List<PlayableCard> attachments = card.getAttachments();
		for(int i = attachments.size()-1; i >= 0; i--)
		{
			displayCard(attachments.get(i), g);
		}
		// Now draw the base card in the unit
		int[] location = card.getLocation();
		// Window resize can move a card off the table if we don't keep it on the play area here
		if(location[0] > getWidth() - 10)		
		{		
			location[0] = getWidth() - 10;		
		}		
		else if(location[0] < 10 - cardWidth)		
		{		
			location[0] = 10 - cardWidth;		
		}		
		if(location[1] > getHeight() - 10)		
		{		
			location[1] = getHeight() - 10;		
		}		
		else if(location[1] < 10 - cardHeight)		
		{		
			location[1] = 10 - cardHeight;		
		}
		BufferedImage currentImage = card.getImage();
		if(currentImage != null)
		{
			g.drawImage(currentImage, location[0], location[1], null);
		}
		else
		{
			// Draw a temporary box to show the card if we are downloading the image still
			g.setColor(Color.DARK_GRAY);
			g.drawRect(location[0], location[1], cardHeight, cardWidth);
		}
	}

	public int getCardHeight()
	{
		return cardHeight;
	}

	public int getCardWidth()
	{
		return cardWidth;
	}

	public float getAttachmentHeight()
	{
		return attachmentHeight;
	}

	// Pop up the correct context menu
	private void showPopup(MouseEvent e)
	{
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
		// On a double click launch correct doubleClicked method
		if(e.getClickCount() == 2)
		{
			if(deckClicked)
			{
				if(dynastyClicked)
				{
					state.getDynastyDeck().doubleClicked();
				}
				else
				{
					state.getFateDeck().doubleClicked();
				}
			}
			else if(discardClicked)
			{
				if(dynastyClicked)
				{
					state.getDynastyDiscard().doubleClicked();
				}
				else
				{
					state.getFateDiscard().doubleClicked();
				}
			}
			else if(attachmentClicked)
			{
				clickedAttachment.doubleClicked();
			}
			else if(cardClicked)
			{
				clickedCard.doubleClicked();
			}
			else if(provClicked)
			{
				state.getProvinces().get(numProvClicked).doubleClicked();
			}
			// Something probably changed so repaint
			repaint();
		}
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{		
		// On mouse click check to see if anything relevant was clicked
		cardClicked = false;
		attachmentClicked = false;
		deckClicked = false;
		dynastyClicked = false;
		fateClicked = false;
		provClicked = false;
		discardClicked = false;
		Rectangle cardArea = new Rectangle();
		Point clickPoint = e.getPoint();
		
		List<PlayableCard> cards = state.getAllCards();
		List<Province> provinces = state.getProvinces();

		int i = cards.size();
		// Search first to see if any cards displayed on the JPanel were clicked
		// This is in reverse order so topmost drawn cards are picked up first
		while(!cardClicked && i > 0)
		{
			i--;
			clickedCard = cards.get(i);
			int[] cardLocation = clickedCard.getLocation();	
			cardArea.setLocation(cardLocation[0], cardLocation[1]);
			if(clickedCard.isBowed())
			{
				cardArea.setSize(cardHeight, cardWidth);
			}
			else
			{
				cardArea.setSize(cardWidth, cardHeight);			
			}
			
			// See if the displayed card was clicked
			if(cardArea.contains(clickPoint))
			{
				cardClicked = true;
				attachmentClicked = false;
				// Redraw the clicked card on top of the draw stack
				cards.remove(clickedCard);
				cards.add(clickedCard);
				// Store the distance between the click and the root of the card to update location on drag correctly
				distanceX = (int)clickPoint.getX() - cardLocation[0];
				distanceY = (int)clickPoint.getY() - cardLocation[1];
				// Update the card box if we clicked a card
				if(e.getButton() == MouseEvent.BUTTON1 && clickedCard.isFaceUp() && !(clickedCard.isToken()))
				{
					Main.cardBox.setCard(Main.databaseID.get(clickedCard.getID()));
				}
			}
			// If not check its attachments
			else
			{
				List<PlayableCard> attachments = clickedCard.getAllAttachments();
				// Look through all the attachments in order
				int j = 0;
				while(!attachmentClicked && j < attachments.size())
				{
					clickedAttachment = attachments.get(j);
					cardLocation = clickedAttachment.getLocation();	
					cardArea.setLocation(cardLocation[0], cardLocation[1]);
					if(clickedAttachment.isBowed())
					{
						cardArea.setSize(cardHeight, cardWidth);
					}
					else
					{
						cardArea.setSize(cardWidth, cardHeight);			
					}
					if(cardArea.contains(clickPoint))
					{
						cardClicked = true;
						attachmentClicked = true;
						// Redraw the clicked card on top of the draw stack
						cards.remove(clickedCard);
						cards.add(clickedCard);
						// Store the distance between the click and the root of the card to update location on drag correctly
						distanceX = (int)clickPoint.getX() - cardLocation[0];
						distanceY = (int)clickPoint.getY() - cardLocation[1];
						// Update the card box if we clicked a card
						if(e.getButton() == MouseEvent.BUTTON1 && clickedAttachment.isFaceUp())
						{
							Main.cardBox.setCard(Main.databaseID.get(clickedAttachment.getID()));
						}
					}
					j++;
				}
			}
		}
		// If there wasn't a card clicked check to see if any of the provinces were
		if(!cardClicked)
		{	
			int leftBorder = 2*(cardWidth+8)-2;
			int startHand = width - (int)(cardWidth*1.5);
			int rightBorder = startHand - (2*(cardWidth+8)-2);
			int yourNumProv = provinces.size();
			int distanceBetween = (rightBorder - leftBorder)/(yourNumProv+1);
			Rectangle area = new Rectangle();
			// Check each of the provinces
			for(int k = 1; k < yourNumProv+1; k++)
			{
				area.setLocation(leftBorder + k*distanceBetween - cardWidth/2 + 2, height - (cardHeight+4));
				area.setSize(cardWidth+8, cardHeight+4);
				if(area.contains(clickPoint))
				{
					provClicked = true;
					numProvClicked = k-1;
				}
				// If the province wasn't clicked check if any of the province's attachments were clicked
				else
				{
					List<PlayableCard> attachments = provinces.get(k-1).getAttachments();
					// Go through all the attachments in order
					int j = 0;
					while(!attachmentClicked && j < attachments.size())
					{
						clickedAttachment = attachments.get(j);
						int[] cardLocation = clickedAttachment.getLocation();	
						area.setLocation(cardLocation[0], cardLocation[1]);
						if(clickedAttachment.isBowed())
						{
							area.setSize(cardHeight, cardWidth);
						}
						else
						{
							area.setSize(cardWidth, cardHeight);			
						}
						if(area.contains(clickPoint))
						{
							attachmentClicked = true;
							numProvClicked = k-1;
							// Update the card box if we clicked a card
							if(e.getButton() == MouseEvent.BUTTON1 && clickedAttachment.isFaceUp())
							{
								Main.cardBox.setCard(Main.databaseID.get(clickedAttachment.getID()));
							}
						}
						j++;
					}
				}
			}
		}
		// If nothing has been detected as clicked yet check for deck clicks
		if(!cardClicked && !attachmentClicked && !provClicked)
		{
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
		}
		// If there was a card queued up to attach see if we can attach it to whatever was clicked
		if(attachingCard != null)
		{
			// Only attach a card to another if they aren't the same
			if(cardClicked && attachingCard != clickedCard)
			{
				clickedCard.attach(attachingCard);
			}
			else if(provClicked)
			{
				provinces.get(numProvClicked).attach(attachingCard);
			}
			// Discard queued attach action if nothing valid was clicked
			attachingCard = null;
		}
		// If something was attached to a province or a card was clicked (and the draw stack reordered) we'll need to repaint
		if(cardClicked || provClicked)
		{
			repaint();
		}
		// Show appropriate right click menus
		if(e.isPopupTrigger())
		{
			showPopup(e);
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		if(cardClicked)
		{
			// Check if card was dragged into a discard
			int[] location = clickedCard.getLocation();
			int startHand = width - (int)(cardWidth*1.5);
			// Detect if a card was dragged into a discard or deck
			if(location[1] == height - (cardHeight+4))
			{
				// This location corresponds to the snap (and display) points for cards in the discard
				if(location[0] == 4 || location[0] == startHand - (cardWidth+4))
				{
					clickedCard.destroy();
					// Discard image has changed so repaint
					repaint();
				}
				// This location corresponds to the snap (and display) points for cards in the decks
				else if((location[0] == cardWidth+10 || location[0] == startHand - 2*(cardWidth+5)) && !clickedCard.isToken())
				{
					clickedCard.moveToDeck();
					// Discard image has changed so repaint
					repaint();
				}
			}
			// Now check provinces and only move into a province if they are dragging a single dynasty card into it
			if(clickedCard.isDynasty() && clickedCard.getAllAttachments().isEmpty())
			{
				for(Province province : state.getProvinces())
				{
					// And then only if the province is empty
					if(province.isEmpty() && Arrays.equals(location, province.getLocation()))
					{
						clickedCard.moveToProvince(province);
						repaint();
					}
				}
			}
		}
		// Show appropriate right-click menus (has to be here as well as mousePressed for cross-platform compatibility
		if(e.isPopupTrigger())
		{
			showPopup(e);
		}
	}

	public void mouseDragged(MouseEvent e)
	{
		// e.getButton() doesn't work inside mouseDragged so SwingUtilities static method is used instead
		if(cardClicked && SwingUtilities.isLeftMouseButton(e))
		{
			Point clickPoint = e.getPoint();
			int newX = (int)clickPoint.getX() - distanceX;
			int newY = (int)clickPoint.getY() - distanceY;
			
			// Don't let them drag cards off the playing field entirely, apply a 10 pixel boundary to all sides
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
			// Detect if dragged into dynasty discard and update location to be perfectly inside if so
			Rectangle area = new Rectangle(2, height - (cardHeight+6), cardWidth+4, cardHeight+4);
			if(area.contains(clickPoint))
			{
				newX = 4;
				newY = height - (cardHeight+4);
			}
			// Detect if dragged into fate discard and update location if so
			int startHand = width - (int)(cardWidth*1.5);
			area.setLocation(startHand - (cardWidth+6), height - (cardHeight+6));
			if(area.contains(clickPoint))
			{
				newX = startHand - (cardWidth+4);
				newY = height - (cardHeight+4);
			}
			// Don't snap tokens into deck/provinces
			if(!clickedCard.isToken())
			{
				// Detect if dragged into dynasty deck
				area.setLocation(cardWidth+8, height - (cardHeight+6));
				if(area.contains(clickPoint))
				{
					newX = cardWidth+10;
					newY = height - (cardHeight+4);
				}
				// Detect if dragged into fate deck
				area.setLocation(startHand - 2*(cardWidth+4), height - (cardHeight+6));
				if(area.contains(clickPoint))
				{
					newX = startHand - 2*(cardWidth+5);
					newY = height - (cardHeight+4);
				}
				// Detect if dragged into a province and a valid target to be put in one
				for(Province province : state.getProvinces())
				{
					int[] location = province.getLocation();
					area.setLocation(location[0]-2, location[1]-2);
					if(area.contains(clickPoint) && clickedCard.getAllAttachments().isEmpty() && clickedCard.isDynasty())
					{
						newX = location[0];
						newY = location[1];					
					}
				}
			}
			
			// Detect if dragged near hand border and stall so it is obvious which the card is in
			if(newX < startHand && newX > startHand - cardWidth)
			{
				if(!state.handContains(clickedCard))
				{
					newX = startHand - cardWidth;
				}
				else
				{
					newX = startHand;
				}
			}

			// Check if card was dragged into hand
			if(newX >= startHand && !state.handContains(clickedCard))
			{
				if(state.removeFromTable(clickedCard))
				{
					state.addToHand(clickedCard);
				}
			}
			// Check if card was dragged out of hand
			else if(newX < startHand && state.handContains(clickedCard))
			{
				if(state.removeFromHand(clickedCard))
				{
					state.addToTable(clickedCard);
				}
			}
			
			// Update location and repaint since card has been moved
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
		List<Province> provinces = state.getProvinces();
		//TODO: Fill out rest of context menu and actions
		/********** Province Menu Items **********/
		if(name.equals("Destroy"))
		{
			Province province = provinces.get(numProvClicked);
			province.destroy();
			provinces.remove(province);
			redrawBackground();
			repaint();
		}
		else if(name.equals("Add Province On Left"))
		{
			provinces.add(numProvClicked, new Province());
			redrawBackground();
			repaint();
		}
		else if(name.equals("Add Province On Right"))
		{
			provinces.add(numProvClicked+1, new Province());
			redrawBackground();
			repaint();
		}
		/********** Deck Menu Items **********/
		else if(name.equals("Shuffle"))
		{
			// Shuffle deck and notify chat box
			if(dynastyClicked)
			{
				state.getDynastyDeck().shuffle();
				TextActionListener.send(Preferences.userName + " shuffles " + Preferences.gender + " dynasty deck.", "Action");
			}
			else if(fateClicked)
			{
				state.getFateDeck().shuffle();
				TextActionListener.send(Preferences.userName + " shuffles " + Preferences.gender + " fate deck.", "Action");
			}
		}
		else if(name.equals("Search"))
		{
			//TODO: Add search interface
		}
		/********** Card Menu Items **********/
		else if(name.equals("Unattach"))
		{
			if(cardClicked)
			{
				clickedCard.unattach(clickedAttachment);
			}
			else
			{
				provinces.get(numProvClicked).unattach(clickedAttachment);
			}
			repaint();
		}
		else if(name.equals("Attach"))
		{
			attachingCard = clickedCard;
		}
	}
}
