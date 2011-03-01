/**
 * 
 */
package org.hyperdata.scute.sparql.panels;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import org.hyperdata.scute.sparql.SparqlContainer;
import org.hyperdata.scute.sparql.actions.RunQueryAction;
import org.hyperdata.scute.sparql.actions.StopQueryAction;
import org.hyperdata.scute.sparql.endpoints.EditEndpointsAction;
import org.hyperdata.scute.sparql.endpoints.Endpoint;
import org.hyperdata.scute.sparql.endpoints.EndpointListModel;
import org.hyperdata.scute.sparql.endpoints.EndpointTableModel;

/**
 * @author danny
 * 
 */
public class SparqlToolbar extends JPanel implements ActionListener {

	private JComboBox comboBox;
	private JTextField uriField;
	private SparqlContainer queryContainer;
	private SparqlSourcePanel sourcePanel;
	private Frame frame;

	public SparqlToolbar(SparqlContainer queryContainer,
			SparqlSourcePanel sourcePanel, Frame frame) {
		super();
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

		this.frame = frame;
		
		this.queryContainer = queryContainer;
		this.sourcePanel = sourcePanel;
		JButton run = new JButton();

		// using sourcepanel here a bit messy, but will do for now
		run.setAction(new RunQueryAction("Run Query", queryContainer,
				sourcePanel));
		add(run);
		add(Box.createHorizontalStrut(10));

		JButton stop = new JButton();
		stop.setAction(new StopQueryAction("Stop"));
		add(stop);

		add(Box.createHorizontalStrut(10));
		add(new JSeparator(SwingConstants.VERTICAL));
		add(Box.createHorizontalStrut(20));

		add(new JLabel("Endpoint:"));
		add(Box.createHorizontalStrut(10));
		EndpointListModel endpointListModel = new EndpointListModel();
		comboBox = new JComboBox(endpointListModel);
		comboBox.setSelectedIndex(0);
		comboBox.addActionListener(this);
		add(comboBox);

		uriField = new JTextField(20);
		uriField.setText("----");
		add(uriField);

		add(Box.createHorizontalStrut(10));

		JButton edit = new JButton();
		EndpointTableModel endpointTableModel = new EndpointTableModel(endpointListModel);
		edit.setAction(new EditEndpointsAction("Edit", endpointTableModel, frame));
		add(edit);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// String selected = (String)comboBox.getSelectedItem();
		Endpoint endpoint = (Endpoint) comboBox.getSelectedItem();
		String uri = endpoint.getUri();
		if (uri != null) {
			uriField.setText(uri);
		} else {
			uriField.setText("---");
		}
		// System.out.println("ENDPOINT ST=" + endpoint);
		queryContainer.setEndpoint(endpoint);
	}
}
