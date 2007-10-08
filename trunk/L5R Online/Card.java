/**
 * @(#)Card.java
 *
 *
 * @author 
 * @version 1.00 2007/10/5
 */
 
 package l5r;

public class Card
{
	//Info
	String id, name, edition, image, legality, type;
	String text, cost, focus;
	String force, chi, hr, ph;
	String clan;

    public Card(String cardID)
    {
    	id = cardID;
    	//Look up in HashTable
    }
    
    
}