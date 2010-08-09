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

		//TODO: This can be optimized much better. It does file i/o on every tick at the moment.
		//      Potentially store unaltered image in PlayableCard when loading in and work from that.
		//      Might need to work around RAM usage if doing so.
		//TOOD: Update opponents cards too
		// Remove images for every card in play
		ListIterator<PlayableCard> iterator = PlayArea.displayedCards.listIterator();
		while(iterator.hasNext())
		{
			PlayableCard element = iterator.next();
			element.setImage(null);
			element.updateAttachmentLocations();
			List<PlayableCard> attachments = element.getAllAttachments();
			for(int i = 0; i < attachments.size(); i++)
			{
				attachments.get(i).setImage(null);
			}
		}
		// Remove images from cards contained in provinces and province attachments
		Province currentProvince;
		for(int i = 0; i < PlayArea.provinces.size(); i++)
		{
			currentProvince = PlayArea.provinces.get(i);
			currentProvince.setImage(null);
			List<PlayableCard> attachments = currentProvince.getAttachments();
			for(int j = 0; j < attachments.size(); j++)
			{
				attachments.get(j).setImage(null);
			}
		}
		// Remove images for all the decks/discards
		PlayArea.dynastyDeck.setImage(null);
		PlayArea.fateDeck.setImage(null);
		PlayArea.dynastyDiscard.setImage(null);
		PlayArea.fateDiscard.setImage(null);
		// Reload images used in multiple places
		StoredImages.loadImages();
		// Repaint the whole area
		// This will force the reloading and resizing of all images
		Main.playArea.repaint();
	}
}
