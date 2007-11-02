// PopupListener.java
// Written by Brian Ouellette
// Part of Dojo
// This is the listener for pop-up menus across the application.

package l5r;

import java.awt.event.*;
import javax.swing.*;

class PopupListener extends MouseAdapter
{
	JPopupMenu popup;

	public PopupListener(JPopupMenu popup)
	{
		super();
		this.popup = popup;
	}

	public void mousePressed(MouseEvent e)
	{
		showPopup(e);
	}

	public void mouseReleased(MouseEvent e)
	{
		showPopup(e);
	}

	private void showPopup(MouseEvent e)
	{
		if (e.isPopupTrigger())
		{
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}
}