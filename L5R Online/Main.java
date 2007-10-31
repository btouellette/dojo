// Main.java
// Written by Brian Ouellette
// Part of Dojo

package l5r;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.*;
import java.util.HashMap;

class Main
{
	public static HashMap<String, StoredCard> database;
	public static String userName, gender;
	public static JTextPane chatBox;
	public static CardInfoBox cardBox;
	public static boolean highRes;
	public static PlayArea playArea;

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI()
    {
        //Create and set up the window
        JFrame frame = new JFrame("Dojo - L5R Online");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Set up defaults if pref file is unavailable

        //Grab the screen resolution
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //Default size is to take up 3/4 of the screen horizontally and 1/2 of the screen vertically
        //If there is a stored preference file then use those numbers
        int width = (int)(screenSize.getWidth()*.75);
        int height = (int)(screenSize.getHeight()*.5);

        //Default username
        userName = "New Player";
        gender = "Male";

		//Create the play area
		playArea = createPlayArea(width, height);

		//Create the info area (card box, chat box, game info box)
		JSplitPane infoArea = createInfoArea(width, height);

		//Create a split panel so that the relative size of the areas can be user controlled
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, playArea, infoArea);
		//Set it so that the play area gets all the extra space by default (from resizing the main window)
		splitPane.setResizeWeight(1);
		//Don't use the ugly default Java divider
		splitPane.setUI(new BasicSplitPaneUI());
		splitPane.setDividerSize(5);

        //Set the menu bar and add the label to the content pane
        frame.setJMenuBar(createMenuBar(width, playArea));
        frame.getContentPane().add(splitPane, BorderLayout.CENTER);

        //Display the window
        frame.pack();
        frame.setVisible(true);
    }

    private static JMenuBar createMenuBar(int width, PlayArea playArea)
    {
		//Create the menu bar
		JMenuBar menuBar = new JMenuBar();
		menuBar.setOpaque(true);
		//Size is across the entire application and 25 pixels high
		menuBar.setPreferredSize(new Dimension(width, 25));
		//We use a BoxLayout so that we can easily add other components to the menu bar and space them how we want
		menuBar.setLayout(new BoxLayout(menuBar, BoxLayout.X_AXIS));

		//Lay out the Game menu
		JMenu game = new JMenu("Game");
		//Prevent the menu button from being occluded if the layout is condensed
		game.setMinimumSize(game.getPreferredSize());
		MenuListener menuListener = new MenuListener();
		JMenuItem connect = new JMenuItem("Connect");
		connect.addActionListener(menuListener);
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
		game.add(startGame);
		game.add(findGame);
		game.addSeparator();
		game.add(deck);
		game.addSeparator();
		game.add(pref);
		game.addSeparator();
		game.add(exit);
		//Add the Game menu to the menu bar
		menuBar.add(game);

		//Lay out the Action menu
		JMenu action = new JMenu("Action");
		//Prevent the menu button from being occluded if the layout is condensed
		action.setMinimumSize(action.getPreferredSize());
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
		//Add the Action menu to the menu bar
		menuBar.add(action);

		//Lay out the About menu
		JMenu about = new JMenu("About");
		JMenuItem aboutDojo = new JMenuItem("About Dojo");
		aboutDojo.addActionListener(menuListener);
		about.add(aboutDojo);
		//Add the About menu to the menu bar
		menuBar.add(about);

		//Create the top bar
		//Create the card size slider
		// TODO: More intelligent max/min for the slider
		JSlider sizeSlider = new JSlider(JSlider.HORIZONTAL);
		sizeSlider.addChangeListener(new SliderListener());
		sizeSlider.setPreferredSize(new Dimension(100, 25));
		sizeSlider.setMaximumSize(sizeSlider.getPreferredSize());
		//Make it so the menu background color shows through behind the slider
		sizeSlider.setOpaque(false);

		//Create the token generator (naming box and button)
		JComboBox tokenName = new JComboBox();
		tokenName.setPreferredSize(new Dimension(175, 23));
		tokenName.setMaximumSize(tokenName.getPreferredSize());
		//Indent the text by 3 pixels
		((BasicComboBoxRenderer)tokenName.getRenderer()).setBorder(new EmptyBorder(0,3,0,0));
		//Prevent the popup from appearing on the first click
		tokenName.setEnabled(false);
		tokenName.setForeground(Color.LIGHT_GRAY);
		tokenName.addItem("Create token");
		TokenActionListener tokenListener = new TokenActionListener();
		tokenName.addMouseListener(tokenListener);
		tokenName.addActionListener(tokenListener);

		//Create the buttons
		JButton unbowButton = new JButton("Unbow All");
		//No reason for the button to hold focus
		unbowButton.setFocusable(false);

		JButton turnButton = new JButton("End Turn");
		//No reason for the button to hold focus
		turnButton.setFocusable(false);

		ButtonListener buttonListener = new ButtonListener();
		unbowButton.addActionListener(buttonListener);
		turnButton.addActionListener(buttonListener);

		//Add some horizontal glue to push the action boxes to the right side
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

		return(menuBar);
	}

	private static PlayArea createPlayArea(int width, int height)
	{
		//Create the main play area
        PlayArea playAreaPanel = new PlayArea(width, height);
        playAreaPanel.setOpaque(true);
        // TODO: Allow for custom backgrounds
        playAreaPanel.setBackground(Color.WHITE);
        // TODO: Save the last used size and load it here
        playAreaPanel.setPreferredSize(new Dimension(width, height));

        return playAreaPanel;
	}

	private static JSplitPane createInfoArea(int width, int height)
	{
		//Create the chat/info area
        JPanel infoArea = new JPanel();
        infoArea.setOpaque(true);
        infoArea.setBackground(Color.LIGHT_GRAY);
        // TODO: Remember the previous size of all these windows
        infoArea.setPreferredSize(new Dimension(2*width/4, 175));
        //Add a pretty border to it
		infoArea.setBorder(BorderFactory.createLoweredBevelBorder());
		infoArea.setLayout(new BorderLayout());

		//Create the chat area
		chatBox = new JTextPane();
		JTextField chatSend = new JTextField();
		chatBox.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(chatBox);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		chatSend.addActionListener(new TextActionListener());

		infoArea.add(scrollPane, BorderLayout.CENTER);
		infoArea.add(chatSend, BorderLayout.SOUTH);

		//Create the card info box
		cardBox = new CardInfoBox();
		cardBox.setPreferredSize(new Dimension(width/4, 175));
		JScrollPane cardBoxScrollPane = new JScrollPane(cardBox);

		GameInfoBox gameInfo = new GameInfoBox();
		gameInfo.setPreferredSize(new Dimension(width/4, 175));

		//Set up JSplitPanes to allow for dynamic resizing
		// TODO: Allow corner dragging to resize multiple SplitPanes. Would have to use a different component. Possible?
		JSplitPane outerInfoArea1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, cardBoxScrollPane, infoArea);
		//Set it so that the chatbox gets all the extra space by default (from resizing the main window)
		outerInfoArea1.setResizeWeight(0);
		JSplitPane outerInfoArea2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, outerInfoArea1, gameInfo);
		//Set it so that the chatbox gets all the extra space by default (from resizing the main window)
		outerInfoArea2.setResizeWeight(1);

		outerInfoArea1.setUI(new BasicSplitPaneUI());
		outerInfoArea1.setDividerSize(5);

		outerInfoArea2.setUI(new BasicSplitPaneUI());
		outerInfoArea2.setDividerSize(5);

		cardBox.setCard(database.get("WoE091"));

		return outerInfoArea2;
	}

	private static void importDatabase()
	{
		// Initialize the SAX event handler
		Importer handler = new Importer();

		// Use the default (non-validating) parser
		SAXParserFactory factory = SAXParserFactory.newInstance();
        try
        {
		 	// Parse the input
		    SAXParser saxParser = factory.newSAXParser();
		    System.out.print("Loading card database: ");
		    saxParser.parse( new File("cards.xml"), handler );

		} catch (SAXParseException spe) {
		   // Error generated by the parser
		   System.out.println("\n** Parsing error"
			  + ", line " + spe.getLineNumber()
			  + ", uri " + spe.getSystemId());
		   System.out.println("   " + spe.getMessage() );

		} catch (IOException io) {
			// File is not there or unreadable
			System.err.println(io);
			// TODO: Fetch the cards database from Kamisasori and unzip it
			// http://www.kamisasori.net/files/cards-complete.zip
			System.exit(1);

		} catch (Throwable t) {

			t.printStackTrace();
    	}

    	database = handler.getDatabase();
    	System.out.println("success");
	}

    public static void main(String[] args)
    {
    	//Read in cards.xml
    	importDatabase();

        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                createAndShowGUI();
            }
        });
    }
}