package main;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
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
import main.Employees;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Control;

public class XMLtree {

	protected Shell shell;
	Employees[] employee;
	Table table;
	TableEditor editor;
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			XMLtree window = new XMLtree();
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
		shell.setText("XML Viewer/Editor");
		ToolBar toolBar = new ToolBar(shell, SWT.FLAT | SWT.RIGHT);
		toolBar.setBounds(0, 0, 444, 32);
		
		ToolItem FileItem = new ToolItem(toolBar, SWT.NONE);
		FileItem.setText("File");
		Tree tree = new Tree(shell, SWT.BORDER);
		tree.setBounds(0, 38, 216, 223);
		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(222, 38, 212, 223);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableColumn idColumn,firstnameColumn, lastnameColumn, ageColumn, salaryColumn;
		
        idColumn = new TableColumn(table, SWT.CENTER);
        idColumn.setText("ID");
        idColumn.setWidth(40);
        
        firstnameColumn = new TableColumn(table, SWT.CENTER);
        firstnameColumn.setText("First");
        firstnameColumn.setWidth(40);
        
        lastnameColumn = new TableColumn(table, SWT.CENTER);
        lastnameColumn.setText("Last");
        lastnameColumn.setWidth(40);
        
        ageColumn = new TableColumn(table, SWT.CENTER);
        ageColumn.setText("Age");
        ageColumn.setWidth(40);
        
        salaryColumn = new TableColumn(table, SWT.CENTER);
        salaryColumn.setText("Salary");
        salaryColumn.setWidth(40);
        
        final TableEditor editor = new TableEditor(table);
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal = true;
		/**********Read the xml file*********/
		FileItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				try{
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document document = db.parse("src/Employees.xml");

				
				Element EmployeeTree = document.getDocumentElement();
				
				NodeList rootNode = EmployeeTree.getChildNodes();
				
				TreeItem item0 = new TreeItem(tree,0);
				item0.setText(EmployeeTree.getNodeName());
				
				int empNum = 0;//number of employees
				int empWhich = -1;
				for (int i = 0; i< rootNode.getLength(); i++) {
					if(rootNode.item(i).getNodeName() != "#text") {
						empNum++;
					}
				}
				if(empNum > 0)
				{
					employee = new Employees[empNum];
				}
				for(int i=0; i< rootNode.getLength() ; i++)
				{
					if(rootNode.item(i).getNodeName() != "#text") {
						empWhich++;
						TreeItem item1 = new TreeItem(item0, SWT.NULL);
						item1.setText(rootNode.item(i).getNodeName());
						
						NodeList person = rootNode.item(i).getChildNodes();
						
						employee[empWhich] = new Employees();
						employee[empWhich].setID(rootNode.item(i).getAttributes().item(0).getNodeValue());
						
						for(int j=0; j< person.getLength(); j++) {
							if(person.item(j).getNodeName() != "#text") {
								TreeItem item2= new TreeItem(item1, SWT.NULL);
								item2.setText(person.item(j).getNodeName());
							
								switch(person.item(j).getNodeName()) {
									case "Firstname":
										employee[empWhich].setFirstname(document.getElementsByTagName("Firstname").item(empWhich).getFirstChild().getNodeValue());
									case "Lastname":
										employee[empWhich].setLastname(document.getElementsByTagName(person.item(j).getNodeName()).item(empWhich).getFirstChild().getNodeValue());
									case "Age":
										employee[empWhich].setAge(document.getElementsByTagName(person.item(j).getNodeName()).item(empWhich).getFirstChild().getNodeValue());
									case "Salary":
										employee[empWhich].setSalary(document.getElementsByTagName(person.item(j).getNodeName()).item(empWhich).getFirstChild().getNodeValue());
									default:
										break;
										
								}
							
							}
						}
						TableItem tItem = new TableItem(table, SWT.NONE);
						tItem.setText(new String[] {employee[empWhich].getID(), employee[empWhich].getFirstname(),
								employee[empWhich].getLastname(), employee[empWhich].getAge(),employee[empWhich].getSalary()});
					
							
					}
					
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
		
		table.addMouseListener(new MouseAdapter() {
		      public void mouseDown(MouseEvent event) {
		        Control old = editor.getEditor();
		        if (old != null)
		          old.dispose();
		        //get row
		        Point pt = new Point(event.x, event.y);

		        final TableItem item = table.getItem(pt);
		        if (item == null) {
		          return;
		        }
		        // get column
		        int column = -1;
		        for (int i = 0, n = table.getColumnCount(); i < n; i++) {
		          Rectangle rect = item.getBounds(i);
		          if (rect.contains(pt)) {
		            column = i;
		            break;
		          }
		        }
		        
		        final Text text = new Text(table, SWT.NONE);
		        text.setForeground(item.getForeground());

		        text.setText(item.getText(column));
		        text.setForeground(item.getForeground());
		        text.selectAll();
		        text.setFocus();

		        editor.minimumWidth = text.getBounds().width;

		        editor.setEditor(text, item, column);

		        final int col = column;
		        text.addModifyListener(new ModifyListener() {
		          public void modifyText(ModifyEvent event) {
		            item.setText(col, text.getText());
		            
		          }
		        });
		      }
		    });
		
		
		
	    

	}
}
