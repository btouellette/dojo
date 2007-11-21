// SliderListener.java
// Written by Brian Ouellette
// Part of Dojo
// This is the listener for the JSlider that controls the card size. It makes the cards bigger or smaller.

package l5r;

import javax.swing.JSlider;
import javax.swing.event.*;
import java.util.*;

class SliderListener implements ChangeListener
{
	public SliderListener()
	{
	}

	public void stateChanged(ChangeEvent e)
	{
		int sliderValue = ((JSlider)e.getSource()).getValue();
		Main.playArea.setCardSize(sliderValue*Main.playArea.baseCardHeight/50);

		//TODO: This can be optimized much better probably. It does file i/o on every tick at the moment.
		//TODO: This doesn't work properly with attachments yet
		ListIterator<PlayableCard> iterator = PlayArea.displayedCards.listIterator();
		while (iterator.hasNext())
		{
			PlayableCard element = iterator.next();
			element.setImage(null);
			element.updateAttachmentLocations();
			ArrayList<PlayableCard> attachments = element.getAllAttachments();
			for(int i = 0; i < attachments.size(); i++)
			{
				attachments.get(i).setImage(null);
			}
    	}

		Main.playArea.repaint();
	}
}