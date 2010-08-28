package dojo;
// Main.java
// Written by Brian Ouellette
// Launcher program. Sets up the GUI and sets everything up.

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.zip.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import javax.xml.parsers.*;
import org.xml.sax.*;

class Main
{
	// Database of all cards in XML, ID or name maps to card
	static Map<String, StoredCard> databaseID, databaseName;
	//TODO: It'd be nice to be able to set the font size of these elements in preferences somewhere
	// Box for displaying chat messages
	static JTextPane chatBox;
	// Box for displaying clicked card info
	static CardInfoBox cardBox;
	// Main playing surface
	static PlayArea playArea;
	// Object representing current game state
	static GameState state;
	static JFrame frame;
	// Interface to the network
	static Network network;

	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event-dispatching thread.
	 */
	private static void createAndShowGUI()
	{
		// Create and set up the window
		frame = new JFrame("Dojo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Attempt to import preferences from config file
		Preferences.importPreferences();
		int width = Preferences.width;
		int height = Preferences.height;

		// Initialize a fresh game state
		state = new GameState();

		// Create the play area associated with this game state
		playArea = new PlayArea(state, width, height);
		playArea.setOpaque(true);

		// Load in various images used in the program
		StoredImages.loadImages();

		// Create the info area (card box, chat box, game info box)
		JSplitPane infoArea = createInfoArea(width);

		// Create a split panel so that the relative size of the areas can be user controlled
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, playArea, infoArea);
		// Set it so that the play area gets all the extra space by default (from resizing the main window)
		splitPane.setResizeWeight(1);
		// Don't use the ugly default Java divider
		splitPane.setUI(new BasicSplitPaneUI());
		splitPane.setDividerSize(5);

		// Set the menu bar and add the label to the content pane
		frame.setJMenuBar(createMenuBar(width));
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);

		// Display the window
		frame.pack();

		frame.setVisible(true);

		// Read in cards.xml
		//TODO: Prevent instantiation of StoredCard before this finishes
		new Thread() {
			public void run() {
				importDatabase();
			}
		}.start();
	}

	private static JMenuBar createMenuBar(int width)
	{
		// Create the menu bar
		JMenuBar menuBar = new JMenuBar();
		menuBar.setOpaque(true);
		// Size is across the entire application and 25 pixels high
		menuBar.setPreferredSize(new Dimension(width, 25));
		// We use a BoxLayout so that we can easily add other components to the menu bar and space them how we want
		menuBar.setLayout(new BoxLayout(menuBar, BoxLayout.X_AXIS));

		// Lay out the Game menu
		JMenu game = new JMenu("Game");
		// Prevent the menu button from being occluded if the layout is condensed
		game.setMinimumSize(game.getPreferredSize());
		// Add buttons and associate listener with them
		MenuListener menuListener = new MenuListener();
		JMenuItem connect = new JMenuItem("Connect");
		connect.addActionListener(menuListener);
		JMenuItem load = new JMenuItem("Load Deck");
		load.addActionListener(menuListener);
		JMenuItem startGame = new JMenuItem("Start Game");
		startGame.addActionListener(menuListener);
		JMenuItem findGame = new JMenuItem("Find Game");
		findGame.addActionListener(menuListener);
		JMenuItem deck = new JMenuItem("Deckbuilder");
		deck.addActionListener(menuListener);
		JMenuItem pref = new JMenuItem("Preferences");
		pref.addActionListener(menuListener);
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(menuListener);
		game.add(connect);
		game.add(load);
		game.add(startGame);
		game.add(findGame);
		game.addSeparator();
		game.add(deck);
		game.addSeparator();
		game.add(pref);
		game.addSeparator();
		game.add(exit);
		// Add the Game menu to the menu bar
		menuBar.add(game);

		// Lay out the Action menu
		JMenu action = new JMenu("Action");
		// Prevent the menu button from being occluded if the layout is condensed
		action.setMinimumSize(action.getPreferredSize());
		// Add buttons and associate listener with them
		JMenuItem flipCoin = new JMenuItem("Flip Coin");
		flipCoin.addActionListener(menuListener);
		JMenuItem randomCard = new JMenuItem("Drop Random Fate Card");
		randomCard.addActionListener(menuListener);
		JMenuItem revealHand = new JMenuItem("Reveal Hand");
		revealHand.addActionListener(menuListener);
		action.add(flipCoin);
		action.addSeparator();
		action.add(randomCard);
		action.add(revealHand);
		// Add the Action menu to the menu bar
		menuBar.add(action);

		// Lay out the About menu
		JMenu about = new JMenu("About");
		JMenuItem aboutDojo = new JMenuItem("About Dojo");
		aboutDojo.addActionListener(menuListener);
		about.add(aboutDojo);
		// Add the About menu to the menu bar
		menuBar.add(about);

		// Create the top bar
		// Create the card size slider
		//TODO: More intelligent max/min for the slider
		JSlider sizeSlider = new JSlider(JSlider.HORIZONTAL);
		sizeSlider.addChangeListener(new SliderListener());
		sizeSlider.setValue(Preferences.sliderValue);
		sizeSlider.setPreferredSize(new Dimension(100, 25));
		sizeSlider.setMaximumSize(sizeSlider.getPreferredSize());
		// Make it so the menu background color shows through behind the slider
		sizeSlider.setOpaque(false);

		// Create the token generator (naming box and button)
		JComboBox tokenName = new JComboBox();
		tokenName.setPreferredSize(new Dimension(175, 23));
		tokenName.setMaximumSize(tokenName.getPreferredSize());
		// Indent the text by 3 pixels
		((BasicComboBoxRenderer)tokenName.getRenderer()).setBorder(new EmptyBorder(0,3,0,0));
		// Prevent the popup from appearing on the first click
		tokenName.setEnabled(false);
		tokenName.setForeground(Color.LIGHT_GRAY);
		tokenName.addItem("Create token");
		TokenActionListener tokenListener = new TokenActionListener();
		tokenName.addMouseListener(tokenListener);
		tokenName.addActionListener(tokenListener);

		// Create the buttons
		JButton unbowButton = new JButton("Unbow All");
		// No reason for the button to hold focus
		unbowButton.setFocusable(false);

		JButton turnButton = new JButton("End Turn");
		// No reason for the button to hold focus
		turnButton.setFocusable(false);

		ButtonListener buttonListener = new ButtonListener();
		unbowButton.addActionListener(buttonListener);
		turnButton.addActionListener(buttonListener);

		// Add some horizontal glue to push the action boxes to the right side
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(new JLabel("Card Size"));
		menuBar.add(sizeSlider);
		menuBar.add(Box.createHorizontalStrut(10));
		menuBar.add(tokenName);
		menuBar.add(Box.createHorizontalStrut(10));
		menuBar.add(unbowButton);
		menuBar.add(Box.createHorizontalStrut(3));
		menuBar.add(turnButton);
		menuBar.add(Box.createHorizontalStrut(3));

		return menuBar;
	}

	private static JSplitPane createInfoArea(int width)
	{
		// Create the chat/info area
		JPanel infoArea = new JPanel();
		infoArea.setOpaque(true);
		infoArea.setBackground(Color.LIGHT_GRAY);
		infoArea.setPreferredSize(new Dimension(width/2, 175));
		if(Preferences.infoAreaHeight != 0)
		{
			infoArea.setPreferredSize(new Dimension(width/2, Preferences.infoAreaHeight));
		}
		// Register a listener to update the split height in preferences when the user resizes
		infoArea.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) {
				Preferences.infoAreaHeight = ((JPanel)e.getComponent()).getHeight();
			}
			public void componentMoved(ComponentEvent e) {}
			public void componentShown(ComponentEvent e) {}
			public void componentHidden(ComponentEvent e) {}
		});
		//Add a pretty border to it
		infoArea.setBorder(BorderFactory.createLoweredBevelBorder());
		infoArea.setLayout(new BorderLayout());

		// Create the chat area
		chatBox = new JTextPane();
		JTextField chatSend = new JTextField();
		chatBox.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(chatBox);
		// Always display a scroll bar
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		chatSend.addActionListener(new TextActionListener());

		infoArea.add(scrollPane, BorderLayout.CENTER);
		infoArea.add(chatSend, BorderLayout.SOUTH);

		// Create the card info box
		cardBox = new CardInfoBox();
		cardBox.setPreferredSize(new Dimension(width/4, 175));
		JScrollPane cardBoxScrollPane = new JScrollPane(cardBox);

		// Create the game info box
		GameInfoBox gameInfo = new GameInfoBox();
		gameInfo.setPreferredSize(new Dimension(width/4, 175));

		// Set up JSplitPanes to allow for dynamic resizing
		//TODO: Allow corner dragging to resize multiple SplitPanes
		//      Would have to use a different component since SplitPanes only take 2 components
		//      Might be possible with org.jdesktop.swingx.JXMultiSplitPane (not yet in mainline swing)
		JSplitPane outerInfoArea1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, cardBoxScrollPane, infoArea);
		// Set it so that the chatbox gets all the extra space by default (from resizing the main window)
		outerInfoArea1.setResizeWeight(0);
		if(Preferences.cardBoxSplitWidth != 0)
		{
			// Set the divider to the saved location
			outerInfoArea1.setDividerLocation(Preferences.cardBoxSplitWidth);
		}
		// Register a listener to update the split height in preferences when the user resizes
		outerInfoArea1.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				Preferences.cardBoxSplitWidth = ((Number)e.getNewValue()).intValue();;
			}
		});
		
		JSplitPane outerInfoArea2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, outerInfoArea1, gameInfo);
		// Set it so that the chatbox gets all the extra space by default (from resizing the main window)
		outerInfoArea2.setResizeWeight(1);
		if(Preferences.gameInfoSplitWidth != 0)
		{
			// Set the divider to the saved location
			outerInfoArea2.setDividerLocation(Preferences.gameInfoSplitWidth);
		}
		// Register a listener to update the split height in preferences when the user resizes
		outerInfoArea2.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				Preferences.gameInfoSplitWidth = ((Number)e.getNewValue()).intValue();;
			}
		});

		outerInfoArea1.setUI(new BasicSplitPaneUI());
		outerInfoArea1.setDividerSize(5);

		outerInfoArea2.setUI(new BasicSplitPaneUI());
		outerInfoArea2.setDividerSize(5);

		return outerInfoArea2;
	}

	private static void importDatabase()
	{
		// Initialize the SAX event handler
		XMLImporter handler = new XMLImporter();

		// Use the default (non-validating) parser
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
		 	// Parse the input
			SAXParser saxParser = factory.newSAXParser();
			System.out.print("Loading card database: ");
			saxParser.parse(new File("cards.xml"), handler);
			databaseID = handler.getIDDatabase();
			databaseName = handler.getNameDatabase();
			System.out.println("success!");
		} catch (SAXParseException spe) {
			// Error generated by the parser
			System.err.println("\n** Parsing error"
			  + ", line " + spe.getLineNumber()
			  + ", uri " + spe.getSystemId());
			System.err.println("   " + spe.getMessage() );
		} catch (IOException io) {
			System.out.print("failed\n** Card database missing.\n** Attempting to get from kamisasori.net: ");
			// Database not present so try to get database off kamisasori.net
			//TODO: Also do this if someone you connect to has a newer database
			try {
				// Create an input stream to the appropriate card file
				URL url = new URL("http://kamisasori.net/files/cards-complete.zip");
				int fileSize = url.openConnection().getContentLength();
				ProgressMonitorInputStream is = new ProgressMonitorInputStream(frame, "Downloading cards database...", url.openStream());
				is.getProgressMonitor().setMaximum(fileSize);
				FileOutputStream fos = new FileOutputStream("cards.zip");
				// And read the input stream in
				for (int c = is.read(); c != -1; c = is.read()) {
					fos.write(c);
				}
				is.close();
				fos.close();
				System.out.println("success!");
				
				// Unzip database
				InputStream fis = new ProgressMonitorInputStream(frame, "Unzipping cards database...", new FileInputStream("cards.zip"));
				ZipInputStream zis = new ZipInputStream(fis);
				ZipEntry ze;
				// Unzip everything inside the file (should just be cards.xml)
				while ((ze = zis.getNextEntry()) != null) {
					System.out.print("** Unzipping " + ze.getName() + ": ");
					if(ze.getName().equals("cards-complete.xml"))
					{
						fos = new FileOutputStream("cards.xml");
					}
					else
					{
						fos = new FileOutputStream(ze.getName());
					}
					// And write unzipped data out to the file
					for (int c = zis.read(); c != -1; c = zis.read()) {
						fos.write(c);
					}
					zis.closeEntry();
					fos.close();
					System.out.println("success!");
				}
				zis.close();

				// Cleanup temp files downloaded
				System.out.print("** Deleting zip file after extraction: ");
				File f = new File("cards.zip");
				f.delete();
				System.out.println("success!");
				// Successfully got database so read it in again
				System.out.println("** Reattempting database import");
				importDatabase();
			} catch (InterruptedIOException io_e) {
				TextActionListener.send("Card database necessary, please restart Dojo", "Error");
				File f1 = new File("cards.zip");
				if(f1.exists())
				{
					f1.delete();
				}
				File f2 = new File("cards.xml");
				if(f2.exists())
				{
					f2.delete();
				}
			} catch (IOException io_e) {
				System.err.println("failed. Check your internet connection.");
				File f1 = new File("cards.zip");
				if(f1.exists())
				{
					f1.delete();
				}
				File f2 = new File("cards.xml");
				if(f2.exists())
				{
					f2.delete();
				}
				io_e.printStackTrace();
				System.exit(1);
			}
		} catch (ParserConfigurationException t) {
			System.err.println("\n** Unknown failure: Please report at http://code.google.com/p/dojo/issues/entry");
			t.printStackTrace();
			System.exit(1);
		} catch (SAXException t) {
			System.err.println("\n** Unknown failure: Please report at http://code.google.com/p/dojo/issues/entry");
			t.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String[] args)
	{
		// Add shutdown hook for writing out preferences
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				Preferences.writePreferences();
			}
		});
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
		
		network = new Network();
		network.start();
	}
}
