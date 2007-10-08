package l5r;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
//import java.awt.event.*;

public class Main
{
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI()
    {
        //Create and set up the window
        JFrame frame = new JFrame("L5R Online");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Grab the screen resolution
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int)(screenSize.getWidth()*.75);
        int height = (int)(screenSize.getHeight()*.5);

		//Create the menu bar
		JMenuBar menuBar = new JMenuBar();
		menuBar.setOpaque(true);
		//menuBar.setBackground(Color.CYAN);
		menuBar.setPreferredSize(new Dimension(width, 25));

		//Create the main play area
        PlayArea playArea = new PlayArea();
        playArea.setOpaque(true);
        playArea.setBackground(Color.WHITE);
        playArea.setPreferredSize(new Dimension(width, height));

        //Create the chat/info area
        JPanel infoArea = new JPanel();
        infoArea.setOpaque(true);
        infoArea.setBackground(Color.LIGHT_GRAY);
        infoArea.setPreferredSize(new Dimension(width, 175));
        //Add a pretty border to it
		infoArea.setBorder(BorderFactory.createLoweredBevelBorder());
		infoArea.setLayout(new BorderLayout());
		
		//Create the chat area
		JEditorPane chatBox = new JEditorPane();
		JTextField chatSend = new JTextField();
		chatBox.setEditable(false);
		chatBox.setContentType("text/rtf");
		JScrollPane scrollPane = new JScrollPane(chatBox);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		chatSend.addActionListener(new TextActionListener(chatBox));
		infoArea.add(scrollPane, BorderLayout.CENTER);
		infoArea.add(chatSend, BorderLayout.SOUTH);

		//Create a split panel so that the relative size of the areas can be user controlled
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, playArea, infoArea);
		//Don't use the ugly default Java divider
		splitPane.setUI(new BasicSplitPaneUI());
		splitPane.setDividerSize(5);

        //Set the menu bar and add the label to the content pane
        frame.setJMenuBar(menuBar);
        frame.getContentPane().add(splitPane, BorderLayout.CENTER);

        //Display the window
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args)
    {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                createAndShowGUI();
            }
        });
    }
}