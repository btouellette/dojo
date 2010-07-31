package dojo;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;

//TODO: Add peek functionality
class Deck
{
	protected BufferedImage deckImage;
	// 0 is top card
	protected ArrayList<StoredCard> cards;
	private boolean isDynasty;
	
	public Deck()
	{
		cards = new ArrayList<StoredCard>(45);
	}
	
	public Deck(boolean isDynasty)
	{
		this();
		this.isDynasty = isDynasty;
	}
	
	public PlayableCard remove()
	{
		return new PlayableCard(cards.remove(0));
	}
	
	public PlayableCard remove(StoredCard card)
	{
		cards.remove(card);
		return new PlayableCard(card);
	}
	
	public void removeAll()
	{
		cards.clear();
	}
	
	public void add(StoredCard card)
	{
		// Put on top
		cards.add(0, card);
	}

	public void add(PlayableCard card)
	{
		// Put on top
		cards.add(0, Main.databaseID.get(card.getID()));
	}
	
	public void shuffle()
	{
		Collections.shuffle(cards);
	}
	
	public int numCards()
	{
		return cards.size();
	}
	
	public BufferedImage getImage()
	{
		// If we have't loaded in an image for this yet
		if(deckImage == null && !cards.isEmpty())
		{			
			//TODO: Check to see if there are performance increases that can be done here
			try
			{
				// We read in and resize the image once, after that it is stored in PlayableCard
				// Use GraphicsConfiguration.createCompatibleImage(w, h) if image isn't compatible
				//TODO: Allow them to use the old card backs as an option
				BufferedImage tempImage;
				if(isDynasty)
				{
					tempImage = ImageIO.read(new File("images/backs/dynasty_new.jpg"));
				}
				else
				{
					tempImage = ImageIO.read(new File("images/backs/fate_new.jpg"));
				}
				BufferedImage newImage = new BufferedImage(PlayArea.cardWidth, PlayArea.cardHeight, tempImage.getType());
				Graphics2D g = newImage.createGraphics();

				// If downsizing image
				if(tempImage.getHeight() >= PlayArea.cardHeight)
				{
					Image cardImage2 = tempImage.getScaledInstance(PlayArea.cardWidth, PlayArea.cardHeight, Image.SCALE_AREA_AVERAGING);
					g.drawImage(cardImage2, 0, 0, null);
					g.dispose();
				}
				// If enlarging image
				else
				{
					g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
					g.drawImage(deckImage, 0, 0, PlayArea.cardWidth, PlayArea.cardHeight, null);
					g.dispose();
				}
				deckImage = newImage;
			} catch(IOException io) {
				System.err.println(io);
			}
		}
		else if(cards.isEmpty())
		{
			return null;
		}
		return deckImage;
	}
	
	public void setImage(BufferedImage deckImage)
	{
		this.deckImage = deckImage;
	}
}