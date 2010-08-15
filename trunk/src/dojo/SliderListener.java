package dojo;
// SliderListener.java
// Written by Brian Ouellette
// This is the listener for the JSlider that controls the card size. It makes the cards bigger or smaller.

import java.util.List;
import java.util.ListIterator;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class SliderListener implements ChangeListener
{
	public void stateChanged(ChangeEvent e)
	{
		Preferences.sliderValue = ((JSlider)e.getSource()).getValue();
		PlayArea.cardHeight = Preferences.sliderValue*PlayArea.baseCardHeight/50;
		PlayArea.cardWidth = (int)(PlayArea.cardHeight*(2.5/3.5));

		//TOOD: Update opponents cards too
		// Rescale images for all cards in units in play
		for(PlayableCard card : PlayArea.displayedCards)
		{
			card.rescale();
			card.updateAttachmentLocations();
			for(PlayableCard attachment : card.getAllAttachments())
			{
				attachment.rescale();
			}
		}
		// Rescale images from cards contained in provinces and province attachments
		for(Province province : PlayArea.provinces)
		{
			province.rescale();
			for(PlayableCard attachment : province.getAttachments())
			{
				attachment.rescale();
			}
		}
		// Reload images used in multiple places
		StoredImages.rescale();
		// Rescale the PlayArea appropriately
		Main.playArea.redrawBackground();
		Main.playArea.repaint();
	}
}
