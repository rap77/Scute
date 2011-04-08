/*
 * Scute
 * 
 * Homepage: http://hyperdata.org/scute
 * 
 * License : http://www.apache.org/licenses/LICENSE-2.0
 * See also license.txt or http://hyperdata.org/wiki/Scute:License
 * 
 * Danny Ayers 2011
 */
package org.hyperdata.scute.main;

import java.awt.BorderLayout;
import org.jdesktop.swingx.*;
import org.jdesktop.swingx.MultiSplitLayout.*;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.Document;

import org.hyperdata.resources.scute.ScuteIcons;
import org.hyperdata.scute.autosave.AutoSave;
import org.hyperdata.scute.autosave.AutoSaveAction;
import org.hyperdata.scute.cards.Card;
import org.hyperdata.scute.cards.CardFactory;
import org.hyperdata.scute.cards.CardsPanel;
import org.hyperdata.scute.cards.TaskPanel;
import org.hyperdata.scute.filemanager.FileExplorerCard;
import org.hyperdata.scute.graph.GraphCard;
import org.hyperdata.scute.graphmanager.GraphManagerCard;
import org.hyperdata.scute.help.HelpUI;
import org.hyperdata.scute.rdf.ModelContainer;
import org.hyperdata.scute.rdf.Models;
import org.hyperdata.scute.source.RdfSourcePanel;
import org.hyperdata.scute.source.popup.PopupListener;
import org.hyperdata.scute.source.popup.SourcePopupMenu;
import org.hyperdata.scute.sparql.panels.SparqlCard;
import org.hyperdata.scute.status.AnimatedCursor;
import org.hyperdata.scute.status.StatusAction;
import org.hyperdata.scute.status.StatusButton;
import org.hyperdata.scute.status.StatusInfoPane;
import org.hyperdata.scute.syntax.ScuteEditorKit;
import org.hyperdata.scute.system.Log;
import org.hyperdata.scute.system.panels.LogPane;
import org.hyperdata.scute.system.panels.SystemCard;
import org.hyperdata.scute.toolbars.file.FileUI;
import org.hyperdata.scute.toolbars.file.IO;
import org.hyperdata.scute.toolbars.source.EditorToolbar;
import org.hyperdata.scute.tree.NodePanel;
import org.hyperdata.scute.tree.RdfTreeNode;
import org.hyperdata.scute.tree.RdfTreeCard;
import org.hyperdata.scute.triples.TriplesCard;
import org.hyperdata.scute.validate.RdfXmlValidateAction;
import org.hyperdata.scute.validate.TurtleValidateAction;

/**
 * The Class Scute.
 */
public class Scute extends ModelContainer implements TreeSelectionListener {

	/** The Constant READ_ONLY_COLOR. */
	public static final Color READ_ONLY_COLOR = (Color) UIManager.getDefaults()
			.get("Button.background");

	/** The Constant READ_WRITE_COLOR. */
	public static final Color READ_WRITE_COLOR = (Color) UIManager
			.getDefaults().get("TextField.background");

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		new Scute();
	}

	/** The frame. */
	private JFrame frame;

	/** The node panel. */
	private NodePanel nodePanel;

	/** The normal cursor. */
	private Cursor normalCursor;

	/** The normal tree cursor. */
	private Cursor normalTreeCursor;

	/** The panel. */
	private final JPanel panel;

	/** The rdfxml panel. */
	private RdfSourcePanel rdfxmlPanel;

	/** The turtle panel. */
	private RdfSourcePanel turtlePanel;

	/** The tabs. */
	// private final JTabbedPane tabs;

	/** The tree panel. */
	private Card treePanel;

	/** The graph panel. */
	private Card graphCard = null;

	private Card settingsPanel;

	private CardsPanel cardsPanel;

	private Card fileExplorerCard;

	private Card sparqlCard;

	private Card graphManagerPanel;

	public static ScuteHelp scuteHelp;

	private AutoSave autoSave;

	private Card triplesPanel;

	private FileUI fileUI;
	private HelpUI helpUI;

	private IO io;

	private ScratchPad scratchPad;

	private EditorToolbar editorToolbar;

	private FocusMonitor focusMonitor;

	private Leaf leftLeaf;

	private Leaf centerLeaf;

	private Leaf rightLeaf;

	private SplitScreen splitScreen;

	private Card imageCard;

	private JPanel controlPanel;

	private JPanel statusBar;

	private JScrollPane scratchPane;

	private JXMultiSplitPane multiSplitPane;

	/**
	 * Instantiates a new scute.
	 */
	public Scute() {

		frame = new JFrame("Scute");

		// FIXME restorePreviousState BROKEN - fix!
		// TODO restorePreviousState BROKEN - fix!

		// for bootstrapping/debugging
		// Config.self.setDefaults();
		// Config.self.saveNow();

		if (Config.self.getSync()) { // previous run was shut down correctly
			System.out.println("CLEAN");
			Config.self.setSync(false);
			Config.self.saveNow();
		}
		setModel(Models.workingModel);
		setModelFilename(Config.WORKING_MODEL_FILENAME);
		setModelURI(Config.WORKING_MODEL_URI);

//		Models.workingModel = Models.sampleModel;

		scuteHelp = new ScuteHelp();

		panel = new JPanel(new BorderLayout());

		focusMonitor = new FocusMonitor();
		
		autoSave = new AutoSave();
		
		editorToolbar = new EditorToolbar(frame);
		focusMonitor.setEditorToolbar(editorToolbar);
		
		makeCardsPanel();
		makeScratchPad();
		makeSplitScreen();

		// effectively presets
		autoSave.setWorkingModelContainer(this);
		autoSave.setWorkingModelContainer(Config.self);
		autoSave.setCurrentTextContainer(turtlePanel);
		autoSave.setScratchTextContainer(scratchPad);
		
		cardsPanel.addChangeListener(autoSave);

		io = new IO(this, cardsPanel);
		fileUI = new FileUI(io);

		makeControlPanel();
		
		helpUI = new HelpUI(io);

		// FIXME basic Save and Load

		// Set up autosave
		// FIXME merge with AutoSave stuff above

		TaskPanel taskPanel = new TaskPanel(cardsPanel);
		multiSplitPane.add(taskPanel, "left");

		/*
		 * FIXME validator, autosave must interrupt/be halted immediately on any
		 * actions only one can run at any given time make singleton?
		 */

		setupFrame();
		
		splitScreen.setFullMiddle();
		showTools(false);
		showStatusBar(false);

		if (Config.self.getSync() == false) { // previous run wasn't shut down
			// correctly
			System.out.println("RESTORE");
			autoSave.restorePreviousState(this);

			cardsPanel.addChangeListener(autoSave);
		}
	}

	/**
	 * 
	 */
	private void makeSplitScreen() {
		// Split Pane stuff
		// there's an awful lot of it...
		leftLeaf = new Leaf("left");
		centerLeaf = new Leaf("center");
		rightLeaf = new Leaf("right");
		
		List children = Arrays.asList(leftLeaf, new Divider(), centerLeaf,
				new Divider(), rightLeaf);
		Split splitModel = new Split();
		splitModel.setChildren(children);
		MultiSplitLayout multiSplitLayout = new MultiSplitLayout();
		multiSplitLayout.setLayoutMode(MultiSplitLayout.NO_MIN_SIZE_LAYOUT);
		multiSplitPane = new JXMultiSplitPane(multiSplitLayout);
		multiSplitPane.getMultiSplitLayout().setModel(splitModel);
		multiSplitPane.add(cardsPanel, "center");
		multiSplitPane.add(new JXTitledPanel("Scratch Pad", scratchPane), "right");

		splitScreen = new SplitScreen(multiSplitPane, leftLeaf, centerLeaf,
				rightLeaf);
		panel.add(multiSplitPane, BorderLayout.CENTER);
	}

	/**
	 * 
	 */
	private void makeScratchPad() {
		scratchPad = new ScratchPad("Text");
		scratchPad.setEditorKit(new ScuteEditorKit("SPARQL"));
		scratchPad.setFilename(Config.SCRATCH_FILENAME);
		scratchPad.addFocusListener(focusMonitor);
		scratchPad.loadSoon(); // load saved contents
		
		scratchPane = new JScrollPane(scratchPad,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scratchPane.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.RAISED));
	}

	/**
	 * 
	 */
	private void makeControlPanel() {
		controlPanel = new JPanel(); // contains JToolBars
		panel.add(controlPanel, BorderLayout.NORTH);
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
		// controlPanel.add(splitButtons.getLeftButton()); more trouble than it was worth
		controlPanel.add(fileUI.getToolBar());
		controlPanel.add(editorToolbar);
		// controlPanel.add(splitButtons.getRightButton()); more trouble than it was worth
	}

	private static final int FRAME_X_INSET = 75;
	private static final int FRAME_Y_INSET = 20;
	private static final int IDEAL_WIDTH = 1200;
	private static final int IDEAL_HEIGHT = 600;	
	
	private void setupFrame() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int maxWidth = screenSize.width-2*FRAME_X_INSET;
		int maxHeight = screenSize.height-2*FRAME_Y_INSET;
		int width = maxWidth < IDEAL_WIDTH ? maxWidth : IDEAL_WIDTH;
		int height = maxHeight < IDEAL_HEIGHT ? maxHeight : IDEAL_HEIGHT;
		frame.setBounds(FRAME_X_INSET, FRAME_Y_INSET, width, height);

		frame.setIconImage(ScuteIcons.applicationIcon.getImage());
		frame.addWindowListener(autoSave);
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileUI.getFileMenu());
		menuBar.add(editorToolbar.getMenu());
		menuBar.add(helpUI.getHelpMenu());

		frame.setJMenuBar(menuBar);
		frame.setContentPane(panel);
		// frame.pack();

		frame.setVisible(true);
	}

	/**
	 * 
	 */
	private void makeCardsPanel() {
		cardsPanel = new CardsPanel();
		makeImagePanel();
		makeTurtlePanel();
		makeRdfXmlPanel();
		makeTreePanel();
		makeGraphPanel();
		makeTriplesPanel();
		makeSparqlPanel();
		makeGraphManagerPanel();
		makeFileExplorerPanel();
		makeLogPanel();
		makeSystemPanel();
		
		cardsPanel.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.RAISED));
		cardsPanel.add(editorToolbar, BorderLayout.SOUTH);
	}

	/**
	 * 
	 */
	private void makeImagePanel() {
		imageCard = CardFactory.createCard(Card.IMAGE);
			((ImageCard)imageCard).setScute(this); 
			//new ImageCard(this);
		
		cardsPanel.addPlain(imageCard, "Image");
	}

	/**
	 * 
	 */
	private void makeSystemPanel() {
		settingsPanel = CardFactory.createCard(Card.SETTINGS);
			// new SystemCard();
		// systemPanel.addUserActivityListener(autoSave);

		cardsPanel.addScroll(settingsPanel, "Settings");
	}

	/**
	 * 
	 */
	private void makeLogPanel() {
		LogPane log = LogPane.getLogPane();
		// JScrollPane logScroll = new JScrollPane(log);
		// logScroll.setBorder(BorderFactory.createLoweredBevelBorder());
		LogPane.println("Ok.");
		// TitledBorder logBorder = BorderFactory.createTitledBorder("Log");
		// logScroll.setBorder(logBorder);
		// cardsPanel.add(logScroll, "Log");
		Card logCard = CardFactory.createCard(Card.DEFAULT);
		logCard.add(log);
		cardsPanel.addScroll(logCard, "Log");
	}

	/**
	 * 
	 */
	private void makeFileExplorerPanel() {
		fileExplorerCard = CardFactory.createCard(Card.FILE_EXPLORER);
			// new FileExplorerCard(Config.DATA_DIR);
		fileExplorerCard.addFocusListener(focusMonitor);
		cardsPanel.add(fileExplorerCard, "Files");
	}

	/**
	 * 
	 */
	private void makeGraphManagerPanel() {
		graphManagerPanel = CardFactory.createCard(Card.GRAPH_MANAGER);
			// new GraphManagerCard();
		graphManagerPanel.addFocusListener(focusMonitor);
		cardsPanel.add(graphManagerPanel, "Graphs");
	}

	/**
	 * 
	 */
	private void makeSparqlPanel() {
		sparqlCard = CardFactory.createCard(Card.SPARQL);
			// new SparqlCard(frame, focusMonitor);
		((SparqlCard)sparqlCard).setFrame(frame);
		((SparqlCard)sparqlCard).addFocusMonitor(focusMonitor);
		
		sparqlCard.setTextCard(true);
		cardsPanel.add(sparqlCard, "SPARQL");
	}

	/**
	 * 
	 */
	private void makeTriplesPanel() {
		triplesPanel = CardFactory.createCard(Card.TRIPLES);
			//new TriplesCard(Models.workingModel);
		triplesPanel.addFocusListener(focusMonitor);
		// triplesPanel.addUserActivityListener(autoSave);
		// TODO create UserActivityListener interface
		// need change listener???
		// TODO ADD SELECTION LISTENER - make shared listener?
		cardsPanel.add(triplesPanel, "Triples");
	}

	/**
	 * 
	 */
	private void makeTreePanel() {
		treePanel = CardFactory.createCard(Card.TREE);
			// new RdfTreeCard(Models.workingModel);
		treePanel.addUserActivityListener(autoSave);
		treePanel.addFocusListener(focusMonitor);
		// need change listener???
		cardsPanel.add(treePanel, "Tree");
	}

	private void makeGraphPanel() {
		graphCard = CardFactory.createCard(Card.GRAPH);
			// new GraphCard(Models.workingModel);
		graphCard.addUserActivityListener(autoSave);
		graphCard.addFocusListener(focusMonitor);
		// need change listener???
		cardsPanel.add(graphCard, "Graph");
	}

	/**
	 * 
	 */
	private void makeRdfXmlPanel() {
		rdfxmlPanel = new RdfSourcePanel("RDF/XML");
		rdfxmlPanel.setFilename(Config.RDFXML_TEMP);
		rdfxmlPanel.addUserActivityListener(autoSave);
		// rdfxmlPanel.loadModel(Models.workingModel);
		rdfxmlPanel.load();
		rdfxmlPanel.setEditorKit(new ScuteEditorKit("XML"));
		rdfxmlPanel.addFocusListener(focusMonitor);

		JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEADING)); // left-aligned
		statusPanel.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED));

		// add to save-on-shutdown list
		autoSave.addSaveable(rdfxmlPanel);
		
		// set up autosave button
		StatusAction autosaveAction = new AutoSaveAction();
		StatusButton autosaveButton = new StatusButton(autosaveAction,
				"Unsaved", "Saving...", "Saved");
		statusPanel.add(autosaveButton);

		// Set up validators
		Document rdfxmlDocument = rdfxmlPanel.getDocument();
		StatusAction rdfxmlAction = new RdfXmlValidateAction(rdfxmlDocument);
		StatusInfoPane validatorPane = new StatusInfoPane(rdfxmlAction);

		// Set up validator button
		StatusButton validatorButton = new StatusButton(rdfxmlAction,
				"Invalid syntax", "Checking syntax...", "Valid syntax");

		statusPanel.add(validatorButton);
		statusPanel.add(validatorPane);

		cardsPanel.addChangeListener(rdfxmlPanel);

		Card rdfxmlCard = CardFactory.createCard(Card.RDFXML);
		rdfxmlCard.setTextCard(true);
		rdfxmlCard.setTextContainer(rdfxmlPanel);
		rdfxmlCard.add(new JScrollPane(rdfxmlPanel), BorderLayout.CENTER);
		rdfxmlCard.add(statusPanel, BorderLayout.SOUTH);

		cardsPanel.add(rdfxmlCard, "RDF/XML");
	}

	/**
	 * 
	 */
	private void makeTurtlePanel() {
		turtlePanel = new RdfSourcePanel("Turtle");

		turtlePanel.setFilename(Config.TURTLE_TEMP);
		turtlePanel.addUserActivityListener(autoSave);
		turtlePanel.setEditorKit(new ScuteEditorKit("Turtle"));
		turtlePanel.loadModel(getModel());
		turtlePanel.load();
		turtlePanel.addFocusListener(focusMonitor);
		
		SourcePopupMenu popupMenu = new SourcePopupMenu(turtlePanel);
		PopupListener popupListener = new PopupListener(popupMenu);
		turtlePanel.addMouseListener(popupListener);

		cardsPanel.addChangeListener(turtlePanel);
		// add to save-on-shutdown list
		autoSave.addSaveable(turtlePanel);
		
		statusBar = new JPanel(new FlowLayout(FlowLayout.LEADING)); // left-aligned
		statusBar.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED));
		// set up autosave button
		StatusAction autosaveAction = new AutoSaveAction();
		StatusButton autosaveButton = new StatusButton(autosaveAction,
				"Unsaved", "Saving...", "Saved");
		statusBar.add(autosaveButton);

		// Set up validators
		Document turtleDocument = turtlePanel.getDocument();
		StatusAction turtleAction = new TurtleValidateAction(turtleDocument);
		StatusInfoPane validatorPane = new StatusInfoPane(turtleAction);

		// Set up validator button
		StatusButton validatorButton = new StatusButton(turtleAction,
				"Invalid syntax", "Checking syntax...", "Valid syntax");

		statusBar.add(validatorButton);
		statusBar.add(validatorPane);
		
		
		Card turtleCard = CardFactory.createCard(Card.TURTLE);
		turtleCard.setTextCard(true);
		turtleCard.setTextContainer(turtlePanel);
		turtleCard.add(new JScrollPane(turtlePanel), BorderLayout.CENTER);
		turtleCard.add(statusBar, BorderLayout.SOUTH);

		cardsPanel.add(turtleCard, "Turtle");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event
	 * .TreeSelectionEvent)
	 */
	@Override
	public void valueChanged(TreeSelectionEvent event) {
		final Object object = ((RdfTreeCard)treePanel).getTree()
				.getLastSelectedPathComponent();
		System.out.println("value changed");
		if ((object == null)) {
			System.out.println("null object");
			return;
		}
		nodePanel.setRdfTreeNode((RdfTreeNode) object);
	}

	public void waitCursor(boolean wait) {
		if (wait) {
			normalCursor = frame.getCursor();
			normalTreeCursor = treePanel.getCursor();
			frame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			treePanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		} else {
			frame.setCursor(normalCursor);
			treePanel.setCursor(normalTreeCursor);
			frame.setCursor(normalCursor);
			treePanel.setCursor(normalTreeCursor);
		}
	}

	// is needed?
	public static void setSystemLookFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception exception) {
			Log.exception(exception);
		}
	}

	/**
	 * @param selectedView
	 */
	public void setSelectedCard(String selectedView) {
		cardsPanel.setCurrentCard(selectedView);
	}

	/**
	 * 
	 */
	public void setDefaultSplit() {
		splitScreen.setDefaults();
	}

	/**
	 * @param b
	 */
	public void showTools(boolean b) {
		controlPanel.setVisible(b);
	}

	/**
	 * @param b
	 */
	public void showStatusBar(boolean b) {
		statusBar.setVisible(b);
	}
}
