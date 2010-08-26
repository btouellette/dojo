package dojo;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class ListActionListener extends MouseAdapter
{
	public ListActionListener()
	{
	}

	public void mouseClicked(MouseEvent e)
	{
		if(e.getClickCount()%2 == 0)
		{
			int index = Deckbuilder.list.locationToIndex(e.getPoint());
			//String val = Deckbuilder.vect.elementAt(index).getType();
			try{
				if(Deckbuilder.vect.elementAt(index).isDynasty())
				{
					if(!(Deckbuilder.vect.elementAt(index).getType().equals("strongholds")
							&& Deckbuilder.hasSH==true))
						Deckbuilder.dyn.add(Deckbuilder.vect.elementAt(index));
					Deckbuilder.refreshDyn();
				}
				else if (!Deckbuilder.vect.elementAt(index).isDynasty())
				{
					Deckbuilder.fate.add(Deckbuilder.vect.elementAt(index));
					Deckbuilder.refreshFate();
				}
			}catch(ArrayIndexOutOfBoundsException excptn){}
			Deckbuilder.setFrameTitle(Deckbuilder.fileName,true);
		}
	}
}