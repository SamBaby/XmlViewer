package main;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Listener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.eclipse.swt.widgets.Menu;

public class xmlViewer {

	protected Shell shell;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			xmlViewer window = new xmlViewer();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");

		Tree tree = new Tree(shell, SWT.BORDER | SWT.V_SCROLL
		        | SWT.H_SCROLL);
		tree.setBounds(0, 48, 193, 223);

		TreeItem item = new TreeItem(tree, SWT.NULL);
	    item.setText("ITEM");
	    
	    TreeItem item2 = new TreeItem(item, SWT.NULL);
	    item2.setText("ITEM2");
	    
	    TreeItem item3 = new TreeItem(item2, SWT.NULL);
	    item3.setText("ITEM3");

	    

	}
}
