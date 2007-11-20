package l5r;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.io.*;
import java.util.*;

class Deckbuilder
{

	public static JFrame frame;
	static ArrayList<String> legalChoices = new ArrayList<String>();
	static String[] array = {"Legal: ","Card Type: ","Clan: ","Title: ","Text: ",
							 "Gold Cost: ","Force: ","Chi: ","Honor Req.: ","PH: ",
							 "Focus Value: "};
	static ArrayList<String> types = new ArrayList<String>();
	static ArrayList<String> clans = new ArrayList<String>();
	public static JLabel[] c = new JLabel[11];
	public static CardInfoBox card;
	public static JList list;
	public static Vector<StoredCard> vect;
	public static JLabel apples;
	public static JScrollPane listScroller;
	public static JComboBox Legal,CardType,faction;
	public static JTextField title;

	public Deckbuilder()
	{
		for (int i=0;i<11;i++)
		{
			c[i] = new JLabel(array[i]);
			c[i].setFont(new Font("Serif", Font.PLAIN, 13));
		}
		Object[] p = Main.database.keySet().toArray();

		vect = new Vector<StoredCard>();

		types.add("");
		clans.add("");

		for(int i = 0; i < p.length; i++)
		{
			StoredCard currentCard = Main.database.get(p[i]);
			vect.add(Main.database.get(p[i]));

			ArrayList<String> currentCardLegal = currentCard.getLegal();
			for(int k = 0; k < currentCardLegal.size(); k++)
			{
				if(!legalChoices.contains(currentCardLegal.get(k)))
				{
					legalChoices.add(currentCardLegal.get(k));
				}
			}

			String currentCardType = currentCard.getType();
			if(!types.contains(currentCardType))
			{
				types.add(currentCardType);
			}

			ArrayList<String> currentCardClan = currentCard.getClan();
			for(int k = 0; k < currentCardClan.size(); k++)
			{
				if(!clans.contains(currentCardClan.get(k)))
				{
					clans.add(currentCardClan.get(k));
				}
			}
		}

		for(int i = 0; i < legalChoices.size(); i++)
		{
			String temp = legalChoices.get(i).substring(0,1).toUpperCase() + legalChoices.get(i).substring(1);
			legalChoices.set(i, temp);
		}

		for(int i = 1; i < types.size(); i++)
		{
			String temp = types.get(i).substring(0,1).toUpperCase() + types.get(i).substring(1);
			types.set(i, temp);
		}

		for(int i = 1; i < clans.size(); i++)
		{
			String temp = clans.get(i).substring(0,1).toUpperCase() + clans.get(i).substring(1);
			clans.set(i, temp);
		}

		Collections.sort(types);
		Collections.sort(clans);
	}

	public static void showGUI(int width, int height)
	{
		frame = new JFrame("DeckBuilder");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));
        //frame.setLayout(new GridLayout(1,3));
        //frame.setLayout(new FlowLayout(FlowLayout.LEFT,5,5));
		frame.setPreferredSize(new Dimension(width,height));
		//Set up defaults if pref file is unavailable

        width+=12;
        height+=187;

		JPanel panel1 = createSearch(width,height);
		JPanel resarea = createResults(width,height);
		JPanel deckarea = createDeckArea(width,height);

		frame.add(panel1);
		frame.add(resarea);
		frame.add(deckarea);
        //Set the menu bar and add the label to the content pane
        frame.setJMenuBar(createMenuBar(width));
        //Display the window

		frame.pack();
        frame.setVisible(true);
	}

	private static JPanel createBar()
	{
		JPanel panel = new JPanel();
		JTextField min = new JTextField(3);
		JTextField max = new JTextField(3);
		JLabel to = new JLabel(" to ");
		to.setMaximumSize(new Dimension(20,20));
		to.setFont(new Font("Serif", Font.PLAIN, 12));
		panel.setMaximumSize(new Dimension(140,20));
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(min);
		panel.add(to);
		panel.add(max);
		return (panel);
	}

	private static JMenuBar createMenuBar(int width)
	{
		//Create the menu bar
		JMenuBar menuBar = new JMenuBar();
		menuBar.setOpaque(true);
		//Size is across the entire application and 25 pixels high
		menuBar.setPreferredSize(new Dimension(width, 25));
		menuBar.setLayout(new BoxLayout(menuBar, BoxLayout.X_AXIS));

		//Lay out the File menu
		JMenu file = new JMenu("File");
		MenuListener menuListener = new MenuListener();
		JMenuItem newDeck = new JMenuItem("New");
		newDeck.addActionListener(menuListener);
		JMenuItem load = new JMenuItem("Load");
		load.addActionListener(menuListener);
		JMenuItem save = new JMenuItem("Save");
		save.addActionListener(menuListener);
		JMenuItem saveas = new JMenuItem("Save As...");
		saveas.addActionListener(menuListener);
		JMenuItem close = new JMenuItem("Close");
		close.addActionListener(menuListener);
		file.add(newDeck);
		file.add(load);
		file.add(save);
		file.add(saveas);
		file.addSeparator();
		file.add(close);

		//Add the File menu to the menu bar
		menuBar.add(file);

		JMenu option = new JMenu("Options");
		JMenuItem diag = new JMenuItem("Diagnostics");
		newDeck.addActionListener(menuListener);
		JMenuItem proxyPrint = new JMenuItem("Proxy Printer");
		load.addActionListener(menuListener);
		option.add(diag);
		option.add(proxyPrint);

		//Add the Options menu to the menu bar
		menuBar.add(option);

		return(menuBar);
	}

	private static JPanel createSearch(int width, int height)
	{
		SearchListener briansanewb = new SearchListener();

		JPanel panel = new JPanel();
		panel.setOpaque(true);

		JPanel searchMenu = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));

		//Create the card info box
		card = new CardInfoBox();
		//Kind of hacked. Using a large vertical value so that the info box will take up the rest of the vertical space.
		card.setPreferredSize(new Dimension((width-12)/4, 2000));
		JScrollPane cardScrollPane = new JScrollPane(card);

		JLabel infolabel=new JLabel("Card Info:");
		infolabel.setFont(new Font("Serif",Font.BOLD,14));

		JLabel searchlabel = new JLabel("Search Criteria:");
		searchlabel.setFont(new Font("Serif",Font.BOLD,14));

		Legal = new JComboBox(legalChoices.toArray());
		Legal.setSelectedIndex(4);
		Legal.addActionListener(briansanewb);
		Legal.setPreferredSize(new Dimension(140, 20));
		Legal.setMaximumSize(Legal.getPreferredSize());
		Legal.setBackground(Color.WHITE);

		CardType = new JComboBox(types.toArray());
		CardType.addActionListener(briansanewb);
		CardType.setPreferredSize(new Dimension(140, 20));
		CardType.setMaximumSize(CardType.getPreferredSize());
		CardType.setBackground(Color.WHITE);

		faction = new JComboBox(clans.toArray());
		faction.addActionListener(briansanewb);
		faction.setPreferredSize(new Dimension(140, 20));
		faction.setMaximumSize(faction.getPreferredSize());
		faction.setBackground(Color.WHITE);

		title = new JTextField(10);
		title.setMaximumSize(new Dimension(140,20));
		title.addActionListener(briansanewb);
		JTextField text = new JTextField(10);
		text.setMaximumSize(new Dimension(140,20));

		JPanel goldcost =  createBar();
		JPanel force = createBar();
		JPanel chi = createBar();
		JPanel honor = createBar();
		JPanel phonor = createBar();
		JPanel focus = createBar();

		GroupLayout layout = new GroupLayout(searchMenu);
		searchMenu.setLayout(layout);
		layout.setAutoCreateGaps(true);
		//layout.setAutoCreateContainerGaps(true);


		layout.setHorizontalGroup(
		   layout.createSequentialGroup()
		   .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		      .addComponent(c[0])
			  .addComponent(c[1])
			  .addComponent(c[2])
			  .addComponent(c[3])
			  .addComponent(c[4])
			  .addComponent(c[5])
			  .addComponent(c[6])
			  .addComponent(c[7])
			  .addComponent(c[8])
			  .addComponent(c[9])
			  .addComponent(c[10]))
		   .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		      .addComponent(Legal)
		      .addComponent(CardType)
		      .addComponent(faction)
		      .addComponent(title)
		      .addComponent(text)
		      .addComponent(goldcost)
		      .addComponent(force)
		      .addComponent(chi)
		      .addComponent(honor)
		      .addComponent(phonor)
		      .addComponent(focus))
		);
		layout.setVerticalGroup(
		   layout.createSequentialGroup()
		      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		           .addComponent(c[0])
		           .addComponent(Legal))
			  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			  	   .addComponent(c[1])
			  	   .addComponent(CardType))
			  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			  	   .addComponent(c[2])
			  	   .addComponent(faction))
			  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			  	   .addComponent(c[3])
			  	   .addComponent(title))
			  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			  	   .addComponent(c[4])
			  	   .addComponent(text))
			  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			  	   .addComponent(c[5])
			  	   .addComponent(goldcost))
			  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			  	   .addComponent(c[6])
			  	   .addComponent(force))
			  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			  	   .addComponent(c[7])
			  	   .addComponent(chi))
			  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			  	   .addComponent(c[8])
			  	   .addComponent(honor))
			  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			  	   .addComponent(c[9])
			  	   .addComponent(phonor))
			  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			  	   .addComponent(c[10])
			  	   .addComponent(focus))
		);
		//panel.add(Box.createVerticalStrut(5));
		searchlabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		searchMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
		infolabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		cardScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(searchlabel);
		panel.add(searchMenu);
		panel.add(Box.createHorizontalStrut(5));
		panel.add(infolabel);
		panel.add(cardScrollPane);

		return(panel);

	}

	public static JPanel createResults(int width, int height)
	{
		JPanel panel = new JPanel();
		//panel.setOpaque(true);
		//panel.setBackground(Color.RED);
		panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));

		apples = new JLabel("Card Results("+vect.size()+"):");
		apples.setFont(new Font("Serif",Font.BOLD,14));
		apples.setMaximumSize(new Dimension(140,20));

		alphasort(vect);

		list = new JList((Vector)vect.clone()); //data has type Object[]
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.addListSelectionListener(new ListListener());
		list.setVisibleRowCount(-1);
		list.setSelectedIndex(0);
		card.setCard(vect.elementAt(list.getSelectedIndex()));

		listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(300, 550));

		apples.setAlignmentX(Component.LEFT_ALIGNMENT);
		listScroller.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(apples);
		panel.add(listScroller);
		return(panel);
	}

	public static JPanel createDeckArea(int width, int height)
	{
		JPanel panel = new JPanel();
		panel.setOpaque(true);
		panel.setBackground(Color.YELLOW);
		//panel.setPreferredSize(new Dimension(width/3, height));
		//Add a pretty border to it
		panel.setLayout(new BorderLayout());

		return(panel);
	}
	public static void alphasort(Vector<StoredCard> vex)
	{
		Vector<StoredCard> temp = new Vector<StoredCard>();
		temp.add(null);

		for (int x = 0; x < vex.size(); x++)
			for (int y = 0; y < vex.size(); y++)
				if(vex.elementAt(x).getName().toString().compareToIgnoreCase(vex.elementAt(y).getName().toString())<0)
				{
					temp.setElementAt(vex.elementAt(y),0);    //temp = y
					vex.setElementAt(vex.elementAt(x),y);     //y = x
					vex.setElementAt(temp.elementAt(0),x);    //x = temp
				}
	}
}