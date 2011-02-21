
package org.hyperdata.scute.main;

import java.awt.CardLayout;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;


public class CardPanel extends JPanel {

	private CardLayout layout;
	private EventListenerList listenerList = new EventListenerList();
	private ChangeEvent changeEvent = null;
	
	public CardPanel(){
		super();
		layout = new CardLayout();
		setLayout(layout);

	}
	


	 public void addChangeListener(ChangeListener listener) {
	     listenerList.add(ChangeListener.class, listener);
	 }

	 public void removeChangeListener(ChangeListener listener) {
	     listenerList.remove(ChangeListener.class, listener);
	 }


	 // Notify all listeners that have registered interest for
	 // notification on this event type.  The event instance 
	 // is lazily created using the parameters passed into 
	 // the fire method.

	 protected void fireStateChanged() {
	     // Guaranteed to return a non-null array
	     Object[] listeners = listenerList.getListenerList();
	     // Process the listeners last to first, notifying
	     // those that are interested in this event
	     for (int i = listeners.length-2; i>=0; i-=2) {
	         if (listeners[i]==ChangeListener.class) {
	             // Lazily create the event:
	             if (changeEvent == null)
	            	 changeEvent = new ChangeEvent(this);
	             ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
	         }
	     }
	 }

}
