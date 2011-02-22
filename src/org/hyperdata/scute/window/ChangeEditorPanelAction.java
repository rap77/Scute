/**
 * 
 */
package org.hyperdata.scute.window;

import java.awt.event.ActionEvent;

import org.jdesktop.swingx.action.AbstractActionExt;

final class ChangeEditorPanelAction extends
		AbstractActionExt {
//	private String label;

	/**
	 * 
	 */
	private final TaskPanel taskPanel;

	public ChangeEditorPanelAction(TaskPanel taskPanel, String command, String label) {
		// setName(panelName);
		super(label,command);
	//	this.label = label;
		this.taskPanel = taskPanel;
	}

	public void actionPerformed(ActionEvent actionEvent) {
    	System.out.println("AACCTTIIOONN = "+actionEvent);
    	// CardLayout cLay = cardPanel.getLayout();
    	// cLay.show(cardPanel,"panel1Identifier");
    	
    	taskPanel.cardPanel.fireChange(actionEvent);
    	taskPanel.layout.show(this.taskPanel.cardPanel, actionEvent.getActionCommand());
    }
}