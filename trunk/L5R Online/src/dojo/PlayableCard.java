package dojo;
// PlayableCard.java
// Written by Brian Ouellette
// Part of Dojo

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.imageio.ImageIO;

class PlayableCard extends Card
{
	private String type;
	private List<PlayableCard> attachments;
	private Set<String> downloadedEditions;
	private int[] location;
	private BufferedImage cardImage;

	public PlayableCard(String id)
	{
		super(id);
		
		location = new int[2];
		location[0] = 0;
		location[1] = 0;
		attachments = new ArrayList<PlayableCard>();
		downloadedEditions = new HashSet<String>();

		type = Main.databaseID.get(id).getType();
		
		if(type.equals("actions")   || type.equals("kihos")     || type.equals("spells") ||
		   type.equals("ancestors") || type.equals("followers") || type.equals("items")  ||
		   type.equals("rings")     || type.equals("senseis")   || type.equals("winds"))
		{
			isDynasty = false;
		}
		else
		{
			// True for: events, regions, holdings, personalities, strongholds
			isDynasty = true;
		}
	}
	
	public PlayableCard(StoredCard card)
	{
		this(card.getID());
	}

	public void setLocation(int x, int y)
	{
		location[0] = x;
		location[1] = y;
		updateAttachmentLocations();
	}

	public void setLocationSimple(int x, int y)
	{
		location[0] = x;
		location[1] = y;
	}

	public int[] getLocation()
	{
		// Return copy so as not to allow inadvertent changes
		return location.clone();
	}

	public void attach(PlayableCard attachingCard)
	{
		attachments.add(attachingCard);
	}

	public void unattach(PlayableCard unattachingCard)
	{
		//TODO: Make sure this works properly with multiple of the same card attached
		attachments.remove(unattachingCard);
		PlayArea.displayedCards.add(unattachingCard);
	}
	
	public void destroy()
	{
		while(!attachments.isEmpty())
		{
			attachments.remove(0).destroy();
		}
		PlayArea.displayedCards.remove(this);
		if(isDynasty())
		{
			PlayArea.dynastyDiscard.add(this);
		}
		else
		{
			PlayArea.fateDiscard.add(this);
		}
	}

	public void updateAttachmentLocations()
	{
		List<PlayableCard> allAttachments = getAllAttachments();
		for(int i = 0; i < allAttachments.size(); i++)
		{
			PlayableCard currentCard = allAttachments.get(i);
			currentCard.setLocationSimple(location[0], location[1] - (int)(PlayArea.cardHeight*PlayArea.attachmentHeight*(i+1)));
		}
	}

	public List<PlayableCard> getAttachments()
	{
		return attachments;
	}

	//TODO: Remove recursive attachments. Only allow attaching to a single base card
	public List<PlayableCard> getAllAttachments()
	{
		List<PlayableCard> recursedAttachments = new ArrayList<PlayableCard>();
		for(int i = 0; i < attachments.size(); i++)
		{
			recursedAttachments.add(attachments.get(i));
			recursedAttachments.addAll(attachments.get(i).getAllAttachments());
		}
		return recursedAttachments;
	}

	public BufferedImage getImage()
	{
		// Load in the image from the class, the file, or kamisasori.net
		// If we have't loaded in an image for this yet
		if(cardImage == null)
		{
			// Go to the database to find out where the image should be located
			StoredCard databaseCard = Main.databaseID.get(id);
			String imageLocation = databaseCard.getImageLocation();
			String imageEdition = databaseCard.getImageEdition();
			// If there wasn't a valid file in the file system
			if(imageLocation == null && !downloadedEditions.contains(imageEdition))
			{
				downloadedEditions.add(imageEdition);
				System.err.print("** Card image missing. Attempting to get image pack for " + databaseCard.getImageEdition() + " from kamisasori.net: ");
				// Get image pack off kamisasori.net
				//TODO: Allow preference option to disable automatic download
				//TODO: Fail once don't try again
				try {
					// Download image pack as zip via http
					URL url = new URL("http://www.kamisasori.net/files/imagepacks/" + databaseCard.getImageEdition() + ".zip");
					url.openConnection();
					InputStream is = url.openStream();
					FileOutputStream fos = new FileOutputStream("tmp-imagepack.zip");
					for (int c = is.read(); c != -1; c = is.read())
					{
						fos.write(c);
					}
					is.close();
					fos.close();
					System.err.println("success!");

					// Unzip image pack
					FileInputStream fis = new FileInputStream("tmp-imagepack.zip");
					ZipInputStream zis = new ZipInputStream(fis);
					ZipEntry ze;
					while((ze = zis.getNextEntry()) != null)
					{
						System.out.print("** Unzipping " + ze.getName() + ": ");
						// Make any directories as needed before unzipping
						File f = new File("images/cards/" + databaseCard.getImageEdition());
						f.mkdirs();
						fos = new FileOutputStream("images/cards/" + ze.getName());
						for (int c = zis.read(); c != -1; c = zis.read())
						{
							fos.write(c);
						}
						zis.closeEntry();
						fos.close();
						System.out.println("success!");
					}
					zis.close();

					// Delete leftover zip file
					System.out.print("** Deleting zip file after extraction: ");
					File f = new File("tmp-imagepack.zip");
					f.delete();
					System.out.println("success!");
					// Successfully got the files so we should have a valid imageLocation now
					imageLocation = databaseCard.getImageLocation();
				} catch (Throwable t) {
					System.err.println("failed. Kamisasori doesn't have pack or no internet connection.");
					File f = new File("tmp-imagepack.zip");
					if(f.exists())
					{
						f.delete();
					}
					createImage();
				}
			}
			// We should either have loaded in a valid image or have generated a placeholder one
			//TODO: Check to see if there are performance increases that can be done here
			try
			{
				// We read in and resize the image once, after that it is stored in PlayableCard
				// Use GraphicsConfiguration.createCompatibleImage(w, h) if image isn't compatible
				BufferedImage tempImage;
				if(imageLocation != null)
				{
					tempImage = ImageIO.read(new File(imageLocation));
				}
				else
				{
					tempImage = cardImage;
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
					g.drawImage(cardImage, 0, 0, PlayArea.cardWidth, PlayArea.cardHeight, null);
					g.dispose();
				}
				cardImage = newImage;
			} catch(IOException io) {
				System.err.println(io);
			}
		}
		return cardImage;

		// A good fast high-quality image downscaling algorithm hasn't been implemented yet
		// in Graphics2D. This is a helper method to avoid using the old .getScaledInstance
		// which rescales the image multiple times using the standard Bilinear interpolation.
		// It was written by Chris Campbell and found here:
		// http://today.java.net/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html
	}
	
	public BufferedImage createImage()
	{
		// 306x428 is size of high res images provided by Alderac
		BufferedImage image = new BufferedImage(306, 428, BufferedImage.TYPE_BYTE_GRAY);
		image.createGraphics();
		Graphics2D g = (Graphics2D)image.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 306, 428);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(10, 10, 286, 408);
		//TODO: Handle long names well
		//TODO: Consider using templates that are type appropriate
		String name = Main.databaseID.get(id).getName();
		Font font = new Font(g.getFont().getFontName(), Font.ITALIC | Font.BOLD, 25);
		g.setFont(font);
	    int x = (306 - g.getFontMetrics().stringWidth(name)) / 2;  
		g.setColor(Color.BLACK);  
		g.drawString(name, x, 50);
		cardImage = image;
		return image;
	}

	public void setImage(BufferedImage cardImage)
	{
		this.cardImage = cardImage;
	}
}
