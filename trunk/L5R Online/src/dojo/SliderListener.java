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
		int sliderValue = ((JSlider)e.getSource()).getValue();
		PlayArea.cardHeight = sliderValue*PlayArea.baseCardHeight/50;
		PlayArea.cardWidth = (int)(PlayArea.cardHeight*(2.5/3.5));

		//TODO: This can be optimized much better. It does file i/o on every tick at the moment.
		//      Potentially store unaltered image in PlayableCard when loading in and work from that.
		//      Might need to work around RAM usage if doing so.
		//TOOD: Update opponents cards too
		ListIterator<PlayableCard> iterator = PlayArea.displayedCards.listIterator();
		while (iterator.hasNext())
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
		PlayArea.dynastyDeck.setImage(null);
		PlayArea.fateDeck.setImage(null);
		PlayArea.dynastyDiscard.setImage(null);
		PlayArea.fateDiscard.setImage(null);

		Main.playArea.repaint();
	}
}
