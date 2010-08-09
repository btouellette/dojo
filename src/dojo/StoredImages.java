package dojo;
// StoredImages.java
// Written by Brian Ouellette
// Static container for images that are used throughout the program

import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

class StoredImages
{
	public static BufferedImage dynasty, fate;

	public static void loadImages()
	{
		try
		{
			//TODO: Allow them to use the old card backs as an option
			BufferedImage dynastyImage, fateImage;
			dynastyImage = ImageIO.read(new File("images/backs/dynasty_new.jpg"));
			fateImage = ImageIO.read(new File("images/backs/fate_new.jpg"));

			BufferedImage newImageDynasty = new BufferedImage(PlayArea.cardWidth, PlayArea.cardHeight, dynastyImage.getType());
			BufferedImage newImageFate = new BufferedImage(PlayArea.cardWidth, PlayArea.cardHeight, fateImage.getType());

			Graphics2D gDynasty = newImageDynasty.createGraphics();
			Graphics2D gFate = newImageFate.createGraphics();

			// If downsizing image
			if(dynastyImage.getHeight() >= PlayArea.cardHeight)
			{
				gDynasty.drawImage(dynastyImage.getScaledInstance(PlayArea.cardWidth, PlayArea.cardHeight, Image.SCALE_AREA_AVERAGING), 0, 0, null);
				gDynasty.dispose();
				gFate.drawImage(fateImage.getScaledInstance(PlayArea.cardWidth, PlayArea.cardHeight, Image.SCALE_AREA_AVERAGING), 0, 0, null);
				gFate.dispose();
			}
			// If enlarging image
			else
			{
				gDynasty.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				gDynasty.drawImage(dynastyImage, 0, 0, PlayArea.cardWidth, PlayArea.cardHeight, null);
				gDynasty.dispose();
				gFate.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				gFate.drawImage(fateImage, 0, 0, PlayArea.cardWidth, PlayArea.cardHeight, null);
				gFate.dispose();
			}
			dynasty = dynastyImage;
			fate = fateImage;
		} catch(IOException io) {
			System.err.println(io);
		}
	}
}
