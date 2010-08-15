package dojo;
// PlayableCard.java
// Written by Brian Ouellette
// Represents any card which might be on the field at some point

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.imageio.ImageIO;

class PlayableCard extends Card
{
	private String type;
	private List<PlayableCard> attachments;
	private int[] location;
	private BufferedImage originalImage, cardImage, cardImageBowed;
	private boolean faceUp, bowed;

	public PlayableCard(String id)
	{
		super(id);
		
		location = new int[2];
		location[0] = 0;
		location[1] = 0;	
		attachments = new ArrayList<PlayableCard>();
		faceUp = false;
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

	public boolean isFaceUp()
	{
		return faceUp;
	}

	public void setFaceUp()
	{
		faceUp = true;
	}

	public boolean isBowed()
	{
		return bowed;
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
		PlayArea.displayedCards.remove(attachingCard);
		updateAttachmentLocations();
	}

	public boolean unattach(PlayableCard unattachingCard)
	{
		boolean removed = attachments.remove(unattachingCard);
		int index = 0;
		// Found attached to this card
		if(removed)
		{
			// Move the card into its own unit and update locations
			int[] location = unattachingCard.getLocation();
			unattachingCard.setLocation(location[0]+PlayArea.cardWidth, location[1]);
			PlayArea.displayedCards.add(unattachingCard);
		}
		// We didn't find it attached to the base card in the unit.
		// Recurse through attachments of attachments
		while(!removed && index < attachments.size())
		{
			removed = attachments.get(index).unattach(unattachingCard);
			index++;
		}
		updateAttachmentLocations();
		return removed;
	}
	
	public void destroy()
	{
		// Destroy all our attachments firsts
		while(!attachments.isEmpty())
		{
			attachments.remove(0).destroy();
		}
		// Make sure we aren't being displayed on the field anymore
		PlayArea.displayedCards.remove(this);
		// Going into discard pile where everything is face up
		faceUp = true;
		// And put in appropriate discard
		if(isDynasty())
		{
			PlayArea.dynastyDiscard.add(this);
		}
		else
		{
			PlayArea.fateDiscard.add(this);
		}
	}

	public void doubleClicked()
	{
		if(!faceUp)
		{
			faceUp = true;
		}
		else
		{
			bowed = !bowed;
		}
	}

	public void updateAttachmentLocations()
	{
		// Set all attachment locations to move upwards a fixed percent of cardHeight per attachment
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

	// Return a complete list of attachments, including attachments of attachments and so on
	public List<PlayableCard> getAllAttachments()
	{
		List<PlayableCard> recursedAttachments = new ArrayList<PlayableCard>();
		for(PlayableCard attachment : attachments)
		{
			// Put all the current attachments in the list
			recursedAttachments.add(attachment);
			// And all the attachments attachments
			recursedAttachments.addAll(attachment.getAllAttachments());
		}
		return recursedAttachments;
	}

	public BufferedImage getImage()
	{
		// If the card isn't faceup return the default back images
		if(!faceUp)
		{
			if(isDynasty && !bowed)
			{
				return StoredImages.dynasty;
			}
			else if(isDynasty && bowed)
			{
				return StoredImages.dynastyBowed;
			}
			else if(!bowed)
			{
				return StoredImages.fate;
			}
			else
			{
				return StoredImages.fateBowed;
			}
		}
		// Load in the image from the class, the file, or kamisasori.net
		// If we have't loaded in an image for this yet
		if(originalImage == null)
		{
			// Go to the database to find out where the image should be located
			StoredCard databaseCard = Main.databaseID.get(id);
			String imageLocation = databaseCard.getImageLocation();
			String imageEdition = databaseCard.getImageEdition();
			// If there wasn't a valid file in the file system
			if(imageLocation == null && !Preferences.downloadedEditions.contains(imageEdition))
			{
				Preferences.downloadedEditions.add(imageEdition);
				System.err.print("** Card image missing. Attempting to get image pack for " + databaseCard.getImageEdition() + " from kamisasori.net: ");
				// Get image pack off kamisasori.net
				//TODO: Allow preference option to disable automatic download
				//TODO: Fail once don't try again (save in config)
				try {
					// Download image pack as zip via http
					URL url = new URL("http://www.kamisasori.net/files/imagepacks/" + databaseCard.getImageEdition() + ".zip");
					url.openConnection().setConnectTimeout(500);
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
				}
			}
			// We should either have loaded in a valid image or have generated a placeholder one
			try
			{
				// We read in and resize the image once, after that it is stored in PlayableCard
				if(imageLocation != null)
				{
					originalImage = ImageIO.read(new File(imageLocation));
				}
				else
				{
					originalImage = createImage();
				}
				rescale();
			} catch(IOException io) {
				System.err.println("** Failed to read in image from disk");
				io.printStackTrace();
			}
		}
		if(bowed)
		{
			return cardImageBowed;
		}
		return cardImage;
	}

	public void rescale()
	{
		if(originalImage != null)
		{
			// Scale image
			// AffineTransform looks horrible when downscaling high res images. Using getScaledInstance instead
			cardImage = new BufferedImage(PlayArea.cardWidth, PlayArea.cardHeight, originalImage.getType());
			Graphics2D g = cardImage.createGraphics();
			// If downsizing image
			if(originalImage.getHeight() >= PlayArea.cardHeight)
			{
				
				g.drawImage(originalImage.getScaledInstance(PlayArea.cardWidth, PlayArea.cardHeight, Image.SCALE_AREA_AVERAGING), 0, 0, null);
				g.dispose();
			}
			// If enlarging image
			else
			{
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g.drawImage(originalImage, 0, 0, PlayArea.cardWidth, PlayArea.cardHeight, null);
				g.dispose();
			}

			// Rotate it to get a bowed version
			AffineTransform tx = AffineTransform.getTranslateInstance(0,-cardImage.getHeight());
			tx.quadrantRotate(1, 0, cardImage.getHeight());
			AffineTransformOp rotate = new AffineTransformOp(tx, null);
			cardImageBowed = rotate.filter(cardImage, null);
		}
	}
		
	public BufferedImage createImage()
	{
		// 306x428 is size of high res images provided by Alderac
		BufferedImage image = new BufferedImage(306, 428, BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D g = image.createGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 306, 428);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(10, 10, 286, 408);
		//TODO: Handle long names well
		//TODO: Use templates that are type appropriate (get F/C and display)
		String name = Main.databaseID.get(id).getName();
		Font font = new Font(g.getFont().getFontName(), Font.ITALIC | Font.BOLD, 25);
		g.setFont(font);
	    int x = (306 - g.getFontMetrics().stringWidth(name)) / 2;  
		g.setColor(Color.BLACK);  
		g.drawString(name, x, 50);
		return image;
	}

	public void setImage(BufferedImage cardImage)
	{
		this.cardImage = cardImage;
	}
}
