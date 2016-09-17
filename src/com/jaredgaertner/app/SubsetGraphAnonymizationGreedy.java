package com.jaredgaertner.app;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import com.jaredgaertner.util.graph.*;

public class SubsetGraphAnonymizationGreedy {
	private DegreeSequence degreeSequence, subsetDegreeSequence, anonymizedSubsetDegreeSequence, finalSubsetDegreeSequence, finalDegreeSequence, upperBounds, finalUpperBounds;
	private Graph graph;
	List<String> vertexSubset, vertexNotInSubset;
	List<Object> addedEdges, addedExternalEdges;
	private int k;
	private double subsetPercent;
	boolean randomSubset;  // Random subset if true, otherwise take the highest degree nodes
	DegreeAnonymizationGreedy da;
	
	public SubsetGraphAnonymizationGreedy(Graph g, int kValue, double subsetPercentValue, boolean randomSubsetValue){
		graph = g;
		k = kValue;
		subsetPercent = subsetPercentValue;
		randomSubset = randomSubsetValue;
		degreeSequence = new DegreeSequence(graph);
		
		// Find set of vertices (set of vertices for G), which is sorted in DegreeSequence constructor (except empty constructor)
		List<String> vertexSet = new ArrayList<String>();
		for(int i = 0; i < degreeSequence.size(); i++){
			vertexSet.add(degreeSequence.getVertex(i));
		}
		
		int numberOfSubsetVertices = (int)(subsetPercent * vertexSet.size());
		// Set numberOfSubsetVertices to k, if less than k
		if(numberOfSubsetVertices < k){
			numberOfSubsetVertices = k;
		}
		
		// Find random subset of vertices (subset named X, induced subgraph named G_X)
		if( randomSubset ){
			Collections.shuffle(vertexSet);
			vertexSubset = new ArrayList<String>(vertexSet.subList(0, numberOfSubsetVertices));
			vertexNotInSubset = new ArrayList<String>(vertexSet.subList(numberOfSubsetVertices, vertexSet.size()));
		}
		// Find highest degree subset of vertices
		else{
			vertexSubset = new ArrayList<String>(vertexSet.subList(0, numberOfSubsetVertices));
			vertexNotInSubset = new ArrayList<String>(vertexSet.subList(numberOfSubsetVertices, vertexSet.size()));
		}
		
		// Find degree sequence of G_X, with respect to their degrees in G
		subsetDegreeSequence = new DegreeSequence(graph, vertexSubset);
	}
	
	public void solve(){
		findDegreeAnonymizationGreedy();
		findUpperBounds();
		findGreedyMatching();
		findExternalMatching();
		finalSubsetDegreeSequence = new DegreeSequence(graph, vertexSubset);
		finalDegreeSequence = new DegreeSequence(graph);
	}
	
	private void findDegreeAnonymizationGreedy(){
		da = new DegreeAnonymizationGreedy(k, subsetDegreeSequence);
		anonymizedSubsetDegreeSequence = da.getAnonymizedDegreeSequence();
	}
	
	//  Find the upper bounds for adding edges to make the subset of vertices anonymous in G
	private void findUpperBounds(){
		upperBounds = new DegreeSequence();
		for(int i = 0; i < subsetDegreeSequence.size(); i++){
			// If a vertex in upperbounds is 0, don't add it
			int u_i = anonymizedSubsetDegreeSequence.get(i) - subsetDegreeSequence.get(i);
			if( u_i > 0 ){
				String v = subsetDegreeSequence.getVertex(i);
				upperBounds.add(v, u_i);
			}
		}
	}
	
	// Find potential matches for edges to add to G_X to satisfy the upper bounds of the vertices in X
	private void findGreedyMatching(){
		// List to keep track of added edges
		addedEdges = new ArrayList<Object>();
		
		// Iterate through upperBoundsNoZeroes to check if edges can be added between any such vertices
		upperBounds.sort();
		finalUpperBounds = new DegreeSequence(upperBounds);
		for(int i = 0; i-1 < finalUpperBounds.size(); i++){
			for(int j = i+1; j < finalUpperBounds.size(); j++){
				String u = finalUpperBounds.getVertex(i);
				String v = finalUpperBounds.getVertex(j);
				// Check if the upper bounds of both vertices is still about 0 and G doesn't contain the edge (u,v)
				if(finalUpperBounds.get(i) > 0 && finalUpperBounds.get(j) > 0 && !graph.containsEdge(u,v)){
					graph.addEdge(u,v);
					addedEdges.add(graph.getEdge(u, v));
					finalUpperBounds.decrement(i);
					finalUpperBounds.decrement(j);
				}
			}
		}
	}
	
	// Find potential matches for edges to add from G_X to G\X
	public void findExternalMatching(){
		// List to keep track of added edges
		addedExternalEdges = new ArrayList<Object>();		
		
		// Shuffle vertexNotInSubset so adding external edges is random, not just to highest degree nodes in vertexNotInSubset (as it is initially sorted by degree)
		Collections.shuffle(vertexNotInSubset);

		// Add edges
		for(int i = 0; i < finalUpperBounds.size(); i++){
			for(String v : vertexNotInSubset){
				String u = finalUpperBounds.getVertex(i);
				if(finalUpperBounds.get(i) > 0 && !graph.containsEdge(u,v)){
					graph.addEdge(u,v);
					addedExternalEdges.add(graph.getEdge(u, v));
					finalUpperBounds.decrement(i);			
				}
			}
		}
	}
	
	public void printInputParameters(){
		System.out.println();
		System.out.println("||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
		System.out.println("	Input Parameters");
		System.out.println("||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
		System.out.println("Current graph name: " + graph.getGraphName());
		System.out.println("k value: " + k);
		System.out.println("X percent size: " + subsetPercent);
		System.out.println("X determination: " + (randomSubset ? "Random" : "Highest degree vertices"));
		System.out.println("||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
		System.out.println();
	}	
	
	public void printDegreeSequences(){
		System.out.println();
		System.out.println("||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
		System.out.println("	Upper Bounds and Degree Sequences");
		System.out.println("||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
		System.out.println("Upper bounds: "); upperBounds.printAll();
		System.out.println("Initial degree sequence: "); degreeSequence.printDegrees();
		System.out.println("Subset degree sequence: "); subsetDegreeSequence.printDegrees();
		System.out.println("Anonymized subset degree sequence: "); anonymizedSubsetDegreeSequence.printDegrees();
		System.out.println("Final subset degree sequence: "); finalSubsetDegreeSequence.printDegrees();	
		System.out.println("Final degree sequence: "); finalDegreeSequence.printDegrees();		
		System.out.println("||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
		System.out.println();
	}
	
	public void printOutput(){
		System.out.println();
		System.out.println("||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
		System.out.println("	Outputs");
		System.out.println("||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
		System.out.println("Upper bounds after anonymization: "); finalUpperBounds.printAll();
		System.out.println("Added edges to G_X:");
		for(Object e : addedEdges){
			System.out.print(" (" + graph.getEdgeSource(e) + "," + graph.getEdgeTarget(e)  + "),");
		}
		System.out.println();
		System.out.println("Added edge to G_X from G\\X:");
		for(Object e : addedExternalEdges){
			System.out.print(" (" + graph.getEdgeSource(e) + "," + graph.getEdgeTarget(e)  + "),");
		}
		System.out.println();
		System.out.println("||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
		System.out.println();
	}	
	
	public List<String> getVertexSubset(){
		return vertexSubset;
	}
	
	public List<String> getVertexNotInSubset(){
		return vertexNotInSubset;
	}
	
	public List<Object> getAddedEdges(){
		return addedEdges;
	}
	
	public List<Object> getAddedExternalEdges(){
		return addedExternalEdges;
	}	
}
