/**
 * 
 */
package com.jaredgaertner.app;

import java.awt.Dimension;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

import com.jaredgaertner.gui.*;

/**
 *
 */
public class Main extends JApplet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @see java.applet.Applet#init().
	 */
    //Called when this applet is loaded into the browser.
    public void init() {
        //Execute a job on the event-dispatching thread; creating this applet's GUI.
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    createGUI();
                }
            });
        } catch (Exception e) { 
            System.err.println("createGUI didn't complete successfully.");
        }
    }
	
	private void createGUI(){
		GUI gui = new GUI();
		setContentPane(gui);
		this.setSize(new Dimension(600,800));
	}
}