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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.imageio.ImageIO;
import javax.swing.ProgressMonitorInputStream;

class PlayableCard
{
	// The ID of the card in the XML database
	private final String id;
	// Whether the card is dynasty or fate
	private boolean isDynasty;
	// Card type
	private String type;
	// Any cards directly attached
	private List<PlayableCard> attachments;
	// Location on the play area
	private int[] location;
	// Images to display, original kept for rescaling purposes
	// Keeping these separate increases memory cost but saves CPU
	private BufferedImage originalImage, cardImage, cardImageBowed,
			cardImageDishonored, cardImageBowedDishonored;
	// Whether the card is visible, bowed, or dishonored
	private boolean faceUp, bowed, dishonored;
	// Whether the card is actually a token
	private boolean isToken;
	// Whether we are getting the image pack for this. Prevents fetching multiple times
	private boolean isDownloading;

	public PlayableCard(String id)
	{
		this.id = id;
		location = new int[2];
		location[0] = 0;
		location[1] = 0;
		attachments = new ArrayList<PlayableCard>();
		// Default to face down, unbowed, and not dishonored
		faceUp = false;
		bowed = false;
		dishonored = false;
		isToken = false;
		isDownloading = false;
		// Pull type out of database and use it to determine whether the card is a dynasty or fate card
		type = Main.databaseID.get(id).getType();
		if (type.equals("strategy") || type.equals("kiho") || type.equals("spell") || type.equals("ancestor") || type.equals("follower") || type.equals("item") || type.equals("ring") || type.equals("sensei") || type.equals("wind")) {
			isDynasty = false;
		} else {
			// True for: celestials, events, regions, holdings, personalities, strongholds
			isDynasty = true;
		}
	}

	public PlayableCard(String name, boolean isToken)
	{
		// Store the token name in the id field
		id = name;
		this.isToken = isToken;
		location = new int[2];
		location[0] = 0;
		location[1] = 0;
		attachments = new ArrayList<PlayableCard>();
		// Default to face up, unbowed, and not dishonored
		faceUp = true;
		bowed = false;
		dishonored = false;
		isDownloading = false;
		isDynasty = false;
		type = id;
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

	public void unbow()
	{
		bowed = false;
	}

	public boolean isToken()
	{
		return isToken;
	}

	public boolean isHonorable()
	{
		return !dishonored;
	}

	public void dishonor()
	{
		dishonored = true;
	}

	public void rehonor()
	{
		dishonored = false;
	}

	public String getType()
	{
		return type;
	}

	public String getID()
	{
		return id;
	}

	public boolean isDynasty()
	{
		return isDynasty;
	}

	public boolean isFate()
	{
		return !isDynasty;
	}

	public void setLocation(int x, int y)
	{
		// Sets the location of the card and updates all its attachments locations correspondingly
		location[0] = x;
		location[1] = y;
		updateAttachmentLocations();
	}

	public void setLocationSimple(int x, int y)
	{
		// Sets the location of the card without changing any of its attachments
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
		// Attach the card and then remove it from the list of units
		attachments.add(attachingCard);
		// Remove it from the table or the hand appropriately
		//FIXME: Main.state.removeCard(attachingCard);
		// Attachment locations need to be updated since the cards have changed
		updateAttachmentLocations();
	}

	public boolean unattach(PlayableCard unattachingCard)
	{
		boolean removed = attachments.remove(unattachingCard);
		// Found attached to this card so finish removing it
		if (removed) {
			// Move the card into its own unit and update locations
			int[] location = unattachingCard.getLocation();
			// TODO: Do this movement better (move right unless it would put it in a different hand/table position)
			unattachingCard.setLocation(location[0], location[1] - Main.playArea.getCardHeight());
			//FIXME: if (Main.state.handContains(this)) {
				//FIXME: Main.state.addToHand(unattachingCard);
			//FIXME: } else {
				//FIXME: Main.state.addToTable(unattachingCard);
			//FIXME: }
		}
		// We didn't find it attached to the base card in the unit.
		// Recurse through attachments of attachments
		int index = 0;
		while (!removed && index < attachments.size()) {
			removed = attachments.get(index).unattach(unattachingCard);
			index++;
		}
		// Update attachment locations since attachment list has changed
		updateAttachmentLocations();
		// Return true if successfully removed card from unit
		return removed;
	}

	public void destroy()
	{
		// Destroy all our attachments first
		while (!attachments.isEmpty()) {
			attachments.remove(0).destroy();
		}
		// Going into discard pile where everything is face up and unbowed
		faceUp = true;
		bowed = false;
		// And put in appropriate discard or out of game
		if (isToken) {
			//FIXME: Main.state.removeCard(this);
		} else {
			//FIXME: Main.state.addToDiscard(this);
		}
	}

	public void moveToDeck()
	{
		// Move all our attachments first
		while (!attachments.isEmpty()) {
			attachments.remove(0).moveToDeck();
		}
		// Going into deck where everything is face down, unbowed, and honorable
		faceUp = false;
		bowed = false;
		dishonored = false;
		// And put in appropriate deck
		//FIXME: Main.state.addToDeck(this);
	}

	public void moveToProvince(Province province)
	{
		// Don't go if we have attachments
		if (!attachments.isEmpty()) {
			return;
		}
		// Going into a province where everything is unbowed and honorable
		bowed = false;
		dishonored = false;
		// And put in appropriate province
		//FIXME: Main.state.removeFromTable(this);
		province.add(this);
	}

	public void doubleClicked()
	{
		// Flip over if face down
		if (!faceUp) {
			faceUp = true;
		}
		// And toggle bowed state if not
		else {
			bowed = !bowed;
		}
	}

	public void updateAttachmentLocations()
	{
		// Set all attachment locations to move upwards a fixed percent of cardHeight per attachment
		List<PlayableCard> allAttachments = getAllAttachments();
		int cardHeight = Main.playArea.getCardHeight();
		float attachmentHeight = Main.playArea.getAttachmentHeight();
		for (int i = 0; i < allAttachments.size(); i++) {
			PlayableCard currentCard = allAttachments.get(i);
			currentCard.setLocationSimple(location[0], location[1] - (int) (cardHeight * attachmentHeight * (i + 1)));
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
		// For every direct attachment of the current card
		for (PlayableCard attachment : attachments) {
			// Put all the attachment in the list
			recursedAttachments.add(attachment);
			// And all the attachment's attachments
			recursedAttachments.addAll(attachment.getAllAttachments());
		}
		return recursedAttachments;
	}

	public BufferedImage getImage()
	{
		// If the card isn't faceup return the default back images
		if (!faceUp) {
			if (isDynasty && !bowed) {
				return StoredImages.dynasty;
			} else if (isDynasty && bowed) {
				return StoredImages.dynastyBowed;
			} else if (!bowed) {
				return StoredImages.fate;
			} else {
				return StoredImages.fateBowed;
			}
		}
		// Load in the image from the class, the file, or kamisasori.net
		// If we have't loaded in an image for this yet
		if (originalImage == null) {
			if (isToken) {
				// Token name is stored in the ID field, make a new image for it
				createTokenImage(id);
			} else if (Main.databaseID.get(id).getImageLocation() != null) {
				try {
					originalImage = ImageIO.read(new File(Main.databaseID.get(id).getImageLocation()));
				} catch (IOException io_e) {
					System.err.println("** Failed to read in image from disk");
					io_e.printStackTrace();
				}
				rescale();
			} else if (!isDownloading) {
				isDownloading = true;
				new Thread() {
					public void run()
					{
						// Go to the database to find out where the image should be located
						StoredCard databaseCard = Main.databaseID.get(id);
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
								FileOutputStream fos = new FileOutputStream("tmp-imagepack.zip");
								// Write the entire stream out to a temporary file
								for (int c = is.read(); c != -1; c = is.read()) {
									fos.write(c);
								}
								is.close();
								fos.close();
								System.err.println("success!");

								// Unzip image pack
								ProgressMonitorInputStream fis = new ProgressMonitorInputStream(Main.frame, "Unzipping " + imageEdition + "...", new FileInputStream("tmp-imagepack.zip"));
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

								// Delete leftover zip file
								System.out.print("** Deleting zip file after extraction: ");
								File f = new File("tmp-imagepack.zip");
								f.delete();
								System.out.println("success!");
								imageLocation = databaseCard.getImageLocation();
							} catch (InterruptedIOException io_e) {
								// If we failed clean up leftover temporary files
								File f = new File("tmp-imagepack.zip");
								if (f.exists()) {
									f.delete();
								}
							} catch (FileNotFoundException t) {
								System.err.println("failed. Error unzipping downloaded file.");
								// If we failed clean up leftover temporary files
								File f = new File("tmp-imagepack.zip");
								if (f.exists()) {
									f.delete();
								}
							} catch (IOException t) {
								System.err.println("failed. Kamisasori doesn't have pack or no internet connection.");
								// If we failed clean up leftover temporary files
								File f = new File("tmp-imagepack.zip");
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
				}.start();
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
			if (type.equals("personality")) {
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
		// 306x428 is size of high res images provided by Alderac
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
		// 306x428 is size of high res images provided by Alderac
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
}
