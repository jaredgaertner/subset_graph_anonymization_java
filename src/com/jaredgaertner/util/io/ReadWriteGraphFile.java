package com.jaredgaertner.util.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.jaredgaertner.app.Main;
import com.jaredgaertner.util.graph.Graph;

public class ReadWriteGraphFile {
	private String fileName;
	private Graph graph;
	
	public ReadWriteGraphFile(String newFileName){
		fileName = newFileName;
		graph = new Graph();	
	}
	
	public void readGraphFile(){
	    Charset charset = Charset.forName("US-ASCII");
	    BufferedReader reader = null;
	    try {
	    	InputStream is = getClass().getResourceAsStream("/com/jaredgaertner/resources/" + fileName);
	    	reader = new BufferedReader(new InputStreamReader(is));
	    	//reader = Files.newBufferedReader(Paths.get("com/jaredgaertner/resources/" + fileName), charset);
	        String line = null;
	        while ((line = reader.readLine()) != null) {
	            // Ignore comments
	            if(line.startsWith("#") || line.startsWith("%") || line.startsWith("//")){
	            	continue;
	            }
	            // Read in vertices/edges
	            String[] vertices = line.split("\\s+");
	            if( vertices.length != 2 ){
	            	// Error, should be reading in edges in pairs of vertices
	            	System.err.format("%s: invalid edge format.", fileName);
	            }
	            else{
	            	if(!graph.containsVertex(vertices[0])) graph.addVertex(vertices[0]);
	            	if(!graph.containsVertex(vertices[1])) graph.addVertex(vertices[1]);
	            	graph.addEdge(vertices[0],vertices[1]);
	            }
	        }
		} catch (NoSuchFileException x) {
		    System.err.format("%s does not exist\n", x.getFile());
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		} finally {
		    if (reader != null){
				try {
					reader.close();
				} catch (IOException e) {
				    System.err.format("IOException: %s%n", e);
				}
		    }
		}
	}
	
	public void writeGraphFile(){
	    Charset charset = Charset.forName("US-ASCII");
	    String s = "";
	    BufferedWriter writer = null;
	    try {
	    	writer = Files.newBufferedWriter(Paths.get("com/jaredgaertner/resources/" + fileName), charset);
	        writer.write(s, 0, s.length());
	    } catch (NoSuchFileException x) {
		    System.err.format("%s does not exist\n", x.getFile());
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		} finally {
		    if (writer != null){
				try {
					writer.close();
				} catch (IOException e) {
				    System.err.format("IOException: %s%n", e);
				}
		    }
		}		
		
	}
	
	public Graph getGraph(){
		return graph;
	}
}
