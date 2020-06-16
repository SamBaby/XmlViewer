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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.custom.TableEditor;
import java.util.HashMap;
import java.io.OutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DocumentType;

public class XmlViewerandEditor {

	protected Shell shell;
	Table table;
	TableEditor editor;
	Document document = null;
	TreeItem selectedItem;
	Tree tree;
	Node selectedNode = null;
	private HashMap<TreeItem, Node> mapping = new HashMap<TreeItem, Node> ();
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			XmlViewerandEditor window = new XmlViewerandEditor();
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
		
		ToolBar toolBar = new ToolBar(shell, SWT.FLAT | SWT.RIGHT);
		toolBar.setBounds(0, 0, 444, 32);
		
		ToolItem fileItem = new ToolItem(toolBar, SWT.NONE);
		fileItem.setText("File");
		
		ToolItem saveItem = new ToolItem(toolBar, SWT.NONE);
		
		saveItem.setText("Save");
		
		tree = new Tree(shell, SWT.BORDER);
		tree.setBounds(0, 38, 213, 223);
		//TreeItem selectedItem = null;
		
		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(219, 38, 215, 223);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableColumn propertyColumn,valueColumn;
		
		propertyColumn = new TableColumn(table, SWT.CENTER);
		propertyColumn.setText("Porperty");
		propertyColumn.setWidth(80);
        
		valueColumn = new TableColumn(table, SWT.CENTER);
		valueColumn.setText("Value");
		valueColumn.setWidth(80);
		
		final TableEditor editor = new TableEditor(table);
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal = true;
        
		//open a xml file and map to the tree
		fileItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setIgnoringElementContentWhitespace(true);
				try{
				DocumentBuilder db = dbf.newDocumentBuilder();
				FileDialog dlg = new FileDialog(shell, SWT.OPEN);
				dlg.setFilterExtensions(new String[] { "*.xml"});
			    String fileName = dlg.open();
			    if(fileName != null) {
			    	document = db.parse(fileName);
					tree.removeAll(); //clear all the original tree nodes
					//create a tree
					Element root = document.getDocumentElement();
					if(root != null) {
						
						TreeItem item0 = new TreeItem(tree,SWT.NULL);
						item0.setText(root.getNodeName());
						mapping.put(item0,root);
						dfs(root,item0);
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
		//save the file edited
		saveItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				 FileDialog dialog = new FileDialog(shell, SWT.SAVE);
				 dialog.setFilterExtensions(new String[] { "*.xml"});
				 dialog.setFilterPath("c:\\"); dialog.setFileName("xml.xml");
				 String filename =dialog.open();
				 
			    
			    if(document != null) {
			    	try {
				        Transformer t = TransformerFactory.newInstance().newTransformer();
				        DocumentType dt = document.getDoctype();
				        if (dt != null) {
				          String pub = dt.getPublicId();
				          if (pub != null) {
				            t.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, pub);
				          }
				          t.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, dt.getSystemId());
				        }
				        t.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); // NOI18N
				        t.setOutputProperty(OutputKeys.INDENT, "yes"); // NOI18N
				        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); // NOI18N
				        Source source = new DOMSource(document);
				        Result result = new StreamResult(filename);
				        t.transform(source, result);
				      } catch (Exception ee) {
				        ee.printStackTrace();
				      } catch (TransformerFactoryConfigurationError ee) {
				    	  ee.printStackTrace();
				      }
			    }
			    else {
			    	System.out.println("not reading a xml now!");
			    }
			    
			}
		});
		
		//display the selected node by double-clicking
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				Point pt = new Point(e.x, e.y);	
				selectedItem = tree.getItem(pt);
				
				if(selectedItem != null) {
					table.removeAll();
					selectedNode = mapping.get(selectedItem);
					//node has text, show text
					if(selectedNode.hasChildNodes() && selectedNode.getFirstChild().getNodeType() == Node.TEXT_NODE) {
						if(selectedNode.getFirstChild().getTextContent().trim().length() > 0) {
							TableItem tItem = new TableItem(table, SWT.NONE);
							tItem.setText(new String[] {"text", selectedNode.getFirstChild().getTextContent().trim()});
						}
						
					}
					//node has attributes
					int attrlen = selectedNode.getAttributes().getLength();
					if(attrlen > 0) {
						for(int i =0; i< attrlen; i++) {
							TableItem tItem = new TableItem(table, SWT.NONE);
							tItem.setText(new String[] {selectedNode.getAttributes().item(i).getNodeName(),
									selectedNode.getAttributes().item(i).getNodeValue()});
						}
					}
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
			        int index = table.indexOf(item); //
			        //shell.setText(Integer.toString(table.indexOf(item)));
			        // get column
			        int column = -1;
			        for (int i = 0, n = table.getColumnCount(); i < n; i++) {
			          Rectangle rect = item.getBounds(i);
			          if (rect.contains(pt)) {
			            column = i;
			            break;
			          }
			        }
			        
			        if(column > 0) {
			        	final Text text = new Text(table, SWT.CENTER);
				        text.setForeground(item.getForeground());
				        //text.setBounds(item.getTextBounds(column));
				        text.setText(item.getText(column));
				        text.setForeground(item.getForeground());
				        text.selectAll();
				        //text.setFocus();
				        
				        editor.minimumWidth = text.getBounds().width;

				        editor.setEditor(text, item, column);

				        final int col = column;
				        
				        text.addModifyListener(new ModifyListener() {
				          public void modifyText(ModifyEvent event) {
				            item.setText(col, text.getText());
				            
				            if(ifTextNode(selectedNode)) {
				            	if(index == 0) {
				            		selectedNode.getFirstChild().setTextContent(text.getText());
				            	}
				            	else {
				            		selectedNode.getAttributes().item(index-1).setNodeValue(text.getText());
				            	}
				            }
				            else {
				            	selectedNode.getAttributes().item(index).setNodeValue(text.getText());
				            }
				          }
				        });
			        }
		    	  
		      }
		    });
		

	}
	
	//DFS a xml file
	public void dfs(Node node,TreeItem item0) {
		if(node == null)
		{
			return;
		}
		
		for(int i = 0; i <node.getChildNodes().getLength(); i++) {
			if(node.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE)
			{
				TreeItem item1 = new TreeItem(item0,SWT.NULL);
				item1.setText(node.getChildNodes().item(i).getNodeName());
				mapping.put(item1,node.getChildNodes().item(i));
				dfs(node.getChildNodes().item(i),item1);
			}
		}
		
	}
	
	//transform the tree into a xml
	public void createXml(Tree tr) {
		
	}
	
	public boolean ifTextNode(Node node) {
		if(node.hasChildNodes() && node.getFirstChild().getTextContent().trim().length() >0) {
			return true;
		}
		else
			return false;
	}

}
