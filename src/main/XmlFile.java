package main;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Tree;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import org.eclipse.swt.widgets.Text;

public class XmlFile {

	protected Shell shell;
	private Text text;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			XmlFile window = new XmlFile();
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
		shell.setText("Xml Viewer");
		
		ToolBar toolBar = new ToolBar(shell, SWT.FLAT | SWT.RIGHT);
		toolBar.setBounds(0, 0, 444, 26);
		
		ToolItem FileItem = new ToolItem(toolBar, SWT.NONE);
		
		text = new Text(shell, SWT.BORDER);
		text.setBounds(10, 32, 424, 229);
		
		FileItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				try{
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document document = db.parse("src/Employees.xml");
				NodeList Firstnamelist = document.getElementsByTagName("Firstname");
				for (int i = 0; i < Firstnamelist.getLength(); i++) {
					text.setText(text.getText()+document.getElementsByTagName("Firstname").item(i)
									.getFirstChild().getNodeValue());
					text.setText(text.getText()+" "+document.getElementsByTagName("Lastname").item(i)
									.getFirstChild().getNodeValue()+"\n");
				}
				
				}catch (ParserConfigurationException ee){
				ee.printStackTrace();
				}catch (IOException ee){
				ee.printStackTrace();
				}catch (SAXException ee){
				ee.printStackTrace();
				}
				
			}
		});
		FileItem.setText("File");
		
		ToolItem SaveItem = new ToolItem(toolBar, SWT.NONE);
		SaveItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		SaveItem.setText("Save");
		


	}
}
