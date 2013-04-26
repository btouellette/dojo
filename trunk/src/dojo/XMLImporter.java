package dojo;

// XMLImporter.java
// Written by Brian Ouellette
// Imports the database from the XML and stores it in a HashMap.

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import java.util.Map;
import java.util.HashMap;

class XMLImporter extends DefaultHandler
{
	// private Writer out;
	// private String version;
	private String eName;
	// Database to map ID or name to card
	// TODO: Serialize these databases and only reload if the xml is newer
	private Map<String, StoredCard> databaseID, databaseName;
	private StoredCard currentCard;

	public XMLImporter()
	{
		super();
		/*try
		{
			// Set up output stream
			out = new OutputStreamWriter(System.out, "UTF8");

		} catch (Throwable t) {
			t.printStackTrace();
		} */

		// Create a new HashMap with an initial capacity of 1500
		// This value might need to be tweaked
		databaseID = new HashMap<String, StoredCard>(1500);
		databaseName = new HashMap<String, StoredCard>(1500);
	}

	public Map<String, StoredCard> getIDDatabase()
	{
		return databaseID;
	}

	public Map<String, StoredCard> getNameDatabase()
	{
		return databaseName;
	}

	/* Disabled, debugging code for if importer starts barfing on reading cards.xml
	public String getVersion()
	{
		return version;
	}

	private void emit(String s)	throws SAXException
	{
		try
		{
			out.write(s);
			out.flush();
		} catch (IOException e) {
			throw new SAXException("I/O error", e);
		}
	}

	private void nl() throws SAXException
	{
		String lineEnd =  System.getProperty("line.separator");
		try
		{
			out.write(lineEnd);

		} catch (IOException e) {
			throw new SAXException("I/O error", e);
		}
	}

	public void startDocument()	throws SAXException
	{
		nl();
		nl();
		emit("START DOCUMENT");
		nl();
	}

	public void endDocument() throws SAXException
	{
		nl();
		emit("END DOCUMENT");
		try {
			nl();
			out.flush();
		} catch (IOException e) {
			throw new SAXException("I/O error", e);
		}
	}*/

	public void startElement(String namespaceURI, String sName, // simple name (localName)
			String qName, // qualified name
			Attributes attrs) throws SAXException
	{
		// nl();
		// emit("ELEMENT: ");
		eName = sName;
		if ("".equals(eName)) {
			eName = qName;
		}
		// emit("<"+eName);

		if (attrs != null) {
			for (int i = 0; i < attrs.getLength(); i++) {
				// nl();
				// emit("   ATTR: ");
				String localName = attrs.getLocalName(i);
				// emit(localName);
				String localValue = attrs.getValue(i);

				/*
				if(localName.equals("version"))
				{
					version = localValue;
				}*/

				if (localName.equals("id")) {
					if (currentCard != null) {
						databaseID.put(currentCard.getID(), currentCard);
						databaseName.put(currentCard.getName(), currentCard);
					}
					currentCard = new StoredCard(localValue);
				} else if (localName.equals("type")) {
					currentCard.setType(localValue);
				} else if (localName.equals("edition")) {
					currentCard.setImageEdition(localValue);
				}
				// emit("\t\"");
				// emit(localValue);
				// emit("\"");
			}
		}
		/*if(attrs.getLength() > 0)
		{
			nl();
		}
		emit(">");*/
	}

	/*
	public void endElement(String namespaceURI,
						   String sName, // simple name
						   String qName  // qualified name
						  )
	throws SAXException
	{
		nl();
		emit("END_ELM: ");
		emit("</"+sName+">");
	}*/

	// Function called when the parser encounters a block of characters
	public void characters(char buf[], int offset, int len) throws SAXException
	{
		// nl();
		// emit("CHARS:   ");
		String s = new String(buf, offset, len);
		// The if statement makes sure it isn't just useless white space
		if (!s.trim().equals("")) {
			// emit(s);

			if (eName.equals("name")) {
				currentCard.setName(s);
			} else if (eName.equals("image")) {
				currentCard.setImageLocation(s);
			} else if (eName.equals("edition")) {
				currentCard.setEdition(s);
			} else if (eName.equals("legal")) {
				currentCard.setLegal(s);
			} else if (eName.equals("text")) {
				currentCard.setText(s);
			} else if (eName.equals("cost")) {
				currentCard.setCost(s);
			} else if (eName.equals("focus")) {
				currentCard.setFocus(s);
			} else if (eName.equals("clan")) {
				currentCard.setClan(s);
			} else if (eName.equals("province_strength")) {
				currentCard.setProvinceStrength(s);
			} else if (eName.equals("gold_production")) {
				currentCard.setGoldProduction(s);
			} else if (eName.equals("starting_honor")) {
				currentCard.setStartingHonor(s);
			} else if (eName.equals("force")) {
				currentCard.setForce(s);
			} else if (eName.equals("chi")) {
				currentCard.setChi(s);
			} else if (eName.equals("personal_honor")) {
				currentCard.setPersonalHonor(s);
			} else if (eName.equals("honor_req")) {
				currentCard.setHonorReq(s);
			} else if (eName.equals("rarity")) {
				currentCard.setRarity(s);
			} else if (eName.equals("flavor")) {
				currentCard.setFlavor(s);
			} else if (eName.equals("artist")) {
				currentCard.setArtist(s);
			} else if (eName.equals("rulings")) {
				currentCard.setRulings(s);
			}
		}
	}
}
