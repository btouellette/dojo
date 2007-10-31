// GameInfoBox.java
// Written by Brian Ouellette
// Part of Dojo

package l5r;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class GameInfoBox extends JPanel implements ActionListener
{
	int yourHonor, oppHonor;
	int yourCardNum, oppCardNum;
	int yourProvNum, oppProvNum;
	//Favor: 1 if you control, 0 if no control, -1 if opponent control
	int favor;

    public GameInfoBox()
    {
		setBackground(Color.WHITE);
		setLayout(new GridLayout(1,2));

		JButton upHonor = new JButton("\u21E7");
		JButton downHonor = new JButton("\u21E9");

		upHonor.setBorderPainted(false);
		upHonor.setFocusable(false);
		upHonor.setBackground(Color.WHITE);

		downHonor.setBorderPainted(false);
		downHonor.setFocusable(false);
		downHonor.setBackground(Color.WHITE);

		upHonor.setActionCommand("up");
		downHonor.setActionCommand("down");

		JPanel yourInfo = new JPanel();
		JPanel oppInfo = new JPanel();

		yourInfo.setOpaque(false);
		oppInfo.setOpaque(false);

		yourInfo.setLayout(new GridLayout(1, 2));
		oppInfo.setLayout(new GridLayout(1, 1));

		yourInfo.add(upHonor);
		yourInfo.add(downHonor);

		add(yourInfo);
		add(oppInfo);
    }

	public void actionPerformed(ActionEvent e)
	{
	}

}