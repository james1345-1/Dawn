package dawn.library;

import java.nio.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import static java.nio.file.FileVisitResult.*;
import java.nio.charset.*;

import javax.swing.*; // So we can modify the table

import dawn.*;

import org.gstreamer.*;
import org.gstreamer.elements.*;

/**
 * A class to handle the Library's Directory tree walk.
 * 
 * Has its own instance of PlayBin2 to set files to. implements Bus.TAG to handle tag events
 */
public class LibraryFileVisitor extends SimpleFileVisitor<Path> implements Bus.TAG{
	
	// Create tag scanning playbin
	private final PlayBin2 playbin = new PlayBin2("LibraryFileVisitor");
	
	// Initialize utility variables
	private Path currentPath = null;
	private boolean currentPathAdded = false; // Used to make sure each file is only added once
	
	public LibraryFileVisitor(){
		playbin.setVideoSink(ElementFactory.make("fakesink", "video-sink"));
		playbin.setAudioSink(ElementFactory.make("fakesink", "audio-sink"));
		playbin.getBus().connect(this);
	}
	
	/**
	 * On file visit, add file to playbin, add file to library, set playbin to catch 
	 */
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
		try{
			if(Files.probeContentType(file).startsWith("audio")){
				currentPath = file;
				synchronized(currentPath){ // Synchronize so cannot start next file until previous file is completed
					currentPathAdded = false;
					playbin.setInputFile(file.toFile());
					playbin.setState(State.PAUSED);
					playbin.setState(State.NULL); // This is quite an important line it turns out :P
				}
			}
		}catch(Exception e){
			// EXCEPTION, EXCEPTION, EXCEPTION
		}
		// Move on to next file
		return CONTINUE;
	}
	
	
	/** Action to take on tag events */
	public void tagsFound(GstObject source, TagList tagList) {
		if(!currentPathAdded){
			Dawn.library.add(new dawn.lowLevel.Track(currentPath.toFile(), tagList));
		}
		currentPathAdded = true;
	}
}
