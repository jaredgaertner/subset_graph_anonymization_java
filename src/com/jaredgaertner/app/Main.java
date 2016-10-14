/**
 * 
 */
package com.jaredgaertner.app;

import java.awt.Dimension;

import javax.swing.JApplet;
import javax.swing.JFrame;
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

	public static void main(String[] args) {
        GUI gui = new GUI();
        Main applet = new Main();
        applet.setContentPane(gui);
        applet.setSize(new Dimension(600,800));

		// create and set up the applet

		applet.setSize(new Dimension(600, 800));
		applet.init();

		// create a frame to host the applet, which is just another type of Swing Component
		JFrame mainFrame = new JFrame();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// add the applet to the frame and show it
		mainFrame.getContentPane().add(applet);
		mainFrame.pack();
		mainFrame.setVisible(true);

		// start the applet
		applet.start();
	}

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