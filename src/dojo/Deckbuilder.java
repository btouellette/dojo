package dojo;
// Deckbuilder.java
// Written by James Spencer
// Deck building interface and logic

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

class Deckbuilder
{
	public static JFrame frame;
	static ArrayList<String> legalChoices,types,clans;
	public static CardInfoBox card;
	public static JList list, dlist, flist;
	public static Vector<StoredCard> vect, dyn, fate;
	public static JLabel results, dyncards, fatecards, stronghold;
	public static JScrollPane listScroller, dlistScroller, flistScroller;
	public static JComboBox Legal,CardType,faction;
	public static JTextField title,text,min,max;
	public static JTextArea deck;
	public static PanelCreator goldcost, force, chi, honor, phonor, focus;
	public static boolean hasSH, edit;
	public static DefaultListModel formDyn,formFate;
	public static String fileName;


	public Deckbuilder()
	{
		Object[] p = Main.databaseID.keySet().toArray();

		vect = new Vector<StoredCard>();
		types = new ArrayList<String>();
		legalChoices = new ArrayList<String>();
		clans = new ArrayList<String>();
		hasSH = false;
		edit = false;
		fileName = null;

		types.add("");
		clans.add("");

		for(int i = 0; i < p.length; i++)
		{
			StoredCard currentCard = Main.databaseID.get(p[i]);
			vect.add(Main.databaseID.get(p[i]));

			ArrayList<String> currentCardLegal = (ArrayList<String>) currentCard.getLegal();
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

			ArrayList<String> currentCardClan = (ArrayList<String>) currentCard.getClan();
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

	public void showGUI(int width, int height)
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

		JPanel panel1 = createSearch(width);
		panel1.setMaximumSize(panel1.getPreferredSize());

		JPanel resPanel = resultPanel();
		resPanel.setPreferredSize(new Dimension(width-panel1.getWidth()/2,height));
		JPanel decPanel = deckPanel();
		decPanel.setPreferredSize(new Dimension(width-panel1.getWidth()/2,height));


		frame.add(panel1);
		frame.add(resPanel);
		frame.add(decPanel);
		//Set the menu bar and add the label to the content pane
		frame.setJMenuBar(createMenuBar(width));
		//Display the window
		
		frame.pack();
		frame.setVisible(true);
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
		DeckMenuListener menuListener = new DeckMenuListener();
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
		diag.addActionListener(menuListener);
		JMenuItem proxyPrint = new JMenuItem("Proxy Printer");
		proxyPrint.addActionListener(menuListener);
		option.add(diag);
		option.add(proxyPrint);

		//Add the Options menu to the menu bar
		menuBar.add(option);

		return(menuBar);
	}

	private static JPanel createSearch(int width)
	{
		ActionListener pewp = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				display();
			}
		};

		String[] array = {"Legal: ","Card Type: ","Clan: ","Title: ","Text: ",
							 "Gold Cost: ","Force: ","Chi: ","Honor Req.: ","PH: ",
							 "Focus Value: "};
		JLabel[] c = new JLabel[array.length];

		for (int i=0;i<array.length;i++)
		{
			c[i] = new JLabel(array[i]);
		//	c[i].setFont(new Font("Serif", Font.PLAIN, 13));
		}

		JPanel panel = new JPanel();
		panel.setOpaque(true);

		JPanel searchMenu = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));

		card = new CardInfoBox();
		card.setPreferredSize(new Dimension((width-12)/4, 2000));

		JScrollPane cardScrollPane = new JScrollPane(card);

		JLabel infolabel=new JLabel("Card Info:");
		//infolabel.setFont(new Font("Serif",Font.BOLD,14));

		JLabel searchlabel = new JLabel("Search Criteria:");
		//searchlabel.setFont(new Font("Serif",Font.BOLD,14));

		Legal = new JComboBox(legalChoices.toArray());
		Legal.setSelectedIndex(0);
		Legal.addActionListener(pewp);
		Legal.setPreferredSize(new Dimension(140, 20));
		Legal.setMaximumSize(Legal.getPreferredSize());
		Legal.setBackground(Color.WHITE);

		CardType = new JComboBox(types.toArray());
		CardType.addActionListener(pewp);
		CardType.setPreferredSize(new Dimension(140, 20));
		CardType.setMaximumSize(CardType.getPreferredSize());
		CardType.setBackground(Color.WHITE);

		faction = new JComboBox(clans.toArray());
		faction.addActionListener(pewp);
		faction.setPreferredSize(new Dimension(140, 20));
		faction.setMaximumSize(faction.getPreferredSize());
		faction.setBackground(Color.WHITE);

		title = new JTextField(10);
		title.setMaximumSize(new Dimension(140,20));
		title.getDocument().addDocumentListener(new MyDocumentListener());

		text = new JTextField(10);
		text.setMaximumSize(new Dimension(140,20));
		text.getDocument().addDocumentListener(new MyDocumentListener()
		/*	{
				Vector<StoredCard> stor = new Vector<StoredCard>(vect.size());
			public void insertUpdate(DocumentEvent e)
			{
				stor.addAll(dothis());
					refresh();
			}
			public void removeUpdate(DocumentEvent e)
			{
				vect.addAll(stor);
				stor.clear();
				for (int x = 0; x<vect.size();x++)
					if (vect.elementAt(x).getText().toLowerCase().indexOf(text.getText().toLowerCase())<0)
					{
						stor.add(vect.elementAt(x));
						vect.remove(x);
						x--;
					}
				refresh();
			}
			public void changedUpdate(DocumentEvent e) {}

			}

*/
			);

		goldcost =  new PanelCreator();
		//goldcost.min.getDocument().addDocumentListener(new MyDocumentListener());

		force = new PanelCreator();
		chi = new PanelCreator();
		honor = new PanelCreator();
		phonor = new PanelCreator();
		focus = new PanelCreator();

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

	public static JPanel resultPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));

		results = new JLabel("Card Results (" + vect.size() + "):");
		//results.setFont(new Font("Serif",Font.BOLD,14));
		results.setMaximumSize(new Dimension(140,20));
		results.setAlignmentX(Component.LEFT_ALIGNMENT);

		alphasort(vect);

		list = new JList(vect); //data has type Object[]
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.addListSelectionListener(new ListListener());
		list.addMouseListener(new ListActionListener());
		list.setVisibleRowCount(-1);
		list.setSelectedIndex(0);
		if (vect.elementAt(0)!=null)
			card.setCard(vect.elementAt(0));

		listScroller = new JScrollPane(list);
		listScroller.setAlignmentX(Component.LEFT_ALIGNMENT);

		panel.add(results);
		panel.add(listScroller);
		return panel;
	}

	public static JPanel deckPanel()
	{
		dyn = new Vector<StoredCard>();
		fate = new Vector<StoredCard>();

		JPanel pane = new JPanel();
		pane.setLayout(new BoxLayout(pane,BoxLayout.PAGE_AXIS));

		JPanel panel = new JPanel();
		panel.setOpaque(true);
		panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));

		JPanel deck1 = new JPanel();
		deck1.setLayout(new BoxLayout(deck1,BoxLayout.PAGE_AXIS));

		JPanel deck2 = new JPanel();
		deck2.setLayout(new BoxLayout(deck2,BoxLayout.PAGE_AXIS));

		stronghold = new JLabel("Stronghold: ");

		dyncards = new JLabel("Dynasty ("+ dyn.size() + "):");
		fatecards = new JLabel("Fate ("+ fate.size() + "):");

		formDyn = formatDB(dyn);
		dlist = new JList(formDyn);
		dlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dlist.setLayoutOrientation(JList.VERTICAL);
		dlist.addListSelectionListener(new DeckActionListener(dyn));
		dlist.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e)
			{
				if(e.getClickCount()%2 == 0)
				{
	 				if (dlist.getSelectedValue()==null)
	 					dlist.clearSelection();
	 				else
	 				{
	 					String val = dlist.getSelectedValue().toString().substring(3);
	 					for (int i=0;i<dyn.size();i++)
	 						if (dyn.elementAt(i).getName().equals(val))
	 						{
	 							if (dyn.elementAt(i).getType().equals("strongholds"))
	 								hasSH=false;
	 							dyn.remove(i);
	 							break;
	 						}
	 					refreshDyn();
	 					setFrameTitle(fileName,true);
	 				}
				}
			}
		});
		dlist.setVisibleRowCount(-1);

		dlistScroller = new JScrollPane(dlist);

		formFate = new DefaultListModel();
		formFate = formatDB(fate);
		flist = new JList(formFate);
		flist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		flist.setLayoutOrientation(JList.VERTICAL);
		flist.addListSelectionListener(new DeckActionListener(fate));
		flist.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e)
			{
				if(e.getClickCount()%2 == 0)
				{
					if (flist.getSelectedValue()==null)
						flist.clearSelection();
					else
					{
						String val = flist.getSelectedValue().toString().substring(3);
						for (int i=0;i<fate.size();i++)
							if (fate.elementAt(i).getName().equals(val))
							{
								fate.remove(i);
								break;
							}
						refreshFate();
						setFrameTitle(fileName,true);
					}
				}
			}
		});
		flist.setVisibleRowCount(-1);

		flistScroller = new JScrollPane(flist);

		stronghold.setAlignmentX(Component.LEFT_ALIGNMENT);
		dyncards.setAlignmentX(Component.LEFT_ALIGNMENT);
		fatecards.setAlignmentX(Component.LEFT_ALIGNMENT);
		dlistScroller.setAlignmentX(Component.LEFT_ALIGNMENT);
		flistScroller.setAlignmentX(Component.LEFT_ALIGNMENT);

		deck1.add(dyncards);
		deck1.add(dlistScroller);

		deck2.add(fatecards);
		deck2.add(flistScroller);

		pane.add(deck1);
		pane.add(deck2);

		panel.add(stronghold);
		panel.add(pane);

		JPanel panel1 = new JPanel();
		panel1.setLayout(new BorderLayout());

		deck = new JTextArea();
		setText();

		JScrollPane textScroller = new JScrollPane(deck);

		panel1.add(textScroller, BorderLayout.CENTER);

		JTabbedPane tabbedPane = new JTabbedPane();

		tabbedPane.addTab("Editable", panel);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

		tabbedPane.addTab("Text", panel1);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

		JPanel mainPanel = new JPanel();

		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(tabbedPane, BorderLayout.CENTER);

		return mainPanel;
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
	public static void display()
	{
		int max,y;
		String type="", clan, legal;
		String titleVal, textVal;

		RESET();

		y=0;
		legal = Legal.getSelectedItem().toString().toLowerCase();
		clan = faction.getSelectedItem().toString().toLowerCase();
		type = CardType.getSelectedItem().toString().toLowerCase();
		titleVal = title.getText().toLowerCase();
		textVal = text.getText().toLowerCase();
		do
		{
			try{

			if(!vect.elementAt(y).getLegal().contains(legal))
				vect.remove(y);
			else if(!type.equals("")&&!vect.elementAt(y).getType().equals(type))
				vect.remove(y);
			else if (!clan.equals("")&&!vect.elementAt(y).getClan().contains(clan))
				vect.remove(y);
			else if (vect.elementAt(y).getName().toLowerCase().indexOf(titleVal)<0)
				vect.remove(y);
			else if (vect.elementAt(y).getText().toLowerCase().indexOf(textVal)<0)
				vect.remove(y);
			else if (checkDigit(goldcost.min.getText()) &&
					(!checkDigit(vect.elementAt(y).getCost())||
						Integer.parseInt(vect.elementAt(y).getCost())<Integer.parseInt(goldcost.min.getText())))
				vect.remove(y);
			else if (checkDigit(goldcost.max.getText()) &&
					(!checkDigit(vect.elementAt(y).getCost())||
						Integer.parseInt(vect.elementAt(y).getCost())>Integer.parseInt(goldcost.max.getText())))
				vect.remove(y);
			else if (checkDigit(force.min.getText()) &&
					(!checkDigit(vect.elementAt(y).getForce())||
						Integer.parseInt(vect.elementAt(y).getForce())<Integer.parseInt(force.min.getText())))
				vect.remove(y);
			else if (checkDigit(force.max.getText()) &&
					(!checkDigit(vect.elementAt(y).getForce())||
						Integer.parseInt(vect.elementAt(y).getForce())>Integer.parseInt(force.max.getText())))
				vect.remove(y);
			else if (checkDigit(chi.min.getText()) &&
					(!checkDigit(vect.elementAt(y).getChi())||
						Integer.parseInt(vect.elementAt(y).getChi())<Integer.parseInt(chi.min.getText())))
				vect.remove(y);
			else if (checkDigit(chi.max.getText()) &&
					(!checkDigit(vect.elementAt(y).getChi())||
						Integer.parseInt(vect.elementAt(y).getChi())>Integer.parseInt(chi.max.getText())))
				vect.remove(y);
			else if (checkDigit(honor.min.getText()) &&
					(!checkDigit(vect.elementAt(y).getHonorReq())||
						Integer.parseInt(vect.elementAt(y).getHonorReq())<Integer.parseInt(honor.min.getText())))
				vect.remove(y);
			else if (checkDigit(honor.max.getText()) &&
					(!checkDigit(vect.elementAt(y).getHonorReq())||
						Integer.parseInt(vect.elementAt(y).getHonorReq())>Integer.parseInt(honor.max.getText())))
				vect.remove(y);
			else if (checkDigit(phonor.min.getText()) &&
					(!checkDigit(vect.elementAt(y).getPersonalHonor())||
						Integer.parseInt(vect.elementAt(y).getPersonalHonor())<Integer.parseInt(phonor.min.getText())))
				vect.remove(y);
			else if (checkDigit(phonor.max.getText()) &&
					(!checkDigit(vect.elementAt(y).getPersonalHonor())||
						Integer.parseInt(vect.elementAt(y).getPersonalHonor())>Integer.parseInt(phonor.max.getText())))
				vect.remove(y);
			else if (checkDigit(focus.min.getText()) &&
					(!checkDigit(vect.elementAt(y).getFocus())||
						Integer.parseInt(vect.elementAt(y).getFocus())<Integer.parseInt(focus.min.getText())))
				vect.remove(y);
			else if (checkDigit(focus.max.getText()) &&
					(!checkDigit(vect.elementAt(y).getFocus())||
						Integer.parseInt(vect.elementAt(y).getFocus())>Integer.parseInt(focus.max.getText())))
				vect.remove(y);
			else y++;

			}catch(NumberFormatException e){}

			max=vect.size();
		}
		while(y<max);

		refresh();
	}

	public static void refresh()
	{
		alphasort(vect);
		results.setText("Card Results ("+vect.size()+"):");
		list.setListData(vect);
	}
	public static void refreshDyn()
	{
		alphasort(dyn);
		int index=-1;
		for (int i=0;i<dyn.size();i++)
		{
			if (dyn.elementAt(i).getType().equals("strongholds"))
			{
				hasSH=true;
				index = i;
			}
			if (index ==-1) hasSH=false;
		}
		if (dyn.size()==0)
			hasSH=false;

		dyncards.setText("Dynasty ("+(hasSH?(dyn.size()-1):(dyn.size()))+"):");
		stronghold.setText("Stronghold: " + (hasSH?dyn.elementAt(index):""));
		formDyn = formatDB(dyn);
		dlist.setModel(formDyn);
		setText();
	}
	public static void refreshFate()
	{
		alphasort(fate);
		fatecards.setText("Fate ("+fate.size()+"):");
		formFate = formatDB(fate);
		flist.setModel(formFate);
		setText();
	}
	public static void RESET()
	{
		vect.clear();
		Object[] p = Main.databaseID.keySet().toArray();
		for(int i = 0; i < p.length; i++)
		{
			vect.add(Main.databaseID.get(p[i]));
		}
	}
	public static boolean checkDigit(String val) //returns true when string is a number
	{
		if (val==null)
			return false;
		else if(val.length()==0)
			return false;
		for (int x=0; x< val.length();x++)
			if(!Character.isDigit(val.charAt(0)))
				return false;
		return true;
	}
	public static Vector<StoredCard> dothis()
	{
		Vector<StoredCard> stor = new Vector<StoredCard>(vect.size());
		for (int x = 0; x<vect.size();x++)
			if (vect.elementAt(x).getText().toLowerCase().indexOf(text.getText().toLowerCase())<0)
			{
				stor.add(vect.elementAt(x));
				vect.remove(x);
				x--;
			}
			refresh();
		return stor;
	}
	public static DefaultListModel formatDB(Vector<StoredCard> v)
	{
		DefaultListModel arr = new DefaultListModel();
		int x=0, i=1;
		alphasort(v);

		do
		{
			if(v.size()!=0)
			{
				while(x+1<v.size())
				{
					if (v.elementAt(x).getName().equals(v.elementAt(x+1).getName()))
					{
						i++;
						x++;
					}
					else
						break;
				}

				arr.addElement(i + "x " + v.elementAt(x).getName());
				i=1;
			}
			x++;
		}while(x<v.size());

		return arr;
	}
	public static boolean hasSH()
	{

		for(int i=0; i<dyn.size();i++)
		{
			if (dyn.elementAt(i).getType().equals("strongholds"))
				return true;
		}
		return false;
	}
	public static void setFrameTitle(String s, boolean ed)
	{
		boolean name = false;
		fileName = s;
		edit = ed;
		if (s==null)
			name = true;
		frame.setTitle((name?"DeckBuilder":("DeckBuilder - " + s)) + (edit?"*":""));
	}

	public static void setText()
	{
		String newline = "\n";
		String tab = "    ";
		String[] dynTypes = {"Celestial","Event","Holding","Personality","Region"};
		String[] fateTypes = {"Action","Ancestor","Follower","Item","Ring","Sensei","Spell","Wind"};
		String shName = "";
		int counter, a = 1;

		deck.setText("");

		for (int x = 0; x < dynTypes.length; x++)
		{
			counter=0;
			for (int i = 0; i < dyn.size();i++)
			{
				if (dyn.elementAt(i).getType().equalsIgnoreCase(dynTypes[x]))
					counter++;
				if (dyn.elementAt(i).getType().equalsIgnoreCase("Strongholds"))
					shName = dyn.elementAt(i).getName();
			}

			if (x==0)
			{
				deck.append("Stronghold: " + (hasSH?shName:"") + newline + newline);
				deck.append("Dynasty (" + dyn.size() + ")" + newline);
			}

			if (counter>0)
				deck.append(tab + dynTypes[x] + " (" + counter + "):" + newline);

			for (int y = 0; y<dyn.size();y++)
			{
				if(dyn.elementAt(y).getType().equalsIgnoreCase(dynTypes[x]))
				{
					while(y+1<dyn.size())
					{
					if (dyn.elementAt(y).getName().equals(dyn.elementAt(y+1).getName()))
						{
							a++;
							y++;
						}
						else
							break;
					}

					deck.append(tab + tab + (a + "x " + dyn.elementAt(y).getName())+ newline);
					a=1;
				}
			}

		}
		deck.append(newline);

		for (int x = 0; x < fateTypes.length; x++)
		{
			counter=0;
			for (int i = 0; i < fate.size();i++)
			{
				if (fate.elementAt(i).getType().equalsIgnoreCase(fateTypes[x]))
					counter++;
			}

			if (x==0)
				deck.append("Fate (" + fate.size() + ")" + newline);

			if (counter>0)
				deck.append(tab + fateTypes[x] + " (" + counter + "):" + newline);

			for (int y = 0; y<fate.size();y++)
			{
				if(fate.elementAt(y).getType().equalsIgnoreCase(fateTypes[x]))
				{
					while(y+1<fate.size())
					{
					if (fate.elementAt(y).getName().equals(fate.elementAt(y+1).getName()))
						{
							a++;
							y++;
						}
						else
							break;
					}

					deck.append(tab + tab + (a + "x " + fate.elementAt(y).getName())+ newline);
					a=1;
				}
			}
		}
	}
}