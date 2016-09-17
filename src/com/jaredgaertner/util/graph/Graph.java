package com.jaredgaertner.util.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;

import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.SimpleGraph;

//import com.mxgraph.view.mxGraph;

/**
 * 
 * 
 * This graph class creates a simple graph, using the JGraphT library,
 * and returns a JGraph object which can be drawn.
 * 
 * As this is a simple graph (and not a listenable graph), any changes
 * made to it after creation of the JGraph object (during the getJGraph()
 * method) will not change the JGraph.  In order to have it change, this
 * class must instead extend a ListenableGraph (or ListenableUndirectGraph).
 * However, the listenable graphs are much slower.
 *
 * @author Jared
 * 
 */
public class Graph extends SimpleGraph<Object, Object> {

	private static final long serialVersionUID = 1L;
	
	private JGraphModelAdapter<?, Object> m_jgAdapter;
	private JGraph jgraph;
	
	String graphName = "";
	private static final int VERTEX_HEIGHT = 5;
	private static final int VERTEX_WIDTH = 5;
	private static final int MIN_VERTEX_SPACE = 30;
	private static final int BORDER_SPACE = 25;
	private static final Color VERTEX_COLOR = Color.BLUE;	
	private static final Color SUBSET_VERTEX_COLOR = Color.RED;
	private static final Color ADDED_EDGE_COLOR = Color.MAGENTA;
	private static final Color ADDED_EXTERNAL_EDGE_COLOR = Color.BLACK;
	
	public Graph(){
		// create a JGraphT graph
		super( DefaultEdge.class );
	}
	
	// Color and position vertices of graph
	public void draw(List<String> vertexSubset, List<String> vertexNotInSubset, List<Object> addedEdges, List<Object> addedExternalEdges, Dimension size){
		// Create a visualization using JGraph, via an adapter
		m_jgAdapter = new JGraphModelAdapter<Object, Object>(this);
		
		int numberOfNotInSubsetVertices = vertexNotInSubset.size();
		int numberOfSubsetVertices = vertexSubset.size();
		int numberOfVertices = numberOfNotInSubsetVertices + numberOfSubsetVertices;
		double subset_percent = (double)numberOfSubsetVertices / numberOfVertices;
		
		boolean randomPlacement = true;	// If true, randomly place vertices in graph, otherwise, placed in square grid

		// Values needed to calculate size of jgraph for random placement and grid placement
		int totalArea = size.width * size.height;
		int minVertexArea = (VERTEX_WIDTH + MIN_VERTEX_SPACE) * (VERTEX_HEIGHT + MIN_VERTEX_SPACE);
		int maximumNumberOfVertices = (int)(totalArea / minVertexArea);
		int side = size.width;
		
		// Randomly place the vertices in the graph space (find width/height such that they're equal and can fit all vertices)
		if(randomPlacement){
			Random generator = new Random();
			if( numberOfVertices >= maximumNumberOfVertices){
				side = (int)Math.sqrt(minVertexArea * numberOfVertices);
			}
			for(String v : vertexSubset){
				// Draw randomly on left side (get random value from 0.0 to subset_percent for x)
				drawSubsetVertexAt(v, (int)(generator.nextDouble() * subset_percent * side), (int)(generator.nextDouble() * side));
			}
			for(String u : vertexNotInSubset){
				// Draw randomly on right side (get random value from subset_percent to 1.0 for x)
				drawVertexAt(u, (int)((generator.nextDouble() * (1.0 - subset_percent) + subset_percent) * side), (int)(generator.nextDouble() * side));
			}
		}
		else{
			// Find the required vertex space (or if not enough, use minimum vertex space)
			int vertexSpace = MIN_VERTEX_SPACE;
			if( numberOfVertices < maximumNumberOfVertices){
				int vertexArea = (int)(totalArea / numberOfVertices);
				// vertexArea 	= (VERTEX_WIDTH + vertexSpace) * (VERTEX_HEIGHT + vertexSpace) 
				//			0	= vertexSpace^2 + (VERTEX_HEIGHT+VERTEX_WIDTH)*vertexSpace + (VERTEX_HEIGHT*VERTEX_WIDTH)-vertexArea
				// Solve with quadratic equation
				int a = 1, b = VERTEX_HEIGHT+VERTEX_WIDTH, c = (VERTEX_HEIGHT*VERTEX_WIDTH)-vertexArea;
		        double temp1 = Math.sqrt(b * b - 4 * a * c);
		        
		        double root1 = (-b +  temp1) / (2*a) ;
		        double root2 = (-b -  temp1) / (2*a) ;
	
				vertexSpace = root1 > root2 ? (int)root1 : (int)root2;
			}
			else{
				// Adjust width (and height by extension) so that they nearly the same
				side = (int)Math.sqrt(minVertexArea * numberOfVertices);
				vertexSpace = MIN_VERTEX_SPACE;
			}
			
			// Systematically place the vertices (left side subset vertices, right side not)
			int currentSubsetVertexPositionX = BORDER_SPACE, currentSubsetVertexPositionY = BORDER_SPACE;
			int startRightSide = BORDER_SPACE + (int)(side * subset_percent);
			int currentVertexPositionX = startRightSide, currentVertexPositionY = BORDER_SPACE;		
			
			// Position and color each vertex depending on if it is in the subset or not
			for(String v : vertexSubset){
				drawSubsetVertexAt(v, currentSubsetVertexPositionX, currentSubsetVertexPositionY);
				currentSubsetVertexPositionX += (VERTEX_WIDTH + vertexSpace);
				if(currentSubsetVertexPositionX + VERTEX_WIDTH >= startRightSide){
					currentSubsetVertexPositionX = BORDER_SPACE;
					currentSubsetVertexPositionY += (VERTEX_HEIGHT + vertexSpace);
				}
			}
			for(String u : vertexNotInSubset){
				drawVertexAt(u, currentVertexPositionX, currentVertexPositionY);
				currentVertexPositionX += (VERTEX_WIDTH + vertexSpace);
				if(currentVertexPositionX + VERTEX_WIDTH >= side){
					currentVertexPositionX = startRightSide;
					currentVertexPositionY += (VERTEX_HEIGHT + vertexSpace);
				}
			}
		}
		
		// Color the added edges
		for(Object e : addedEdges){
			colourAddedEdge(e);
		}
		for(Object e : addedExternalEdges){
			colourAddedExternalEdge(e);
		}		
	}	
	
	public JGraph getJGraph(){		
		jgraph = new JGraph(m_jgAdapter);
		return jgraph;
	}
	
	public void setGraphName(String name){
		graphName = name;
	}
	
	public String getGraphName(){
		return graphName;
	}
	
	private void drawVertexAt(String vertex, int x, int y) {
		DefaultGraphCell cell = m_jgAdapter.getVertexCell(vertex);
		Map<?, ?> attr = cell.getAttributes();
		GraphConstants.setBounds(attr, new Rectangle(x, y, VERTEX_WIDTH, VERTEX_HEIGHT));
		GraphConstants.setBackground(attr, VERTEX_COLOR);
		
		Map<DefaultGraphCell, Map<?, ?>> cellAttr = new HashMap<DefaultGraphCell, Map<?, ?>>();
		cellAttr.put(cell, attr);
		m_jgAdapter.edit(cellAttr, null, null, null);
	}
	
	private void drawSubsetVertexAt(String vertex, int x, int y) {
		DefaultGraphCell cell = m_jgAdapter.getVertexCell(vertex);
		Map<?, ?> attr = cell.getAttributes();
		GraphConstants.setBounds(attr, new Rectangle(x, y, VERTEX_WIDTH, VERTEX_HEIGHT));
		GraphConstants.setBackground(attr, SUBSET_VERTEX_COLOR);
		
		Map<DefaultGraphCell, Map<?, ?>> cellAttr = new HashMap<DefaultGraphCell, Map<?, ?>>();
		cellAttr.put(cell, attr);
		m_jgAdapter.edit(cellAttr, null, null, null);
	}
	
	public void colourAddedEdge(Object e){
		DefaultGraphCell cell = m_jgAdapter.getEdgeCell(e);
		Map<?, ?> attr = cell.getAttributes();

		GraphConstants.setLineColor(attr, ADDED_EDGE_COLOR);
		
		Map<DefaultGraphCell, Map<?, ?>> cellAttr = new HashMap<DefaultGraphCell, Map<?, ?>>();
		cellAttr.put(cell, attr);
		m_jgAdapter.edit(cellAttr, null, null, null);		
	}
	
	public void colourAddedExternalEdge(Object e){
		DefaultGraphCell cell = m_jgAdapter.getEdgeCell(e);
		Map<?, ?> attr = cell.getAttributes();

		GraphConstants.setLineColor(attr, ADDED_EXTERNAL_EDGE_COLOR);
		
		Map<DefaultGraphCell, Map<?, ?>> cellAttr = new HashMap<DefaultGraphCell, Map<?, ?>>();
		cellAttr.put(cell, attr);
		m_jgAdapter.edit(cellAttr, null, null, null);			
	}	
}
