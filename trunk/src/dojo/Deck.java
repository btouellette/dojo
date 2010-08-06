package dojo;
// Deck.java
// Written by Brian Ouellette
// Used for your dynasty and fate decks

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

//TODO: Add peek functionality
class Deck extends CardHolder
{
	private boolean isDynasty;
		
	public Deck(boolean isDynasty)
	{
		super();
		this.isDynasty = isDynasty;
	}
		
	public BufferedImage getImage()
	{
		// If we have't loaded in an image for this yet
		if(image == null && !cards.isEmpty())
		{
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
					g.drawImage(image, 0, 0, PlayArea.cardWidth, PlayArea.cardHeight, null);
					g.dispose();
				}
				image = newImage;
			} catch(IOException io) {
				System.err.println(io);
			}
		}
		else if(cards.isEmpty())
		{
			return null;
		}
		return image;
	}
}