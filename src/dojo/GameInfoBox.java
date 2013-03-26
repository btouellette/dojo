package dojo;

// GameInfoBox.java
// Written by Brian Ouellette
// Box for displaying information on the game state.

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// TODO: This sucks. Needs to be better.
class GameInfoBox extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	// y = yours
	// o = opponents
	private JLabel yHonor, oHonor;
	private JLabel yHand, oHand;
	private JLabel yFavor, oFavor;

	/*
	JLabel yFateDeck, oFateDeck;
	JLabel yFateDis, oFateDis;
	JLabel yDynDeck, oDynDeck;
	JLabel yDynDis, oDynDis;*/

	public GameInfoBox()
	{
		super();
		// Set up JLabels for the game state
		yHonor = new JLabel("3", JLabel.CENTER);
		oHonor = new JLabel("35", JLabel.CENTER);
		yHand = new JLabel("8", JLabel.CENTER);
		oHand = new JLabel("10", JLabel.CENTER);
		yFavor = new JLabel("", JLabel.CENTER);
		oFavor = new JLabel("", JLabel.CENTER);

		// Too much information, disabled for now
		/*yFateDeck = new JLabel("", JLabel.CENTER);
		oFateDeck = new JLabel("", JLabel.CENTER);
		yFateDis = new JLabel("", JLabel.CENTER);
		oFateDis = new JLabel("", JLabel.CENTER);
		yDynDeck = new JLabel("", JLabel.CENTER);
		oDynDeck = new JLabel("", JLabel.CENTER);
		yDynDis = new JLabel("", JLabel.CENTER);
		oDynDis = new JLabel("", JLabel.CENTER);*/

		setBackground(Color.WHITE);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		// Vertically aligned JPanels which hold information on the game state
		add(createJPanel("name"));
		add(createJPanel("honor"));
		add(createJPanel("hand"));
		add(createJPanel("favor"));
		/*
		add(createJPanel("fate"));
		add(createJPanel("fatedeck"));
		add(createJPanel("fatediscard"));
		add(createJPanel("dynasty"));
		add(createJPanel("dyndeck"));
		add(createJPanel("dyndiscard"));
		*/
	}

	private JComponent createJPanel(String arg)
	{
		JComponent panel = new JPanel();

		// Set up a JPanel for each item to be displayed
		// Markers for which side of the info box is whose
		if (arg.equals("name")) {
			panel.setLayout(new GridLayout(1, 2));
			panel.add(new JLabel("Your", JLabel.CENTER));
			panel.add(new JLabel("Opponent's", JLabel.CENTER));
		}
		// Display honor totals and arrows to change your total
		else if (arg.equals("honor")) {
			panel.setLayout(new GridLayout(1, 3));

			// Panel to hold buttons and honor total
			JPanel panel2 = new JPanel();
			panel2.setLayout(new BorderLayout());
			panel2.setOpaque(false);

			// Panel to hold up and down buttons
			JPanel panel3 = new JPanel();
			panel3.setLayout(new GridLayout(2, 1));
			panel3.setOpaque(false);

			// Unicode characters for up and down arrows
			JButton upHonor = new JButton("\u21E7");
			JButton downHonor = new JButton("\u21E9");

			// ButtonListener for honor changes is here
			upHonor.addActionListener(this);
			downHonor.addActionListener(this);
			upHonor.setActionCommand("up");
			downHonor.setActionCommand("down");

			// Prevent borders or background from being displayed on buttons
			upHonor.setBorderPainted(false);
			upHonor.setFocusable(false);
			upHonor.setBackground(Color.WHITE);
			upHonor.setOpaque(false);
			downHonor.setBorderPainted(false);
			downHonor.setFocusable(false);
			downHonor.setBackground(Color.WHITE);
			downHonor.setOpaque(false);

			// Add a margin to the buttons so they aren't right against the side
			upHonor.setMargin(new Insets(0, 10, 0, 0));
			downHonor.setMargin(new Insets(0, 10, 0, 0));

			// Construct conglomerate panel
			panel3.add(upHonor);
			panel3.add(downHonor);

			panel2.add(panel3, BorderLayout.WEST);
			panel2.add(yHonor, BorderLayout.CENTER);

			panel.add(panel2);
			panel.add(new JLabel(":Honor:", JLabel.CENTER));
			panel.add(oHonor);
		}
		// Display number of cards present in hands
		else if (arg.equals("hand")) {
			panel.setLayout(new GridLayout(1, 3));
			panel.add(yHand);
			panel.add(new JLabel(":Hand:", JLabel.CENTER));
			panel.add(oHand);
		}
		/*
		else if(arg.equals("fate"))
		{
			panel.add(new JLabel("Fate", JLabel.CENTER));
		}
		else if(arg.equals("fatedeck"))
		{
			panel.setLayout(new GridLayout(1, 3));
			panel.add(yFateDeck);
			panel.add(new JLabel(":Deck:", JLabel.CENTER));
			panel.add(oFateDeck);
		}
		else if(arg.equals("fatediscard"))
		{
			panel.setLayout(new GridLayout(1, 3));
			panel.add(yFateDis);
			panel.add(new JLabel(":Discard:", JLabel.CENTER));
			panel.add(oFateDis);
		}
		else if(arg.equals("dynasty"))
		{
			panel.add(new JLabel("Dynasty", JLabel.CENTER));
		}
		else if(arg.equals("dyndeck"))
		{
			panel.setLayout(new GridLayout(1, 3));
			panel.add(yDynDeck);
			panel.add(new JLabel(":Deck:", JLabel.CENTER));
			panel.add(oDynDeck);
		}
		else if(arg.equals("dyndiscard"))
		{
			panel.setLayout(new GridLayout(1, 3));
			panel.add(yDynDis);
			panel.add(new JLabel(":Discard:", JLabel.CENTER));
			panel.add(oDynDis);
		}
		*/
		// Display who has the Imperial Favor
		else if (arg.equals("favor")) {
			panel.setLayout(new GridLayout(1, 3));
			panel.add(yFavor);
			panel.add(new JLabel(":Favor:", JLabel.CENTER));
			panel.add(oFavor);
		}

		panel.setOpaque(false);
		return panel;
	}

	public void actionPerformed(ActionEvent e)
	{
		// Move honor total up or down depending on which button is pressed
		if (e.getActionCommand().equals("up")) {
			// Take the string, convert it to an int to add to it and then back to a string to display
			yHonor.setText(Integer.toString(Integer.parseInt(yHonor.getText()) + 1));
			TextActionListener.send(Main.state.name + " increases honor to " + yHonor.getText() + ".", "Action");
		} else if (e.getActionCommand().equals("down")) {
			// Take the string, convert it to an int to add to it and then back to a string to display
			yHonor.setText(Integer.toString(Integer.parseInt(yHonor.getText()) - 1));
			TextActionListener.send(Main.state.name + " decreases honor to " + yHonor.getText() + ".", "Action");
		}
	}
}