/**
 * 
 */
package org.hyperdata.scute.window;

import java.awt.CardLayout;

import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.JXTitledPanel;

import org.hyperdata.resources.scute.ScuteIcons;

public class TaskPanel extends JXTitledPanel {

	private JXTaskPaneContainer taskPaneContainer;
	CardPanel cardPanel;
	CardLayout layout;

	public TaskPanel(CardPanel cardPanel) {
		super("Activities");
		this.cardPanel = cardPanel;
		this.layout = (CardLayout) cardPanel.getLayout();
		taskPaneContainer = new JXTaskPaneContainer();
		this.setContentContainer(new JScrollPane(taskPaneContainer));
		addTaskPanes();
	}

	private void addTaskPanes() {
		addEditorTaskPane();
		addSparqlTaskPane();
		addDataManagerTaskPane();
		addSystemTaskPane();
	}


	@SuppressWarnings("deprecation")
	private void addEditorTaskPane() {
		JXTaskPane taskPane = new JXTaskPane();
		taskPane.setIcon(ScuteIcons.rdfIcon);
		taskPane.setTitle("View/Edit RDF");
		taskPaneContainer.add(taskPane);

		taskPane.add(new ChangeEditorPanelAction(this, "Turtle", "Turtle View"));
		taskPane.add(new ChangeEditorPanelAction(this, "RDF/XML", "RDF/XML View"));
		taskPane.add(new ChangeEditorPanelAction(this, "Tree", "Tree View"));
		taskPane.add(new ChangeEditorPanelAction(this, "Graph", "Graph View"));
	}

	@SuppressWarnings("deprecation")
	private void addSparqlTaskPane() {
		JXTaskPane taskPane = new JXTaskPane();
		taskPane.setIcon(ScuteIcons.sparqlIcon);
		taskPane.setTitle("Run SPARQL Queries");
		taskPaneContainer.add(taskPane);

		taskPane.add(new ChangeEditorPanelAction(this, "SPARQL", "SPARQL Editor"));
	}
	

	private void addDataManagerTaskPane() {
		JXTaskPane taskPane = new JXTaskPane();
		taskPane.setIcon(ScuteIcons.rdfIcon);
		taskPane.setTitle("Manage Data");
		taskPaneContainer.add(taskPane);

		taskPane.add(new ChangeEditorPanelAction(this, "Files", "File Manager"));
		
		taskPane.add(new ChangeEditorPanelAction(this, "Graphs", "Graph Manager"));
		
		taskPane.add(new DocPanelAction(this, "Data", "Documentation")); // TODO need to pass help context
	}


	private void addSystemTaskPane() {
		JXTaskPane taskPane = new JXTaskPane();
		taskPane.setIcon(ScuteIcons.rdfIcon);
		taskPane.setTitle("System Features");
		taskPaneContainer.add(taskPane);

		taskPane.add(new ChangeEditorPanelAction(this, "System", "Settings"));
		taskPane.add(new ChangeEditorPanelAction(this, "Log", "System Log"));
	}



}
