package dawn;

import javax.swing.AbstractListModel;
import java.util.Vector;


import org.gstreamer.*;
import org.gstreamer.elements.*;

// TODO multiple add/remove

public class PlayQueue extends AbstractListModel<Track> implements Bus.EOS{
	
	// Data list
	private Vector<Track> data = new Vector<Track>();
	
	// Playbin
	private PlayBin2 playbin = new PlayBin2("Dawn");
	private int currentIndex = -1;
	
	public PlayQueue(){
		
		// End Of Stream
		playbin.getBus().connect(this);
		
		// Error loggin
		playbin.getBus().connect(new Bus.ERROR(){
			public void errorMessage(GstObject source, int code, String message) {
				System.err.printf("Error Code %d : %s", code, message);
			}
		});
		
		// Warn logging
		playbin.getBus().connect(new Bus.WARNING(){
			public void warningMessage(GstObject source, int code, String message) {
				System.err.printf("Error Code %d : %s", code, message);
			}
		});
		 // Prevent any sort of video window from being created
        playbin.setVideoSink(ElementFactory.make("fakesink", "videosink"));
	}
	
	// Vector methods

	/** Add a track onto the end of the list */
	public void add(Track track){
		data.add(track);
		int insertIndex = data.size() - 1;
		if(0 == insertIndex) { // If this is the first item to be added
			playbin.setInputFile(track.file);
			currentIndex = 0;
		}
		fireIntervalAdded(this, insertIndex, insertIndex);
	}
	
	/** Insert a track at the given index */
	public void add(int index, Track track){
		data.add(index, track);
		int insertIndex = index;
		fireIntervalAdded(this, insertIndex, insertIndex);
	}
	
	/** remove a track from the given index */
	public void remove(int index){
		data.remove(index);
		fireIntervalRemoved(this, index, index);
	}
	
	/** empty the queue */
	public void clear(){
		int index1 = data.size() - 1;
		data = new Vector<Track>(); // clears queue, as there are no external references to data
		fireIntervalRemoved(this, 0, index1);
	}

	// Playbin methods
	/** Play the selected file */
	public void play(){
		playbin.setState(State.PLAYING);
	}
	
	public void pause(){
		playbin.setState(State.PAUSED);
	}
	
	public void stop(){
		playbin.setState(State.NULL);
	}
	
	public void next(){
		playbin.setState(State.NULL);
		currentIndex++;
		if(currentIndex == data.size()){ // If the last song in the queue is playing
			// Do nothing
		} else {
			playbin.setInputFile(data.get(currentIndex).file);
			playbin.setState(State.PLAYING);
		}
	}
	
	public void previous(){
		playbin.setState(State.NULL);
		currentIndex--;
		if(currentIndex < 0){ // If the first song in the queue is playing, restart it
			currentIndex = 0;
		}
		playbin.setInputFile(data.get(currentIndex).file);
		playbin.setState(State.PLAYING);
	}

	public Bus getBus(){
		return playbin.getBus();
	}
	
	public State getState(){
		return playbin.getState();
	}
	
	public void setIndex(int index){
		currentIndex = index;
		playbin.setInputFile(data.get(currentIndex).file);
	}
	
	// Overrides
	// List model implemented as internal vector	
	@Override
	public int getSize(){
		return data.size();
	}
	
	@Override
	public Track getElementAt(int index){
		return data.get(index);
	}
	
	// End of stream handling
	public void endOfStream(GstObject source) {
		next();
	}

}