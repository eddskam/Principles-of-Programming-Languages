import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;



public class JackCompiler {

    public static void main(String[] args) {
	if (args.length != 0) {  // Assume file/folder name passed on command line.
	    new JackCompiler().compile(args[0]);
	    return;
	}

	// Pop-up a JFileChooser.
	JFileChooser fc = new JFileChooser();

	try {
	    SwingUtilities.invokeAndWait(new Runnable() {
		public void run() {
		    UIManager.put("swing.boldMetal", Boolean.FALSE);
		    fc.setFileFilter(new FileNameExtensionFilter("Jack files", "jack"));
		    fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		    if (fc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
			System.out.println("No file selected; terminating.");
			System.exit(1);
		    }
		}
	    });
	} catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}

	new JackCompiler().compile(fc.getSelectedFile().getPath());
    }

    private void compile(String path) {
	String basename;
	File folder = new File(path);
	ArrayList<File> files;
	XMLWriter xmlWriter;

	if (folder.isFile()) {   // Folder is actually a file; translating a single
	    // Jack file.
	    files = new ArrayList<File>();
	    files.add(folder);
	    basename = folder.getPath(); 
	    basename = basename.substring(0,basename.lastIndexOf('/'));
	}
	else {   // folder is a folder; translating a folder of Jack files.
	    files = new ArrayList<File>(Arrays.asList(folder.listFiles()));
	    basename = folder.getPath();
	}

	

	// Iterate through all the Jack files, or file, that we're compiling.

	for (File file : files) {

	    if (!file.getName().endsWith(".jack"))   // Skip non-jack files.
		continue;

	    String filename = file.getName(); 
	    filename = filename.substring(0, filename.indexOf('.'));
	    
	    CompilationEngine compiler = new CompilationEngine(basename+"/"+filename,file);

	    compiler.compileClass();
	    compiler.close();
	}

	
    }
}
