package dojo;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

class MyDocumentListener implements DocumentListener
{
	public void insertUpdate(DocumentEvent e) {
		updateLog();
	}
	public void removeUpdate(DocumentEvent e) {
		updateLog();
	}
	public void changedUpdate(DocumentEvent e) {
		//Plain text components do not fire these events
	}

	public void updateLog()
	{
		Deckbuilder.display();
	}
}

/*
if !contain element data match
	remove element
if !contains element data previous && contains element data current
	add element
	sort.


reset
remove
sort

remove or add
			-sort*/