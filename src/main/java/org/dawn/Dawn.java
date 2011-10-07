package org.dawn;

// Import statements
// Java utils
import java.util.ArrayList;
import java.nio.*;
import java.nio.file.*;
import java.nio.charset.*;
import javax.swing.*;

// Gstreamer classes
import org.gstreamer.*;

// Dawn classes
import org.dawn.lowLevel.*;
import org.dawn.swing.*;

/**
 * The main class of Dawn.
 * 
 * Use of GStreamer leaves a very procedural manner necessary. It also requires the use of at least
 * one global component (The playbin has to be accessible from all parts of the application). Currently,
 * the playbin and library are accessible globally.
 */
public class Dawn{
	
	
	// Main method (and associated constructor)
		
	public static void main(String[] args){	
		new Dawn(args);
	}
	
	/**
	 * Creates a new instance of Dawn.
	 * 
	 * This class performs all of the initialization required.
	 * It sets up the GStreamer interface, loads config from rc file and creates a main window.
	 * Currently, the database of tracks is rebuilt every startup - may take a while.
	 */
	private Dawn(String[] args){
		
		// Initialize the gstreamer framework, and let it interpret any command line flags it is interested in.
        args = Gst.init("Dawn", args);
        
        // Load images
        ImageCache.init();
        
        // Initialize the playbin
        PlayQueue.init();
        
        // Load configuration from file
        loadConfig();
        
        // Create Dawn Window
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				DawnWindow.init();
			}
		});
        
	}
	
	/** Load the config from the config file */
	// currently there is no config to actually load :P
	private void loadConfig(){
		
		// Open config file, stored in ~/.dawn/dawnrc
		Path config = Paths.get(System.getProperty("user.home"), ".dawn", "dawnrc");
		Charset charset = Charset.forName("US-ASCII");
		
		// Read Music Library path. This is the first line of the config file atm.
		try{
			//library.setPath(Paths.get(Files.readAllLines(config, charset).get(0)));
		} catch (Exception e){
			//Todo error handling
		}
		
	}
}