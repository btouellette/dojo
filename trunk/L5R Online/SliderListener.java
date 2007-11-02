// SliderListener.java
// Written by Brian Ouellette
// Part of Dojo
// This is the listener for the JSlider that controls the card size. It makes the cards bigger or smaller.

package l5r;

import javax.swing.JSlider;
import javax.swing.event.*;

class SliderListener implements ChangeListener
{
	public SliderListener()
	{
	}

	public void stateChanged(ChangeEvent e)
	{
		int sliderValue = ((JSlider)e.getSource()).getValue();
		//Main.playArea.setCardSize(sliderValue*Main.playArea.getBaseCardSize()/50);
		Main.playArea.setCardSize(sliderValue*Main.playArea.baseCardHeight/50);
		Main.playArea.repaint();

		//System.out.println(sliderValue*Main.playArea.baseCardHeight/50);
	}
}