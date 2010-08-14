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
		ListIterator<PlayableCard> iterator = PlayArea.displayedCards.listIterator();
		while(iterator.hasNext())
		{
			PlayableCard element = iterator.next();
			element.rescale();
			element.updateAttachmentLocations();
			List<PlayableCard> attachments = element.getAllAttachments();
			for(int i = 0; i < attachments.size(); i++)
			{
				attachments.get(i).rescale();
			}
		}
		// Rescale images from cards contained in provinces and province attachments
		Province currentProvince;
		for(int i = 0; i < PlayArea.provinces.size(); i++)
		{
			currentProvince = PlayArea.provinces.get(i);
			currentProvince.rescale();
			List<PlayableCard> attachments = currentProvince.getAttachments();
			for(int j = 0; j < attachments.size(); j++)
			{
				attachments.get(j).rescale();
			}
		}
		// Reload images used in multiple places
		StoredImages.rescale();
		// Repaint the whole area
		Main.playArea.repaint();
	}
}
