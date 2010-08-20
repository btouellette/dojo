package dojo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


class DeckMenuListener implements ActionListener
{
	public DeckMenuListener()
	{
	}

	public void actionPerformed(ActionEvent e)
	{
		//TODO: RESoLVE error
		String name = ((AbstractButton)e.getSource()).getText();
		if(name.equals("New"))
		{
			if(close()!=2)
				newFile();
		}
		else if(name.equals("Load"))
		{
			if(close()!=2)
			{
				File file = chooseFile();
				if (file!=null)
				{
					newFile();
					readFile(file);
				}
			}
		}
		else if(name.equals("Save"))
		{
			save();
		}
		else if(name.equals("Save As..."))
		{
			saveAs();
		}
		else if(name.equals("Close"))
		{
			Deckbuilder.frame.setVisible(false);
		}
		else if(name.equals("Diagnostics"))
		{
		}
		else if(name.equals("Proxy Printer"))
		{
		}

	}
	private int close()
	{
		int x=1;
		if (Deckbuilder.edit==true)
			x = JOptionPane.showOptionDialog(null,"Save before closing?","Save",JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,null,null,null);
		if (x==0)
			save();
		return x;
	}
	private void newFile()
	{
		Deckbuilder.dyn = new Vector<StoredCard>();
		Deckbuilder.refreshDyn();
		Deckbuilder.fate = new Vector<StoredCard>();
		Deckbuilder.refreshFate();
		Deckbuilder.setFrameTitle(null,false);
	}
	private File chooseFile()
	{
		File file = null;
		JFileChooser open = new JFileChooser();

		try{
			open.setCurrentDirectory(new File(new File(".\\decks").getCanonicalPath()));
		}catch(IOException e){}

    	open.addChoosableFileFilter(new javax.swing.filechooser.FileFilter()
    	{
    		public boolean accept(File file)
    		{
        		String filename = file.getName();
        		if(file.isDirectory())
        			return true;
        		return filename.endsWith(".jbp");
    		}
    		public String getDescription()
    		{
    			return "*.jbp Files";
    		}
    	});
    	open.setAcceptAllFileFilterUsed(false);

    	int ret = open.showDialog(null, "Open");

	    if (ret == JFileChooser.APPROVE_OPTION)
	    	file = open.getSelectedFile();

		return file;

	}
	private void save()
	{
		if(Deckbuilder.fileName == null)
			saveAs();
		else
		{
			writeFile(new File("decks\\" + Deckbuilder.fileName));
		}
	}
	private void saveAs()
	{
		String s = (String)JOptionPane.showInputDialog(Deckbuilder.frame,"Enter a deck name:","Save As...", JOptionPane.QUESTION_MESSAGE, null,null,null);
		Object[] options = {"Yes","No","Cancel"};
		int n=0;
		if (s!=null)
		{
			File file = new File("decks\\"+ s+ ".jbp");
			try
			{
				if(!file.createNewFile())
					n = JOptionPane.showOptionDialog(null,("File " + file.getName()+ " exists. Overwrite?"),"Save As...",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[0]);
				if (n==0)
					writeFile(file);
			} catch(IOException err){}
		}
	}
	private void writeFile(File file)
	{
		try
		{
    		BufferedWriter out = new BufferedWriter(new FileWriter(file));
    		for (int i=0;i<Deckbuilder.dyn.size();i++)
    			out.write(Deckbuilder.dyn.elementAt(i).getName()+ "|");
    		for (int i=0;i<Deckbuilder.fate.size();i++)
    			out.write(Deckbuilder.fate.elementAt(i).getName()+ "|");
    		out.close();
    		Deckbuilder.setFrameTitle(file.getName(),false);
		} catch (IOException e) {}
	}
	private void readFile(File file)
	{
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(file));

			String str="";

			while ((str = in.readLine()) != null)
			{
				StringTokenizer s = new StringTokenizer(str,"|");
				Object[] p = Main.databaseID.keySet().toArray();

				while(s.hasMoreTokens())
				{
					String val = s.nextToken();
					for(int i=0;i<p.length;i++)
					{
						if (Main.databaseID.get(p[i]).getName().equals(val))
						{
							if(Main.databaseID.get(p[i]).isDyn())
								Deckbuilder.dyn.add(Main.databaseID.get(p[i]));
							else
								Deckbuilder.fate.add(Main.databaseID.get(p[i]));
						}
					}
					Deckbuilder.refreshDyn();
					Deckbuilder.refreshFate();

				}
			}
			in.close();

			Deckbuilder.setFrameTitle(file.getName(),false);


		}catch (IOException e) {}

	}
}