// SliderListener.java
// Written by Brian Ouellette
// Part of Dojo

package l5r;

import javax.swing.JSlider;
import javax.swing.event.*;

class SliderListener implements ChangeListener
{
	PlayArea playArea;

	public SliderListener(PlayArea playArea)
	{
		this.playArea = playArea;
	}

	public void stateChanged(ChangeEvent e)
	{
		int sliderValue = ((JSlider)e.getSource()).getValue();
		playArea.setCardSize(sliderValue*playArea.getBaseCardSize()/50);
		playArea.repaint();
	}
}