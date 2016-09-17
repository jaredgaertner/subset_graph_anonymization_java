package com.jaredgaertner.util.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.jgrapht.graph.Subgraph;

import com.jaredgaertner.util.graph.Graph;

public class DegreeSequence {
	private List<DegreePair> degreeSequence;
	
	private static class DegreePair {
		int degree;
		String vertex;
		
		private DegreePair(String v, int d){
			degree = d;
			vertex = v;
		}
	}
	
	// Initialize an empty ArrayList degreeSequence
	public DegreeSequence(){
		degreeSequence = new ArrayList<DegreePair>();
	}	
	
	// Copy a given degree sequence
	public DegreeSequence(DegreeSequence d){
		degreeSequence = new ArrayList<DegreePair>(d.degreeSequence);
	}
	
	// Create a degree sequence given a graph
	public DegreeSequence(Graph graph){
		List<String> vertexSet = new ArrayList(graph.vertexSet());
		degreeSequence = new ArrayList<DegreePair>();
		
		for(String v : vertexSet){
			degreeSequence.add(new DegreePair(v, graph.degreeOf(v)));
		}
		
		sort();
	}
	
	// Create a subset degree sequence (the degrees, in the original graph, of a subset of vertices)
	public DegreeSequence(Graph graph, List<String> vertexSubset){
		List<String> vertexSet = new ArrayList(graph.vertexSet());
		degreeSequence = new ArrayList<DegreePair>();
		
		for(String v : vertexSet){
			if( vertexSubset.contains(v) ){
				degreeSequence.add(new DegreePair(v, graph.degreeOf(v)));
			}
		}
		
		sort();
	}
	
	public void sort(){
		Collections.sort(degreeSequence, new Comparator<DegreePair>(){
			public int compare(DegreePair v1, DegreePair v2){
				return v2.degree - v1.degree;
			}
		}
		);	
	}
		
	
	public int get(int index){
		return degreeSequence.get(index).degree;
	}
	
	public String getVertex(int index){
		return degreeSequence.get(index).vertex;
	}

	public DegreePair set(int index, int value){
		String vertex = degreeSequence.get(index).vertex;
		return degreeSequence.set(index, new DegreePair(vertex, value));
	}
	
	public DegreePair decrement(int index){
		int value = degreeSequence.get(index).degree - 1;
		return this.set(index, value);
	}
	
	public boolean add(String v, int value){
		return degreeSequence.add(new DegreePair(v, value));
	}
	
//	public DegreePair remove(int index){
//		return degreeSequence.remove(index);
//	}
	
	public int size(){
		return degreeSequence.size();
	}
	
	public void printAll(){
		System.out.print("[Vertex, Value]:");
		for(DegreePair d : degreeSequence){
			System.out.print(" [" + d.vertex + "," + d.degree + "],");
		}
		System.out.println();
	}
	
	public void printDegrees(){
		for(DegreePair d : degreeSequence){
			System.out.print(d.degree + ",");
		}
		System.out.println();
	}	
}
