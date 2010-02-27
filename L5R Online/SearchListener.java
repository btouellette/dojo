// SearchListener.java
// Written by James Spencer
// Part of Dojo

import java.awt.event.*;
import javax.swing.*;
import java.util.Vector;

class SearchListener implements ActionListener
{

	public static Vector<StoredCard> storage;
	String type,clan,legal;
	int max, y;
	JComboBox cb;
	JTextField tf;
	String item,text,title;
	public SearchListener()
	{
		storage = new Vector<StoredCard>();
		type="";
		clan="";
		item="";
		text="";
		title="";
		legal="samurai";
		storage=new Vector<StoredCard>();
		for (int x=0;x<Deckbuilder.vect.size();x++)
			storage.add(Deckbuilder.vect.elementAt(x));
		cb = new JComboBox();
		tf = new JTextField();
	}
	public void actionPerformed(ActionEvent e)
	{
	        if(e.getSource() instanceof JComboBox)
	        {

				JComboBox cb = (JComboBox)e.getSource();
	        	item = (String)cb.getSelectedItem();
	        }
	        else if (e.getSource() instanceof JTextField)
	        {
				JTextField tf = (JTextField)e.getSource();
				text = tf.getText();
			}

			//textArea.append(text + newline);
    		//textField.selectAll();

    		//assign Legal menu option
			if (item.equals(Deckbuilder.Legal.getSelectedItem().toString()))
				legal=item;

			//assign Card Type menu option
			else if (item.equals(Deckbuilder.CardType.getSelectedItem().toString()))
			{
				type=item;
				if (type.equals("Personality"))
					type="personalities";
			}

			//assign Faction menu option
			else if (item.equals(Deckbuilder.faction.getSelectedItem().toString()))
				clan=item;

			//assign Title TextField option
			else if (text.equals(Deckbuilder.title.getText()))
				title=text;



			System.out.println(title);
			Display();
    }

    public void Display()
    {
		//reset the vector
		Deckbuilder.vect.clear();

		for (int x=0;x<storage.size();x++)
			Deckbuilder.vect.add(storage.elementAt(x));

		//reset variables
		max=Deckbuilder.vect.size();
		y=0;

		do
		{
			if(!Deckbuilder.vect.elementAt(y).getLegal().contains(legal.toLowerCase()))
				Deckbuilder.vect.remove(y);
			else if(!Deckbuilder.vect.elementAt(y).getType().startsWith(type.toLowerCase()))
				Deckbuilder.vect.remove(y);
			else if (!clan.equals("")&&!Deckbuilder.vect.elementAt(y).getClan().contains(clan.toLowerCase()))
				Deckbuilder.vect.remove(y);
			//title check = not working
			else if (Deckbuilder.vect.elementAt(y).getName().toLowerCase().indexOf(title.toLowerCase())<0)
				Deckbuilder.vect.remove(y);
			else y++;

			max=Deckbuilder.vect.size();

		}
		while(y<max);

		refresh();
	}

	public void refresh()
	{
		Deckbuilder.alphasort(Deckbuilder.vect);
		Deckbuilder.apples.setText("Card Results("+Deckbuilder.vect.size()+"):");
		Deckbuilder.list.setListData(Deckbuilder.vect);
	}
}