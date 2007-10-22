// CardInfoBox.java
// Written by Brian Ouellette
// Part of Dojo

package l5r;

import java.awt.*;
import javax.swing.*;

class CardInfoBox extends JPanel
{
	Card card;

    public CardInfoBox()
    {
    }

    public void setCard(Card card)
    {
		this.card = card;

	}
}