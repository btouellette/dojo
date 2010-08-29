package dojo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Deckbuilder extends JFrame implements Comparable<Object>, ActionListener
{
	
	CardInfoBox card;
	DataList search;
	PanelCreator gold, force, chi, honor, phonor, focus;
	TextDecklist textDeck;
	Decklist dynDeck, fateDeck;
	
	JTextField title, text;
	JComboBox legalBox, typeBox, clanBox;
	JLabel resultLabel, stronghold, dynLabel, fateLabel;
	JList searchList, dynList, fateList;
	
	private static final long serialVersionUID = 1L;
	ArrayList<String> types, legal, clans;
	boolean hasSH, edit;
	String fileName;
	
	public Deckbuilder(int width, int height)
	{
		super();

		fileName = null;
		
		dynDeck = new Decklist();
		fateDeck = new Decklist();
		
		types = new ArrayList<String>();
		legal = new ArrayList<String>();
		clans = new ArrayList<String>();

		types.add("");
		clans.add("");
		
		search = new DataList();
		
		Object[] p = Main.databaseID.keySet().toArray();

		for(int i = 0; i < p.length; i++)
		{
			StoredCard currentCard = Main.databaseID.get(p[i]);

			ArrayList<String> currentCardLegal = (ArrayList<String>) currentCard.getLegal();
			for(int k = 0; k < currentCardLegal.size(); k++)
			{
				if(!legal.contains(currentCardLegal.get(k)))
				{
					legal.add(currentCardLegal.get(k));
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

		for(int i = 0; i < legal.size(); i++)
		{
			String temp = legal.get(i).substring(0,1).toUpperCase() + legal.get(i).substring(1);
			legal.set(i, temp);
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
		Collections.sort(search);
		
		setTitle("DeckBuilder");
		
		//TODO: Add dialog if file is not saved/or save temp file, reload on open
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
        
		setPreferredSize(new Dimension(width,height));

		JPanel seaPanel = searchPanel();
		seaPanel.setMaximumSize(seaPanel.getPreferredSize());

		JPanel resPanel = resultPanel();
		resPanel.setPreferredSize(new Dimension(width-seaPanel.getWidth()/2,height));
		
		JPanel decPanel = deckPanel();
		decPanel.setPreferredSize(new Dimension(width-seaPanel.getWidth()/2,height));


		add(seaPanel);
		add(resPanel);
		add(decPanel);
        
		setJMenuBar(createMenuBar());
		
		//Display the window
		pack();
        setVisible(true);
	}
	
	private JMenuBar createMenuBar()
	{
		//Create the menu bar
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setOpaque(true);
		menuBar.setLayout(new BoxLayout(menuBar, BoxLayout.X_AXIS));
		
		//Size is across the entire application and 25 pixels high
		//menuBar.setPreferredSize(new Dimension(width, 25));

		//Lay out the File menu
		JMenu file = new JMenu("File");
		
		JMenuItem newDeck = new JMenuItem("New");
		newDeck.addActionListener(this);
		
		JMenuItem load = new JMenuItem("Load");
		load.addActionListener(this);
		
		JMenuItem save = new JMenuItem("Save");
		save.addActionListener(this);
		
		JMenuItem saveas = new JMenuItem("Save As...");
		saveas.addActionListener(this);
		
		JMenuItem close = new JMenuItem("Close");
		close.addActionListener(this);
		
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
		//diag.addActionListener(this);
		//TODO Add diagnostics and proxy printer??
		//JMenuItem proxyPrint = new JMenuItem("Proxy Printer");
		//proxyPrint.addActionListener(this);
		option.add(diag);
		//option.add(proxyPrint);

		//Add the Options menu to the menu bar
		menuBar.add(option);

		return menuBar;
	}

	private JPanel searchPanel()
	{
		String[] namesArray = {"Legal: ","Card Type: ","Clan: ","Title: ","Text: ",
							 "Gold Cost: ","Force: ","Chi: ","Honor Req.: ","PH: ",
							 "Focus Value: "};
		
		JLabel[] names = new JLabel[namesArray.length];
		
		//Add an ActionListener for the Drop Menus
		ActionListener searchListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				updateSearch();
			}
		};
		
		//Add a DocumentListener for TextField events
		DocumentListener textListener = new DocumentListener()
		{
			public void insertUpdate(DocumentEvent d) 
			{
				updateSearch();
			}
			public void removeUpdate(DocumentEvent d) 
			{
				updateSearch();	
			}
			public void changedUpdate(DocumentEvent d) {}
		};

		//Creates an array of JLabels based on namesArray values
		for (int i = 0; i < namesArray.length; i++)
		{
			names[i] = new JLabel(namesArray[i]);
			names[i].setFont(new Font("Serif", Font.PLAIN, 13));
		}
		
		JPanel panel = new JPanel();
		panel.setOpaque(true);
		panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));
		
		JLabel label = new JLabel("Search Criteria:");
		label.setFont(new Font("Serif", Font.BOLD, 14));
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(label);
 
		JPanel searchMenu = new JPanel();		

		legalBox = new JComboBox(legal.toArray());
		legalBox.addActionListener(searchListener);
		legalBox.setPreferredSize(new Dimension(140, 20));
		legalBox.setMaximumSize(legalBox.getPreferredSize());
		legalBox.setBackground(Color.WHITE);

		typeBox = new JComboBox(types.toArray());
		typeBox.addActionListener(searchListener);
		typeBox.setPreferredSize(new Dimension(140, 20));
		typeBox.setMaximumSize(typeBox.getPreferredSize());
		typeBox.setBackground(Color.WHITE);

		clanBox = new JComboBox(clans.toArray());
		clanBox.addActionListener(searchListener);
		clanBox.setPreferredSize(new Dimension(140, 20));
		clanBox.setMaximumSize(clanBox.getPreferredSize());
		clanBox.setBackground(Color.WHITE);

		title = new JTextField(10);
		title.setMaximumSize(new Dimension(140,20));
		title.getDocument().addDocumentListener(textListener);

		text = new JTextField(10);
		text.setMaximumSize(new Dimension(140,20));
		text.getDocument().addDocumentListener(textListener);

		
		gold   = new PanelCreator();
		force  = new PanelCreator();
		chi    = new PanelCreator();
		honor  = new PanelCreator();
		phonor = new PanelCreator();
		focus  = new PanelCreator();
		
		gold.min.getDocument().addDocumentListener(textListener);
		gold.max.getDocument().addDocumentListener(textListener);
		
		force.min.getDocument().addDocumentListener(textListener);
		force.max.getDocument().addDocumentListener(textListener);
		
		chi.min.getDocument().addDocumentListener(textListener);
		chi.max.getDocument().addDocumentListener(textListener);
		
		honor.min.getDocument().addDocumentListener(textListener);
		honor.max.getDocument().addDocumentListener(textListener);
		
		phonor.min.getDocument().addDocumentListener(textListener);
		phonor.max.getDocument().addDocumentListener(textListener);
		
		focus.min.getDocument().addDocumentListener(textListener);
		focus.max.getDocument().addDocumentListener(textListener);
		
		//Set up the layout for the search area
		GroupLayout layout = new GroupLayout(searchMenu);
		searchMenu.setLayout(layout);
		layout.setAutoCreateGaps(true);

		layout.setHorizontalGroup(
		   layout.createSequentialGroup()
		   .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		      .addComponent(names[0])
			  .addComponent(names[1])
			  .addComponent(names[2])
			  .addComponent(names[3])
			  .addComponent(names[4])
			  .addComponent(names[5])
			  .addComponent(names[6])
			  .addComponent(names[7])
			  .addComponent(names[8])
			  .addComponent(names[9])
			  .addComponent(names[10]))
		   .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		      .addComponent(legalBox)
		      .addComponent(typeBox)
		      .addComponent(clanBox)
		      .addComponent(title)
		      .addComponent(text)
		      .addComponent(gold)
		      .addComponent(force)
		      .addComponent(chi)
		      .addComponent(honor)
		      .addComponent(phonor)
		      .addComponent(focus))
		);
		layout.setVerticalGroup(
		   layout.createSequentialGroup()
		      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		           .addComponent(names[0])
		           .addComponent(legalBox))
			  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			  	   .addComponent(names[1])
			  	   .addComponent(typeBox))
			  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			  	   .addComponent(names[2])
			  	   .addComponent(clanBox))
			  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			  	   .addComponent(names[3])
			  	   .addComponent(title))
			  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			  	   .addComponent(names[4])
			  	   .addComponent(text))
			  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			  	   .addComponent(names[5])
			  	   .addComponent(gold))
			  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			  	   .addComponent(names[6])
			  	   .addComponent(force))
			  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			  	   .addComponent(names[7])
			  	   .addComponent(chi))
			  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			  	   .addComponent(names[8])
			  	   .addComponent(honor))
			  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			  	   .addComponent(names[9])
			  	   .addComponent(phonor))
			  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			  	   .addComponent(names[10])
			  	   .addComponent(focus))
		);
		
		searchMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(searchMenu);

		panel.add(Box.createHorizontalStrut(5));
		
		label = new JLabel("Card Info:");
		label.setFont(new Font("Serif", Font.BOLD, 14));
		panel.add(label);
		
		card = new CardInfoBox();
		card.setPreferredSize(new Dimension(140, 2000));
		JScrollPane cardScrollPane = new JScrollPane(card);
		cardScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(cardScrollPane);

		return panel;

	}
	
	private JPanel resultPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));

		resultLabel = new JLabel("Card Results (" + search.size() + "):");
		resultLabel.setFont(new Font("Serif",Font.BOLD,14));
		resultLabel.setMaximumSize(new Dimension(140,20));
		resultLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(resultLabel);

		searchList = new JList(search.toArray());
		searchList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		searchList.setLayoutOrientation(JList.VERTICAL);
		searchList.setVisibleRowCount(-1);
		searchList.setSelectedIndex(0);
		
		//Set CardInfoBox to first value
		if (search.get(0)!=null)
			card.setCard(search.get(0));
		
		//Add a Selection Listener to change Card Info Box when selection changes
		searchList.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if(!e.getValueIsAdjusting())
				{
					if(searchList.getSelectedIndex() < 0)
					{
						searchList.setSelectedIndex(0);
					}
					if(search.size()!=0)
						card.setCard(search.get(searchList.getSelectedIndex()));
				}
			}
		});
		
		//Add a Mouse Listener to add cards to deck list if Double Clicked
		searchList.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
		    {
		   		if(e.getClickCount()%2 == 0)
		   		{
		     		int index = searchList.locationToIndex(e.getPoint());

		     		try
		     		{
		     			if(search.get(index).isDynasty())
		     			{
		     				if(!(search.get(index).getType().equals("strongholds")
		     						&& hasSH==true))
		     					dynDeck.deck.add(search.get(index));
		     				updateDyn();
		     			}
		     			else if (!search.get(index).isDynasty())
		     			{
		     				fateDeck.deck.add(search.get(index));
		     				updateFate();
		     			}
		     		} catch(ArrayIndexOutOfBoundsException excptn){}
		     		setFrameTitle(fileName,true);
		        }
		    }
		});

		JScrollPane listScroller = new JScrollPane(searchList);
		listScroller.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(listScroller);
		
		return panel;
	}
	
	private JPanel deckPanel()
	{	
		JTabbedPane tabbedPane = new JTabbedPane();
		JPanel deckPanel = new JPanel();
		deckPanel.setOpaque(true);
		deckPanel.setLayout(new BoxLayout(deckPanel,BoxLayout.PAGE_AXIS));

		JPanel dynSide = new JPanel();
		dynSide.setLayout(new BoxLayout(dynSide,BoxLayout.PAGE_AXIS));

		dynLabel = new JLabel("Dynasty ("+ dynDeck.size() + "):");
		dynLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		dynSide.add(dynLabel);

		dynList = new JList(dynDeck);
		dynList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dynList.setLayoutOrientation(JList.VERTICAL);
		dynList.setVisibleRowCount(-1);
		
		//Add Selection Listener to change CardInfoBox
		dynList.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				JList list = (JList) e.getSource();
				if(e.getValueIsAdjusting() == false)
				{
					if(list.getSelectedValue()==null)
						list.clearSelection();
					else
					{
						for (int i = 0; i < dynDeck.size(); i++)
		     			{
		     				if (dynDeck.deck.get(i).getName().equals(list.getSelectedValue().toString().substring(3)))
		     				{
		     					card.setCard(dynDeck.deck.get(i));
		     					break;
		     				}
		     			}
					}
				}
		    }
		});
		
		//Add mouse listener to remove cards from the list on a Double Click
		dynList.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
    		{
   				if(e.getClickCount()%2 == 0)
   				{

     				if (dynList.getSelectedValue()==null)
     					dynList.clearSelection();
     				else
     				{
     					String val = dynList.getSelectedValue().toString().substring(3);
     					for (int i=0;i<dynDeck.size();i++)
     						if (dynDeck.deck.get(i).getName().equals(val))
     						{
     							dynDeck.deck.remove(i);
     							break;
     						}
     					updateDyn();
     					setFrameTitle(fileName,true);
     				}
       	 		}
    		}
		});
		
		JScrollPane dlistScroller = new JScrollPane(dynList);		
		dlistScroller.setAlignmentX(Component.LEFT_ALIGNMENT);
		dynSide.add(dlistScroller);
		
		JPanel fateSide = new JPanel();
		fateSide.setLayout(new BoxLayout(fateSide,BoxLayout.PAGE_AXIS));

		fateLabel = new JLabel("Fate ("+ fateDeck.size() + "):");
		fateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		fateSide.add(fateLabel);
		
		fateList = new JList(fateDeck);
		fateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fateList.setLayoutOrientation(JList.VERTICAL);
		fateList.setVisibleRowCount(-1);
		
		//Add Selection Listener to change CardInfoBox
		fateList.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				JList list = (JList) e.getSource();
				if(e.getValueIsAdjusting() == false)
				{
					if(list.getSelectedValue()==null)
						list.clearSelection();
					else
					{
						for (int i = 0; i < fateDeck.size(); i++)
		     			{
		     				if (fateDeck.deck.get(i).getName().equals(list.getSelectedValue().toString().substring(3)))
		     				{
		     					card.setCard(fateDeck.deck.get(i));
		     					break;
		     				}
		     			}
					}
				}
		    }
		});
		
		//Add mouse listener to remove cards from the list on a Double Click
		fateList.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
    		{
   				if(e.getClickCount()%2 == 0)
   				{

     				if (fateList.getSelectedValue()==null)
     					fateList.clearSelection();
     				else
     				{
     					String val = fateList.getSelectedValue().toString().substring(3);
     					for (int i=0;i<fateDeck.size();i++)
     						if (fateDeck.deck.get(i).getName().equals(val))
     						{
     							fateDeck.deck.remove(i);
     							break;
     						}
     					updateFate();
     					setFrameTitle(fileName,true);
     				}
       	 		}
    		}
		});
		
		JScrollPane flistScroller = new JScrollPane(fateList);
		flistScroller.setAlignmentX(Component.LEFT_ALIGNMENT);
		fateSide.add(flistScroller);
		
		stronghold = new JLabel("Stronghold: ");
		stronghold.setAlignmentX(Component.LEFT_ALIGNMENT);
		deckPanel.add(stronghold);
		
		JPanel pane = new JPanel();
		pane.setLayout(new BoxLayout(pane,BoxLayout.PAGE_AXIS));
		pane.add(dynSide);
		pane.add(fateSide);
		deckPanel.add(pane);

		tabbedPane.addTab("Editable", deckPanel);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		
		textDeck = new TextDecklist();
		textDeck.setText(dynDeck.deck, fateDeck.deck);
		JScrollPane textScroller = new JScrollPane(textDeck);
		
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new BorderLayout());
		textPanel.add(textScroller, BorderLayout.CENTER);

		tabbedPane.addTab("Text", textPanel);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

		JPanel mainPanel = new JPanel();

		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(tabbedPane, BorderLayout.CENTER);

		return mainPanel;
	}

	private void updateSearch()
	{
		//Shorthand method for the long filterList
		search.filterList(legalBox.getSelectedItem().toString().toLowerCase(),
						  clanBox.getSelectedItem().toString().toLowerCase(),
						  typeBox.getSelectedItem().toString().toLowerCase(),
						  title.getText().toLowerCase(),
						  text.getText().toLowerCase(),
						  gold.min.getText().toLowerCase(),
						  gold.max.getText().toLowerCase(),
						  force.min.getText().toLowerCase(),
						  force.max.getText().toLowerCase(),
						  chi.min.getText().toLowerCase(),
						  chi.max.getText().toLowerCase(),
						  honor.min.getText().toLowerCase(),
						  honor.max.getText().toLowerCase(),
						  phonor.min.getText().toLowerCase(),
						  phonor.max.getText().toLowerCase(),
						  focus.min.getText().toLowerCase(),
						  focus.max.getText().toLowerCase());
		
		Collections.sort(search);
		resultLabel.setText("Card Results (" + search.size() + "):");
		searchList.setListData(search.toArray());
	}

	private void updateDyn()
	{
		dynDeck.setModel();
		dynList.setModel(dynDeck);
	
		//Check for a Stronghold
		int index = -1;
		for (int i = 0; i < dynDeck.size(); i++)
		{
			if (dynDeck.deck.get(i).getType().equalsIgnoreCase("strongholds"))
			{
				hasSH = true;
				index = i;
			}
			if (index == -1) 
				hasSH = false;
		}
		if (dynDeck.size() == 0)
			hasSH = false;
		
		//Set data fields after change
		dynLabel.setText("Dynasty (" + (hasSH?(dynDeck.size() - 1):(dynDeck.size())) + "):");
		stronghold.setText("Stronghold: " + (hasSH?dynDeck.deck.get(index):""));
		textDeck.setText(dynDeck.deck, fateDeck.deck);
	}
	
	private void updateFate()
	{
		//Set data fields after change
		fateDeck.setModel();
		fateList.setModel(fateDeck);
		fateLabel.setText("Fate (" + fateDeck.size() + "):");
		textDeck.setText(dynDeck.deck, fateDeck.deck);
	}

	private void setFrameTitle(String s, boolean ed)
	{
		//Sets frame title and fileName
		boolean name = false;
		fileName = s;
		edit = ed;
		if (s==null)
			name = true;
		setTitle((name?"DeckBuilder":("DeckBuilder - " + s)) + (edit?"*":""));
	}

	public int compareTo(Object arg) 
	{
		return 0;
	}
	
	protected void processWindowEvent(WindowEvent e) 
	{
        if (e.getID() == WindowEvent.WINDOW_CLOSING && edit == true) 
        {
            int exit = JOptionPane.showConfirmDialog(this, "Save Before Closing?");
            if (exit == JOptionPane.NO_OPTION)
            	dispose();
        }
        else
        	super.processWindowEvent(e);
    }

	public void actionPerformed(ActionEvent e)
	{
		String name = ((AbstractButton)e.getSource()).getText();
		
		if(name.equals("New"))
		{
			if(promptSave() != JOptionPane.CANCEL_OPTION)
				newFile();
		}
		else if(name.equals("Load"))
		{
			if(promptSave() != JOptionPane.CANCEL_OPTION)
			{
				File file = chooseFile();
				if (file != null)
				{
					newFile();
					readFile(file);
				}
			}
		}
		else if(name.equals("Save"))
		{
			save();
		}
		else if(name.equals("Save As..."))
		{
			saveAs();
		}
		else if(name.equals("Close"))
		{
			dispose();
		}
		else if(name.equals("Diagnostics"))
		{
		}
		else if(name.equals("Proxy Printer"))
		{
		}

	}
	
	private int promptSave()
	{
		//Ask to save file before changing it
		int option = JOptionPane.NO_OPTION;
		if (edit == true)
			option = JOptionPane.showOptionDialog(null, "Save before closing?", "Save", 
													JOptionPane.YES_NO_CANCEL_OPTION, 
													JOptionPane.QUESTION_MESSAGE,
													null, null, null);
		if (option == JOptionPane.YES_OPTION)
			save();
		return option;
	}
	
	private void newFile()
	{
		//Reset the decklists
		dynDeck = new Decklist();
		fateDeck = new Decklist();
		
		updateDyn();
		updateFate();
		
		setFrameTitle(null, false);
	}
	
	private File chooseFile()
	{
		File file = null;
		JFileChooser open = new JFileChooser();

		try {
			open.setCurrentDirectory(new File(new File(".\\decks").getCanonicalPath()));
		} 
		catch(IOException e)
		{
			System.err.println("Decks file is missing. Please reinstall.");
		}
		
		//Add new Filter for the File Chooser
    	open.addChoosableFileFilter(new javax.swing.filechooser.FileFilter()
    	{
    		public boolean accept(File file)
    		{
        		String filename = file.getName();
        		if(file.isDirectory())
        			return true;
        		if (filename.endsWith(".dck") || filename.endsWith(".l5d"))
        			return true;
        		return false;
    		}
    		public String getDescription()
    		{
    			return "*.dck, *.l5d";
    		}
    	});
    	open.setAcceptAllFileFilterUsed(false);

    	//Prompt user to choose file
    	int choice = open.showDialog(null, "Open");

	    if (choice == JFileChooser.APPROVE_OPTION)
	    	file = open.getSelectedFile();

		return file;

	}

	private void save()
	{
		//Save as current file, or ask for a new one
		if(fileName == null)
			saveAs();
		else
		{
			writeFile(new File("decks\\" + fileName));
		}
	}
	
	private void saveAs()
	{
		//Prompt for file name
		String name = (String)JOptionPane.showInputDialog(this, "Enter a deck name:", 
										"Save As...", JOptionPane.QUESTION_MESSAGE, 
										null, null, null);
		Object[] options = {"Yes", "No", "Cancel"};
		int option = JOptionPane.YES_OPTION;
		if (name != null)
		{
			File file = new File("decks\\" + name + ".l5d");
			try
			{
				//If file exists, ask to overwrite
				if(!file.createNewFile())
					option = JOptionPane.showOptionDialog(null, ("File " + file.getName()+ " exists. Overwrite?"), 
														"Save As...",JOptionPane.YES_NO_CANCEL_OPTION, 
														JOptionPane.QUESTION_MESSAGE, null, options, 
														options[0]);
				if (option == JOptionPane.YES_OPTION)
					writeFile(file);
			} catch(IOException err){}
		}
	}
	
	private void writeFile(File file)
	{
		try
		{
    		BufferedWriter out = new BufferedWriter(new FileWriter(file));
    		for (int i = 0; i < dynDeck.size(); i++)
    			out.write(dynDeck.deck.get(i).getName() + "|");
    		for (int i = 0; i < fateDeck.size(); i++)
    			out.write(fateDeck.deck.get(i).getName() + "|");
    		//TODO make deck exportable
    		for (int i = 0; i < (dynDeck.size() + fateDeck.size()); i++)
    		{
    			
    		}/*
    		while((line = br.readLine()) != null)
    		{
    			// As long as the line isn't blank or commented out
    			if(!line.isEmpty() && line.charAt(0) != '#')
    			{
    				int count = 0, num = 0;
    				// First grab the number of cards
    				while(Character.isDigit(line.charAt(count)))
    				{
    					num *= 10;
    					num += Character.getNumericValue(line.charAt(count));
    					count++;
    				}
    				// Then the card itself
    				String cardName = line.substring(count+1);
    				// And add the correct number of copies
    				StoredCard currentCard = Main.databaseName.get(cardName);
    				for(int i = 0; i < num; i++)
    				{
    					cards.add(currentCard);
    				}
    			}
    		}*/
    		out.close();
    		setFrameTitle(file.getName(), false);
		} catch (IOException e) {}
	}
	
	private void readFile(File file)
	{
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(file));

			ArrayList<StoredCard> cards = new ArrayList<StoredCard>();
			// Grab the file path
			String path = file.getAbsolutePath();
			// So we can grab the file extension
			String fileType = path.substring(path.length()-4);
			
			if(fileType.equals(".l5d"))
			{
				String line;
				// Iterate over the entire file
				while((line = in.readLine()) != null)
				{
					// As long as the line isn't blank or commented out
					if(!line.isEmpty() && line.charAt(0) != '#')
					{
						int count = 0, num = 0;
						// First grab the number of cards
						while(Character.isDigit(line.charAt(count)))
						{
							num *= 10;
							num += Character.getNumericValue(line.charAt(count));
							count++;
						}
						// Then the card itself
						String cardName = line.substring(count+1);
						// And add the correct number of copies
						StoredCard currentCard = Main.databaseName.get(cardName);
						for(int i = 0; i < num; i++)
						{
							cards.add(currentCard);
						}
					}
				}
			}
			else if(fileType.equals(".dck"))
			{
				cards = new ArrayList<StoredCard>(100);
				String deck = in.readLine();
				if(deck != null)
				{
					StringTokenizer st = new StringTokenizer(deck, "|");
					while(st.hasMoreTokens())
					{
						cards.add(Main.databaseID.get(st.nextToken()));
					}
				}
			}
			in.close();
			
			//Add the cards to the Deck lists
			for(int x = 0; x < cards.size(); x++)
			{
				System.out.println(x);
				if (cards.get(x).getType().equals("strongholds"))
						System.out.println("say so");
				if (cards.get(x).isDynasty())
				{
					dynDeck.deck.add(cards.get(x));
					//TODO System.out.println(x + " " + dynDeck.deck.get(x).getName());
				}
				else if (!cards.get(x).isDynasty())
					fateDeck.deck.add(cards.get(x));
			}
			
			updateDyn();
			updateFate();
			setFrameTitle(file.getName(), false);


		} catch (IOException err) {
			TextActionListener.send("Failed to read in deck.\n", "Error");
			err.printStackTrace();
		}

	}

}