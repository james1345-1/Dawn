package dawn.swing;

import java.awt.event.*;
import javax.swing.*;

import org.gstreamer.*;

import dawn.*;

public class DawnMenuBar extends JMenuBar implements ActionListener{

	private JMenu dawn;
	private JMenuItem mediaDir;
	
	public DawnMenuBar(){
		dawn = new JMenu("Dawn");
		mediaDir = new JMenuItem("Set Media Folder");
		mediaDir.addActionListener(this);
		dawn.add(mediaDir);
		add(dawn);
	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == mediaDir){
			Dawn.rebuildLibrary();
		}
	}
	
	// Need to make file chooser to select a folder

}