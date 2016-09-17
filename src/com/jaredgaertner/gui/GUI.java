package com.jaredgaertner.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jaredgaertner.app.SubsetGraphAnonymizationGreedy;
import com.jaredgaertner.util.graph.Graph;
import com.jaredgaertner.util.io.*;

public class GUI extends JPanel implements ActionListener, ChangeListener {
	private static final long serialVersionUID = 1L;
	public final static int WIDTH = 600;
	public final static int GRAPH_HEIGHT = 400;
	public final static int MENU_HEIGHT = 20;
	public final static int TEXT_HEIGHT = 200;
	public final static int FILLER_HEIGHT = 10;
	public final static int FILLER_WIDTH = 10;
	
	private static final Dimension DEFAULT_GRAPH_SIZE = new Dimension(WIDTH, GRAPH_HEIGHT);
	
	JScrollPane graphScrollPane;
	JTextArea textArea;
	
	// Parameters for subset graph anonymization
	private int k = 3;	// Specifies size of group needed for a vertex to be anonymous (ie. every vertex must have at least k-1 vertices of the same degree)
	private double subset_percent = 0.5;  // Specifies the number of vertices of the subset relative to the original graph
	private boolean randomSubsetValue = false;  // True, takes random vertices for the subset, otherwise, takes the highest degree vertices
	
	// Current graph file name [no. vertices, no. edges]: 
	//		karate_graph.txt [34,78], USpowerGrid.mtx[4941,6594], Wiki-Vote.txt [7115,103689]
	private static final String[] graphNames = {"Karate Graph", "US Power Grid Graph", "Wikipedia Vote Graph"};
    private static final Map<String, String> graphFileName = new HashMap<String, String>();
    static {
    	graphFileName.put(graphNames[0], "karate_graph.txt");
    	graphFileName.put(graphNames[1], "USpowerGrid.mtx");
    	graphFileName.put(graphNames[2], "Wiki-Vote.txt");
    }
    private static final Map<String, Integer> numberOfVertices = new HashMap<String, Integer>();
    static {
    	numberOfVertices.put(graphNames[0], 78);
    	numberOfVertices.put(graphNames[1], 4941);
    	numberOfVertices.put(graphNames[2], 7115);
    }    
    String currentGraphName = graphNames[0];
	
	public GUI() {
        // Set layout of the main panel
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        
        // Create blank scroll pane as placeholder for graph
        graphScrollPane = new JScrollPane();
        graphScrollPane.setPreferredSize(DEFAULT_GRAPH_SIZE);
        
        // Create field with k values
        JLabel kLabel = new JLabel("k");
        kLabel.setAlignmentX(CENTER_ALIGNMENT);
    	JSlider kValues = new JSlider(JSlider.HORIZONTAL, 2, 20, 3);
    	kValues.setName("kValues");
    	kValues.addChangeListener(this);
    	// Turn on labels at major tick marks.
    	kValues.setMajorTickSpacing(2);
    	kValues.setMinorTickSpacing(1);
    	kValues.setPaintTicks(true);
    	kValues.setPaintLabels(true);
    	
    	JPanel kPanel = new JPanel(); // Create panel which holds slider and label for it
    	kPanel.setLayout(new BoxLayout(kPanel, BoxLayout.PAGE_AXIS));
    	kPanel.add(kLabel);
    	kPanel.add(kValues);
    	
        // Create field with subset values (multiplied by 20, but display in range of 0.0 to 1.0)
    	JLabel subsetPercentLabel = new JLabel("Subset Percent");
    	subsetPercentLabel.setAlignmentX(CENTER_ALIGNMENT);
    	JSlider subsetPercentValues = new JSlider(JSlider.HORIZONTAL, 0, 20, 10);
    	subsetPercentValues.setName("subsetPercentValues");
    	subsetPercentValues.addChangeListener(this);
    	// Turn on labels at major tick marks.
    	subsetPercentValues.setMajorTickSpacing(5);
    	subsetPercentValues.setMinorTickSpacing(1);
    	subsetPercentValues.setPaintTicks(true);
    	subsetPercentValues.setPaintLabels(true);
    	// Set custom labels
    	Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
    	labelTable.put( new Integer( 0 ), new JLabel("0.0") );
    	labelTable.put( new Integer( 5 ), new JLabel("0.25") );    	
    	labelTable.put( new Integer( 10 ), new JLabel("0.5") );
    	labelTable.put( new Integer( 15 ), new JLabel("0.75") );    	
    	labelTable.put( new Integer( 20 ), new JLabel("1.0") );
    	subsetPercentValues.setLabelTable(labelTable);
    
    	JPanel subsetPercentPanel = new JPanel(); // Create panel which holds slider and label for it
    	subsetPercentPanel.setLayout(new BoxLayout(subsetPercentPanel, BoxLayout.PAGE_AXIS));
    	subsetPercentPanel.add(subsetPercentLabel);
    	subsetPercentPanel.add(subsetPercentValues);
    	
        // Create list of graphs to choose from
    	JLabel graphOptionsLabel = new JLabel("Graph Options");
    	graphOptionsLabel.setAlignmentX(CENTER_ALIGNMENT);
        JComboBox<?> graphOptions = new JComboBox<Object>(graphNames);
        graphOptions.setSelectedIndex(0);
        graphOptions.setActionCommand("graphOptions");
        graphOptions.addActionListener(this);
    	
        JPanel graphOptionsPanel = new JPanel(); // Create panel which holds combobox and label for it
        graphOptionsPanel.setLayout(new BoxLayout(graphOptionsPanel, BoxLayout.PAGE_AXIS));
        graphOptionsPanel.add(graphOptionsLabel);
        graphOptionsPanel.add(graphOptions);
    	
        // Set layout parameters and add components
        JPanel parametersPanel = new JPanel();
        parametersPanel.setLayout(new BoxLayout(parametersPanel, BoxLayout.LINE_AXIS));
        parametersPanel.add(kPanel);
        parametersPanel.add(Box.createRigidArea(new Dimension(FILLER_WIDTH, 0)));
        parametersPanel.add(subsetPercentPanel);
        parametersPanel.add(Box.createRigidArea(new Dimension(FILLER_WIDTH, 0)));        
        parametersPanel.add(graphOptionsPanel);        
        
        // Create text area
        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(WIDTH, TEXT_HEIGHT));	
        
        // Output console message to text area
        MessageConsole messageConsole = new MessageConsole(textArea);
        messageConsole.redirectOut();
        messageConsole.redirectErr(Color.RED, null);
        
        // Create start anonymization button
        JPanel buttonPanel = new JPanel();
        JButton startAnonymization = new JButton("Start Anonymization");
        startAnonymization.setActionCommand("startAnonymization");
        startAnonymization.addActionListener(this);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(startAnonymization);
        
        this.add(graphScrollPane);
        this.add(Box.createRigidArea(new Dimension(0, FILLER_HEIGHT)));
        this.add(parametersPanel);
        this.add(Box.createRigidArea(new Dimension(0, FILLER_HEIGHT)));        
        this.add(scrollPane);
        this.add(Box.createRigidArea(new Dimension(0, FILLER_HEIGHT)));
        this.add(buttonPanel);
        this.setBorder(BorderFactory.createEmptyBorder(FILLER_HEIGHT, FILLER_WIDTH, FILLER_HEIGHT, FILLER_WIDTH));
	}
	
	public void actionPerformed(ActionEvent e){
		if("startAnonymization".equals(e.getActionCommand())){
			textArea.setText("");
			startAnonymization();
		}
		else if("graphOptions".equals(e.getActionCommand())){
			JComboBox<?> graphOptions = (JComboBox<?>)e.getSource();
			currentGraphName = (String)graphOptions.getSelectedItem();
		}
		else{
			System.err.println("GUI: Unknown action performed");
		}
	}
	
    public void stateChanged(ChangeEvent e) {
    	JSlider slider = (JSlider)e.getSource();
        if (!slider.getValueIsAdjusting()) {
        	if("kValues".equals(slider.getName())){
	        	k = slider.getValue();
        	}
        	if("subsetPercentValues".equals(slider.getName())){
        		subset_percent = slider.getValue() / 20.0;
        	}
        }
    }	
	
	public void startAnonymization(){
		// Read in a graph file
		ReadWriteGraphFile graphFile = new ReadWriteGraphFile(graphFileName.get(currentGraphName));
		graphFile.readGraphFile();
		Graph graph = graphFile.getGraph();
		graph.setGraphName(currentGraphName);
		
		// Perform subset graph anonymization (greedy) solution
		SubsetGraphAnonymizationGreedy sga = new SubsetGraphAnonymizationGreedy(graph, k, subset_percent, randomSubsetValue);		
		sga.solve();
		sga.printInputParameters();
		sga.printDegreeSequences();
		sga.printOutput();
		
		// Adjust position and display graph and components
		graph.draw(sga.getVertexSubset(), sga.getVertexNotInSubset(), sga.getAddedEdges(), sga.getAddedExternalEdges(), new Dimension(graphScrollPane.getWidth() - 20, GRAPH_HEIGHT));

    	// Set the viewing area in the jscrollpane to the current jgraph
    	graphScrollPane.setViewportView(graph.getJGraph());
	}
}
