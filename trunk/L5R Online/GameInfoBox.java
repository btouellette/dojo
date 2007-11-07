// GameInfoBox.java
// Written by Brian Ouellette
// Part of Dojo
// Box for displaying information on the game state.

package l5r;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class GameInfoBox extends JPanel implements ActionListener
{
	// y = yours
	// o = opponents
	JLabel yHonor, oHonor;
	JLabel yHand, oHand;
	/*
	JLabel yFateDeck, oFateDeck;
	JLabel yFateDis, oFateDis;
	JLabel yDynDeck, oDynDeck;
	JLabel yDynDis, oDynDis;*/

	JLabel yFavor, oFavor;

    public GameInfoBox()
    {
		yHonor = new JLabel("3", JLabel.CENTER);
		oHonor = new JLabel("35", JLabel.CENTER);
		yHand = new JLabel("8", JLabel.CENTER);
		oHand = new JLabel("10", JLabel.CENTER);

		//Too much information, disabled for now
		/*yFateDeck = new JLabel("", JLabel.CENTER);
		oFateDeck = new JLabel("", JLabel.CENTER);
		yFateDis = new JLabel("", JLabel.CENTER);
		oFateDis = new JLabel("", JLabel.CENTER);
		yDynDeck = new JLabel("", JLabel.CENTER);
		oDynDeck = new JLabel("", JLabel.CENTER);
		yDynDis = new JLabel("", JLabel.CENTER);
		oDynDis = new JLabel("", JLabel.CENTER);*/

		yFavor = new JLabel("", JLabel.CENTER);
		oFavor = new JLabel("", JLabel.CENTER);

		setBackground(Color.WHITE);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		add(createJPanel("name"));
		add(createJPanel("honor"));
		add(createJPanel("hand"));
		/*
		add(createJPanel("fate"));
		add(createJPanel("fatedeck"));
		add(createJPanel("fatediscard"));
		add(createJPanel("dynasty"));
		add(createJPanel("dyndeck"));
		add(createJPanel("dyndiscard"));
		*/
		add(createJPanel("favor"));
	}

	private JComponent createJPanel(String arg)
	{
		JComponent panel = null;

		if(arg.equals("name"))
		{
			panel = new JPanel();
			panel.setLayout(new GridLayout(1, 2));
			panel.add(new JLabel("Your", JLabel.CENTER));
			panel.add(new JLabel("Opponent's", JLabel.CENTER));
		}
		else if(arg.equals("honor"))
		{
			panel = new JPanel();
			panel.setLayout(new GridLayout(1, 3));

			JPanel panel2 = new JPanel();
			panel2.setLayout(new BorderLayout());
			panel2.setOpaque(false);

			JPanel panel3 = new JPanel();
			panel3.setLayout(new GridLayout(2, 1));
			panel3.setOpaque(false);

			JButton upHonor = new JButton("\u21E7");
			JButton downHonor = new JButton("\u21E9");

			upHonor.addActionListener(this);
			downHonor.addActionListener(this);

			upHonor.setBorderPainted(false);
			upHonor.setFocusable(false);
			upHonor.setBackground(Color.WHITE);
			upHonor.setOpaque(false);

			downHonor.setBorderPainted(false);
			downHonor.setFocusable(false);
			downHonor.setBackground(Color.WHITE);
			downHonor.setOpaque(false);

			upHonor.setActionCommand("up");
			downHonor.setActionCommand("down");

			upHonor.setMargin(new Insets(0, 10, 0, 0));
			downHonor.setMargin(new Insets(0, 10, 0, 0));

			panel3.add(upHonor);
			panel3.add(downHonor);

			panel2.add(panel3, BorderLayout.WEST);
			panel2.add(yHonor, BorderLayout.CENTER);

			panel.add(panel2);
			panel.add(new JLabel(":Honor:", JLabel.CENTER));
			panel.add(oHonor);
		}
		else if(arg.equals("hand"))
		{
			panel = new JPanel();
			panel.setLayout(new GridLayout(1, 3));
			panel.add(yHand);
			panel.add(new JLabel(":Hand:", JLabel.CENTER));
			panel.add(oHand);
		}
		/*
		else if(arg.equals("fate"))
		{
			panel = new JPanel();
			panel.add(new JLabel("Fate", JLabel.CENTER));
		}
		else if(arg.equals("fatedeck"))
		{
			panel = new JPanel();
			panel.setLayout(new GridLayout(1, 3));
			panel.add(yFateDeck);
			panel.add(new JLabel(":Deck:", JLabel.CENTER));
			panel.add(oFateDeck);
		}
		else if(arg.equals("fatediscard"))
		{
			panel = new JPanel();
			panel.setLayout(new GridLayout(1, 3));
			panel.add(yFateDis);
			panel.add(new JLabel(":Discard:", JLabel.CENTER));
			panel.add(oFateDis);
		}
		else if(arg.equals("dynasty"))
		{
			panel = new JPanel();
			panel.add(new JLabel("Dynasty", JLabel.CENTER));
		}
		else if(arg.equals("dyndeck"))
		{
			panel = new JPanel();
			panel.setLayout(new GridLayout(1, 3));
			panel.add(yDynDeck);
			panel.add(new JLabel(":Deck:", JLabel.CENTER));
			panel.add(oDynDeck);
		}
		else if(arg.equals("dyndiscard"))
		{
			panel = new JPanel();
			panel.setLayout(new GridLayout(1, 3));
			panel.add(yDynDis);
			panel.add(new JLabel(":Discard:", JLabel.CENTER));
			panel.add(oDynDis);
		}
		*/
		else if(arg.equals("favor"))
		{
			panel = new JPanel();
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
		if(e.getActionCommand().equals("up"))
		{
			yHonor.setText("" + (Integer.valueOf(yHonor.getText()).intValue() + 1));
			TextActionListener.send(Main.userName + " increases honor to " + yHonor.getText() + "." , "Action");
		}
		else if(e.getActionCommand().equals("down"))
		{
			yHonor.setText("" + (Integer.valueOf(yHonor.getText()).intValue() - 1));
			TextActionListener.send(Main.userName + " decreases honor to " + yHonor.getText() + "." , "Action");
		}
	}
}