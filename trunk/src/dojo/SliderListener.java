package dojo;

// SliderListener.java
// Written by Brian Ouellette
// This is the listener for the JSlider that controls the card size. It makes the cards bigger or smaller.

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class SliderListener implements ChangeListener
{
	public void stateChanged(ChangeEvent e)
	{
		Preferences.sliderValue = ((JSlider) e.getSource()).getValue();
		// Rescale the PlayArea background
		Main.playArea.rescale();
		// Reload images used in multiple places
		StoredImages.rescale();
		// Rescale all the decks and things stored in the game state
		Main.state.rescale();
		Main.playArea.repaint();
	}
}