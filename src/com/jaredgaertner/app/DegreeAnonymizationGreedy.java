package com.jaredgaertner.app;

import com.jaredgaertner.util.graph.DegreeSequence;

/**
 * Performs degree anonymization based on a given degree sequence.
 * 
 * This is an implementation of the degree anonymization algorithm 
 * (Greedy algorithm) talked about in the paper 
 * "Towards Identity Anonymization on Graphs" by Liu and Terzi
 *  
 * @author Jared
 *
 */
public class DegreeAnonymizationGreedy {
	private DegreeSequence degreeSequence;
	
	public DegreeAnonymizationGreedy(int k, DegreeSequence d){
		degreeSequence = new DegreeSequence(d);
		//System.out.print(degreeSequence.get(0) + ",");
		for(int i = 1; i < k; i++){
			degreeSequence.set(i, degreeSequence.get(0));
			//System.out.print(degreeSequence.get(i) + ",");
		}
		
		int start_index = 0;
		int j;
		for(j = k; j+k < degreeSequence.size();){
			
			int c_merge = degreeSequence.get(start_index)-degreeSequence.get(j) + I(j+1,j+k);
			int c_new = I(j,j+k-1);
			
			if(c_merge > c_new){
				// Create new group from j to j+k-1
				//System.out.print(degreeSequence.get(j) + ",");
				for(int l = j+1; l < j+k; l++){
					degreeSequence.set(l, degreeSequence.get(j));
					//System.out.print(degreeSequence.get(l) + ",");
				}	
				start_index = j;
				j += k;
			}
			else{
				// Merge value and check next vertex
				degreeSequence.set(j, degreeSequence.get(j-1));
				//System.out.print(degreeSequence.get(j) + ",");
				j++;
			}
		}
		
		// To make sure the last values are in a k-group (if a new group is set with less than k values after, the for loop would exit, but those
		// values need to be added to that group)
		for(; j < degreeSequence.size(); j++){
			degreeSequence.set(j, degreeSequence.get(j-1));
			//System.out.print(degreeSequence.get(j) + ",");
		}
		
		//System.out.println();
	}
	
	private int I(int start_index, int end_index){
		int cost = 0;
		for(int i = start_index + 1; i <= end_index; i++){
			cost += degreeSequence.get(start_index) - degreeSequence.get(i);
		}
		return cost;
	}
	
	public DegreeSequence getAnonymizedDegreeSequence(){
		return degreeSequence;
	}
}
