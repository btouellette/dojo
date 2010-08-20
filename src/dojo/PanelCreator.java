package dojo;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PanelCreator extends JPanel
{
	private static final long serialVersionUID = 3037675290112377311L;
	JTextField min,max;

    public PanelCreator()
    {
		//JPanel panel = new JPanel();
		min = new JTextField(3);
		min.setText("");
		min.getDocument().addDocumentListener(new MyDocumentListener());
		max = new JTextField(3);
		max.setText("");
		max.getDocument().addDocumentListener(new MyDocumentListener());
		JLabel to = new JLabel(" to ");
		to.setMaximumSize(new Dimension(20,20));
		to.setFont(new Font("Serif", Font.PLAIN, 12));
		setMaximumSize(new Dimension(140,20));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(min);
		add(to);
		add(max);
    }
}