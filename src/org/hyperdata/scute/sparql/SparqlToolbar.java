/**
 * 
 */
package org.hyperdata.scute.sparql;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

/**
 * @author danny
 *
 */
public class SparqlToolbar extends JPanel {

	public SparqlToolbar(){
		super();
		JButton button = new JButton("Press Me!");
		add(button);
		JComboBox comboBox = new JComboBox(new EndpointListModel());
		add(comboBox);
	}
}
