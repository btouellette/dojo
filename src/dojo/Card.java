package dojo;

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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;
import javax.swing.ProgressMonitorInputStream;

// TODO: find and replace all instances of Stored and Playable Cards with this class
public class Card {
	public enum GameArea { Table, DynastyDeck, FateDeck, DynastyDiscard, FateDiscard, Hand, RemovedFromGame, FocusPool }
	public enum CardType { Ancestor, Celestial, Event, Follower, Holding, Item, Other, Personality, Proxy, Region, Ring, Sensei, Spell, Strategy, Stronghold, Wind }
	
	// Unique identifier for this game card (cgid in Egg terminology)
	int id;
	// Card properties
	boolean bowed, faceUp, dishonored;
	// Card location in-game (which game area it currently lives in)
	GameArea gameArea;
	// PlayerID of the owner of this card
	int ownerID;
	// Card ID from XML DB
	String xmlID;
	// Card x and y locations
	double x, y;
	// All cards directly attached to this card
	List<Card> attachments;
	CardType type;
	// Images to display different card states, original kept for rescaling purposes
	// Keeping these separate increases memory cost but saves CPU
	private BufferedImage originalImage, cardImage, cardImageBowed,
			cardImageDishonored, cardImageBowedDishonored;
	private Thread downloadCardImagePack;
	private ReentrantLock isDownloading = new ReentrantLock();
	
	private Card() {
		attachments = new ArrayList<Card>();
		bowed = false;
		faceUp = false;
		dishonored = false;
		downloadCardImagePack = new Thread() {
			public void run()
			{
				// Go to the database to find out where the image should be located
				StoredCard databaseCard = Main.databaseID.get(xmlID);
				String imageLocation = databaseCard.getImageLocation();
				String imageEdition = databaseCard.getImageEdition();
				// If there wasn't a valid file in the file system
				if (imageLocation == null && !Preferences.downloadedEditions.contains(imageEdition)) {
					Preferences.downloadedEditions.add(imageEdition);
					System.err.print("** Card image missing. Attempting to get image pack for " + imageEdition + " from kamisasori.net: ");
					// Get image pack off kamisasori.net
					// TODO: Allow preference option to disable automatic download
					try {
						// Download image pack as zip via http
						URL url = new URL("http://www.kamisasori.net/files/imagepacks/" + imageEdition + ".zip");
						URLConnection urlC = url.openConnection();
						urlC.setConnectTimeout(500);
						int fileSize = urlC.getContentLength();
						ProgressMonitorInputStream is = new ProgressMonitorInputStream(Main.frame, "Downloading image pack for " + imageEdition + "...", url.openStream());
						is.getProgressMonitor().setMaximum(fileSize);
						FileOutputStream fos = new FileOutputStream(id + "-tmp-" + imageEdition + "-imagepack.zip");
						// Write the entire stream out to a temporary file
						for (int c = is.read(); c != -1; c = is.read()) {
							fos.write(c);
						}
						is.close();
						fos.close();
						System.err.println("success!");

						// Unzip image pack
						ProgressMonitorInputStream fis = new ProgressMonitorInputStream(Main.frame, "Unzipping " + imageEdition + "...", new FileInputStream(id + "-tmp-" + imageEdition + "-imagepack.zip"));
						ZipInputStream zis = null;
						try {
							zis = new ZipInputStream(fis);
							ZipEntry ze;
							// Unzip every image in the zip file
							while ((ze = zis.getNextEntry()) != null) {
								System.out.print("** Unzipping " + ze.getName() + ": ");
								fis.getProgressMonitor().setNote("Unzipping " + ze.getName() + "...");
								// Make any directories as needed before unzipping
								File f = new File("images/cards/" + databaseCard.getImageEdition());
								// Check if we made any necessary directories for this file
								if (f.mkdirs()) {
									fos = new FileOutputStream("images/cards/" + ze.getName());
									// Write the entire unzipped image to the output file
									for (int c = zis.read(); c != -1; c = zis.read()) {
										fos.write(c);
									}
								} else {
									throw new IOException();
								}
								zis.closeEntry();
								fos.close();
								System.out.println("success!");
							}
						} finally {
							if (zis != null) {
								zis.close();
							}
						}
						imageLocation = databaseCard.getImageLocation();
					} catch (FileNotFoundException t) {
						System.err.println("failed. Error unzipping downloaded file.");
					} catch (IOException t) {
						System.err.println("failed. Kamisasori doesn't have pack or no internet connection.");
					} finally {
						// Clean up leftover temporary files
						File f = new File(id + "-tmp-" + imageEdition + "-imagepack.zip");
						if (f.exists()) {
							f.delete();
						}								
					}
				}
				// We should either have loaded in a valid image or have generated a placeholder one
				try {
					// If we got the files we should have a valid imageLocation now
					// Read in the image if one is present
					if (imageLocation != null) {
						originalImage = ImageIO.read(new File(imageLocation));
					}
					// If not make a default one
					else {
						originalImage = createImage();
					}
					// Generate appropriately sized images for displaying
					rescale();
					Main.playArea.repaint();
				} catch (IOException io) {
					System.err.println("** Failed to read in image from disk");
					io.printStackTrace();
				}
			}
		};
	}
	
	public Card(int id, int ownerID) {
		this();
		this.id = id;
		this.ownerID = ownerID;
	}
	
	public Card(String xmlID) {
		this();
		this.xmlID = xmlID;
	}

	public List<Card> getAttachments()
	{
		return attachments;
	}

	// Return a complete list of attachments, including attachments of attachments and so on
	public List<Card> getAllAttachments()
	{
		List<Card> recursedAttachments = new ArrayList<Card>();
		// For every direct attachment of the current card
		for (Card attachment : attachments) {
			// Put all the attachment in the list
			recursedAttachments.add(attachment);
			// And all the attachment's attachments
			recursedAttachments.addAll(attachment.getAllAttachments());
		}
		return recursedAttachments;
	}
	
	public boolean isDynastyCard()
	{
		return type == CardType.Celestial || 
			   type == CardType.Holding || 
			   type == CardType.Personality || 
			   type == CardType.Event || 
			   type == CardType.Region;
	}
	
	public boolean isFateCard()
	{
		return type == CardType.Strategy || 
			   type == CardType.Ring || 
			   type == CardType.Follower || 
			   type == CardType.Item || 
			   type == CardType.Ancestor || 
			   type == CardType.Spell;
	}

	public BufferedImage getImage()
	{
		// If the card isn't face-up return the default back images
		if (!faceUp) {
			if (isFateCard() && !bowed) {
				return StoredImages.fate;
			} else if (isFateCard() && bowed) {
				return StoredImages.fateBowed;
			} else if (!bowed) {
				return StoredImages.dynasty;
			} else {
				return StoredImages.dynastyBowed;
			}
		}
		// Load in the image from the class, the file, or kamisasori.net
		// If we have't loaded in an image for this yet
		if (originalImage == null) {
			if (Main.databaseID.get(xmlID) == null) {
				// Token name is stored in the ID field, make a new image for it
				//TODO: Create token image on token creation
				createTokenImage(xmlID);
			} else if (Main.databaseID.get(xmlID).getImageLocation() != null) {
				try {
					originalImage = ImageIO.read(new File(Main.databaseID.get(xmlID).getImageLocation()));
				} catch (IOException io_e) {
					System.err.println("** Failed to read in image from disk");
					io_e.printStackTrace();
				}
				rescale();
			} else if(downloadCardImagePack.getState() == Thread.State.NEW) {
				if(isDownloading.tryLock()) {
					try {
						downloadCardImagePack.start();						
					} finally {
						isDownloading.unlock();
					}
				}
			}
		}
		// Return the bowed image if bowed, otherwise return the default image
		if (bowed && dishonored) {
			return cardImageBowedDishonored;
		} else if (bowed) {
			return cardImageBowed;
		} else if (dishonored) {
			return cardImageDishonored;
		}
		return cardImage;
	}

	public void rescale()
	{
		// Don't do anything if we have nothing to rescale from
		if (originalImage != null) {
			int cardHeight = Main.playArea.getCardHeight();
			int cardWidth = Main.playArea.getCardWidth();
			// Scale image
			// AffineTransform looks horrible when downscaling high res images. Using getScaledInstance instead
			cardImage = new BufferedImage(cardWidth, cardHeight, originalImage.getType());
			Graphics2D g = cardImage.createGraphics();
			// If downsizing image
			if (originalImage.getHeight() >= cardHeight) {
				g.drawImage(originalImage.getScaledInstance(cardWidth, cardHeight, Image.SCALE_AREA_AVERAGING), 0, 0, null);
				g.dispose();
			}
			// If enlarging image
			else {
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g.drawImage(originalImage, 0, 0, cardWidth, cardHeight, null);
				g.dispose();
			}

			// Rotate it to get a bowed version
			// Translate as well so it ends up at the origin of the BufferedImage
			AffineTransform tx = AffineTransform.getTranslateInstance(0, -cardImage.getHeight());
			tx.quadrantRotate(1, 0, cardImage.getHeight());
			AffineTransformOp op = new AffineTransformOp(tx, null);
			cardImageBowed = op.filter(cardImage, null);

			// Only create dishonored images for personalities
			// TODO: Visually inspect these at high and low res to ensure no loss of quality
			if (type == CardType.Personality) {
				// Flip regular image vertically
				tx = AffineTransform.getScaleInstance(1, -1);
				tx.translate(0, -cardImage.getHeight());
				op = new AffineTransformOp(tx, null);
				cardImageDishonored = op.filter(cardImage, null);

				// Flip bowed image horizontally
				tx = AffineTransform.getScaleInstance(-1, 1);
				tx.translate(-cardImageBowed.getWidth(), 0);
				op = new AffineTransformOp(tx, null);
				cardImageBowedDishonored = op.filter(cardImageBowed, null);
			}
		}
	}

	public BufferedImage createImage()
	{
		// 306x428 is size of high res images provided by Alderac so we'll use that as our default size
		// TODO: Research performance implications of different image types
		BufferedImage image = new BufferedImage(306, 428, BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D g = image.createGraphics();
		// Draw an outline
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 306, 428);
		// And the card interior
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(10, 10, 286, 408);
		// TODO: Handle long names well
		// TODO: Use templates that are type appropriate (get F/C and display)
		String name = Main.databaseID.get(id).getName();
		Font font = new Font(g.getFont().getFontName(), Font.ITALIC | Font.BOLD, 25);
		g.setFont(font);
		int x = (306 - g.getFontMetrics().stringWidth(name)) / 2;
		// And the card name
		g.setColor(Color.BLACK);
		g.drawString(name, x, 50);
		return image;
	}

	public void createTokenImage(String name)
	{
		// 306x428 is size of high res images provided by Alderac so we'll use that as our default size
		originalImage = new BufferedImage(306, 428, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = originalImage.createGraphics();
		// Draw an outline
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 306, 428);
		// And the card interior
		g.setColor(Color.CYAN);
		g.fillRect(10, 10, 286, 408);
		// TODO: Handle long names well
		// TODO: Use templates that are type appropriate (get F/C and display)
		Font font = new Font(g.getFont().getFontName(), Font.ITALIC | Font.BOLD, 25);
		g.setFont(font);
		int x = (306 - g.getFontMetrics().stringWidth(name)) / 2;
		// And the card name
		g.setColor(Color.BLACK);
		g.drawString(name, x, 50);
		rescale();
	}
	
	public void updateAttachmentLocations()
	{
		// Set all attachment locations to move upwards a fixed percent of cardHeight per attachment
		List<Card> allAttachments = getAllAttachments();
		int cardHeight = Main.playArea.getCardHeight();
		float attachmentHeight = Main.playArea.getAttachmentHeight();
		for (int i = 0; i < allAttachments.size(); i++) {
			Card currentCard = allAttachments.get(i);
			currentCard.y = y - (int) (cardHeight * attachmentHeight * (i + 1));
		}
	}
	
	public void unbow()
	{
		bowed = false;
	}

	public void setXMLID(String xmlID)
	{
		this.xmlID = xmlID;
		String type = Main.databaseID.get(xmlID).getType();
		if ("ancestor".equals(type)) {
			this.type = CardType.Ancestor;
		} else if ("celestial".equals(type)) {
			this.type = CardType.Celestial;
		} else if ("event".equals(type)) {
			this.type = CardType.Event;
		} else if ("follower".equals(type)) {
			this.type = CardType.Follower;
		} else if ("holding".equals(type)) {
			this.type = CardType.Holding;
		} else if ("item".equals(type)) {
			this.type = CardType.Item;
		} else if ("personality".equals(type)) {
			this.type = CardType.Personality;
		} else if ("region".equals(type)) {
			this.type = CardType.Region;
		} else if ("ring".equals(type)) {
			this.type = CardType.Ring;
		} else if ("sensei".equals(type)) {
			this.type = CardType.Sensei;
		} else if ("spell".equals(type)) {
			this.type = CardType.Spell;
		} else if ("strategy".equals(type)) {
			this.type = CardType.Strategy;
		} else if ("stronghold".equals(type)) {
			this.type = CardType.Stronghold;
		} else if ("wind".equals(type)) {
			this.type = CardType.Wind;
		}
	}
}
