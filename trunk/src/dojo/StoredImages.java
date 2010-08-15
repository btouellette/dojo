package dojo;
// StoredImages.java
// Written by Brian Ouellette
// Static container for images that are used throughout the program

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

class StoredImages
{
	public static BufferedImage originalDynasty, dynasty, dynastyBowed;
    public static BufferedImage originalFate, fate, fateBowed;

	public static void loadImages()
	{
		try
		{
			//TODO: Allow them to use the old card backs as an option
			originalDynasty = ImageIO.read(new File("images/backs/dynasty_new.jpg"));
			originalFate = ImageIO.read(new File("images/backs/fate_new.jpg"));
			rescale();
		} catch(IOException io) {
			System.err.println(io);
		}
	}

	public static void rescale()
	{
		// Recreate appropriately sized cards from original image
		int cardWidth = Main.playArea.getCardWidth();
		int cardHeight = Main.playArea.getCardHeight();

		dynasty = new BufferedImage(cardWidth, cardHeight, originalDynasty.getType());
		fate = new BufferedImage(cardWidth, cardHeight, originalFate.getType());

		Graphics2D gDynasty = dynasty.createGraphics();
		Graphics2D gFate = fate.createGraphics();

		// If downsizing image
		if(originalDynasty.getHeight() >= cardHeight)
		{
			gDynasty.drawImage(originalDynasty.getScaledInstance(cardWidth, cardHeight, Image.SCALE_AREA_AVERAGING), 0, 0, null);
			gDynasty.dispose();
			gFate.drawImage(originalFate.getScaledInstance(cardWidth, cardHeight, Image.SCALE_AREA_AVERAGING), 0, 0, null);
			gFate.dispose();
		}
		// If enlarging image
		else
		{
			gDynasty.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			gDynasty.drawImage(originalDynasty, 0, 0, cardWidth, cardHeight, null);
			gDynasty.dispose();
			gFate.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			gFate.drawImage(originalFate, 0, 0, cardWidth, cardHeight, null);
			gFate.dispose();
		}
		// Rotate it to get a bowed version
		// Translate as well so image is still at origin of BufferedImage
		AffineTransform tx = AffineTransform.getTranslateInstance(0,-dynasty.getHeight());
		tx.quadrantRotate(1, 0, dynasty.getHeight());
		AffineTransformOp rotate = new AffineTransformOp(tx, null);
		dynastyBowed = rotate.filter(dynasty, null);
		fateBowed = rotate.filter(fate, null);
	}
}
