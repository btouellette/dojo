package l5r;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.*;
import java.util.HashMap;

public class Main
{
	private static HashMap<String, Card> database;

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI()
    {
        //Create and set up the window
        JFrame frame = new JFrame("L5R Online");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Grab the screen resolution
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int)(screenSize.getWidth()*.75);
        int height = (int)(screenSize.getHeight()*.5);

		//Create the menu bar
		JMenuBar menuBar = new JMenuBar();
		menuBar.setOpaque(true);
		menuBar.setPreferredSize(new Dimension(width, 25));
		menuBar.setLayout(new BoxLayout(menuBar, BoxLayout.X_AXIS));

		//Lay out the Game menu
		JMenu game = new JMenu("Game");
		JMenuItem connect = new JMenuItem("Connect");
		JMenuItem startGame = new JMenuItem("Start Game");
		JMenuItem deck = new JMenuItem("Deckbuilder");
		JMenuItem pref = new JMenuItem("Preferences");
		JMenuItem exit = new JMenuItem("Exit");
		game.add(connect);
		game.add(startGame);
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
		JMenuItem flipCoin = new JMenuItem("Flip Coin");
		JMenuItem randomCard = new JMenuItem("Drop Random Fate Card");
		JMenuItem revealHand = new JMenuItem("Reveal Hand");
		action.add(flipCoin);
		action.addSeparator();
		action.add(randomCard);
		action.add(revealHand);
		//Add the Action menu to the menu bar
		menuBar.add(action);

		//Lay out the About menu
		JMenu about = new JMenu("About");
		JMenuItem aboutDojo = new JMenuItem("About Dojo");
		about.add(aboutDojo);
		//Add the About menu to the menu bar
		menuBar.add(about);

		//Create the top bar
		JSlider sizeSlider = new JSlider(JSlider.HORIZONTAL);
		sizeSlider.setMaximumSize(new Dimension(width/10 , 25));

		//Add some horizontal glue to push the action boxes to the right side
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(new JLabel("Card Size"));
		menuBar.add(sizeSlider);

		//Create the play area
		PlayArea playArea = createPlayArea(width, height);

        //Create the info area (card box, chat box, game info box)
        JPanel infoArea = createInfoArea(width, height);

		//Create a split panel so that the relative size of the areas can be user controlled
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, playArea, infoArea);
		//Set it so that the play area gets all the extra space by default (from resizing the main window)
		splitPane.setResizeWeight(1);
		//Don't use the ugly default Java divider
		splitPane.setUI(new BasicSplitPaneUI());
		splitPane.setDividerSize(5);

        //Set the menu bar and add the label to the content pane
        frame.setJMenuBar(menuBar);
        frame.getContentPane().add(splitPane, BorderLayout.CENTER);

        //Display the window
        frame.pack();
        frame.setVisible(true);
    }

	private static PlayArea createPlayArea(int width, int height)
	{
		//Create the main play area
        PlayArea playArea = new PlayArea(width, height);
        playArea.setOpaque(true);
        playArea.setBackground(Color.WHITE);
        playArea.setPreferredSize(new Dimension(width, height));

        return playArea;
	}

	private static JPanel createInfoArea(int width, int height)
	{
		//Create the chat/info area
        JPanel infoArea = new JPanel();
        infoArea.setOpaque(true);
        infoArea.setBackground(Color.LIGHT_GRAY);
        infoArea.setPreferredSize(new Dimension(width, 175));
        //Add a pretty border to it
		infoArea.setBorder(BorderFactory.createLoweredBevelBorder());
		infoArea.setLayout(new BorderLayout());

		//Create the chat area
		JTextPane chatBox = new JTextPane();
		JTextField chatSend = new JTextField();
		chatBox.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(chatBox);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		chatSend.addActionListener(new TextActionListener(chatBox));
		infoArea.add(scrollPane, BorderLayout.CENTER);
		infoArea.add(chatSend, BorderLayout.SOUTH);

		return infoArea;
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
		    saxParser.parse( new File("cards-complete.xml"), handler );

		} catch (SAXParseException spe) {
		   // Error generated by the parser
		   System.out.println("\n** Parsing error"
			  + ", line " + spe.getLineNumber()
			  + ", uri " + spe.getSystemId());
		   System.out.println("   " + spe.getMessage() );

		} catch (Throwable t) {

			t.printStackTrace();
    	}

    	database = handler.getDatabase();
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