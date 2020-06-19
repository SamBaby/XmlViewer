package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import java.util.*;

public class XmlViewerandEditor {

	protected Shell shell;
	Table table = null;
	TableEditor editor= null;
	Document document = null;
	TreeItem selectedItem;
	Tree tree = null;
	Node selectedNode = null;
	Text text =null;
	IniParser ini = null;
	int selectedProperty ;
	int mode = 0; // 1:xml 2:ini
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
		createContents(display);
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
	protected void createContents(final Display display) {
		shell = new Shell(display);
		shell.setSize(450, 300);
		shell.setText("XML Viewer and Editor");
		FormLayout layout = new FormLayout();
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		shell.setLayout(layout);
		FormData data = new FormData();
		
		ToolBar toolBar = new ToolBar(shell, SWT.FLAT | SWT.RIGHT);
//		toolBar.setBounds(0, 0, 444, 32);
		data = new FormData();
		data.top = new FormAttachment(0, 0);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		toolBar.setLayoutData(data);
		
		ToolItem fileItem = new ToolItem(toolBar, SWT.NONE);
		fileItem.setText("File");
		
		ToolItem saveItem = new ToolItem(toolBar, SWT.NONE);
		
		saveItem.setText("Save");
		
		tree = new Tree(shell, SWT.BORDER);
//		tree.setBounds(0, 38, 213, 223);
		data = new FormData();
		data.top = new FormAttachment(toolBar, 5);
		data.left = new FormAttachment(0, 5);
		data.right = new FormAttachment(45, 0);
		data.bottom = new FormAttachment(100, -5);
		tree.setLayoutData(data);
		
		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
//		table.setBounds(219, 38, 215, 223);
		data = new FormData();
		data.top = new FormAttachment(toolBar, 5);
		data.left = new FormAttachment(tree, 10);
		data.right = new FormAttachment(100, -5);
		data.bottom = new FormAttachment(100, -5);
		table.setLayoutData(data);
		
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
				FileDialog dlg = new FileDialog(shell, SWT.OPEN);
				dlg.setFilterExtensions(new String[] { "*.xml", "*.ini"});
			    String fileName = dlg.open();
			    if(fileName != null) {
			    	if(fileName.matches(".+\\.xml")) {
			    		mode = 1;
			    	}
			    	else if(fileName.matches(".+\\.ini")){
			    		mode = 2;
			    	}
			    	try {
			    		tree.removeAll();
						createTree(fileName);
					} catch (ParserConfigurationException | SAXException | IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			    }
				
			}
		});
		
		//save the file edited
		saveItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				 FileDialog dialog = new FileDialog(shell, SWT.SAVE);
				 if(mode == 1) {
					 dialog.setFilterExtensions(new String[] { "*.xml"});
					 dialog.setFileName("xml.xml");
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
				 else if(mode == 2) {
					 dialog.setFilterExtensions(new String[] { "*.ini"});
					 dialog.setFileName("xml.ini");
					 String filename =dialog.open();
					 try {
				            FileOutputStream outputStream = new FileOutputStream(filename);
				            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-16");
				            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
				             
				            for(int i =0; i< ini.section.size(); i++) {
				            	bufferedWriter.write("["+ini.section.get(i)+"]");
				            	bufferedWriter.newLine();
				            	for(int j=0; j< ini.property.size(); j++) {
				            		if(ini.findSection(Integer.toString(j)) == ini.section.get(i)) {
										bufferedWriter.write(ini.property.get(j)+ " = "+ ini.content.get(j));
										bufferedWriter.newLine();
									}
				            	}
				            }
				             
				            bufferedWriter.close();
				        } catch (IOException e1) {
				            e1.printStackTrace();
				        }
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
					showProperty(selectedItem);
				}
				
			}
		});
		//edit the node attributes and text
		table.addMouseListener(new MouseAdapter() {
		      public void mouseDoubleClick(MouseEvent event) {
		    	  Control old = editor.getEditor();
			        if (old != null)
			          old.dispose();
			        if(text != null) {
			        	text.dispose();
			        }
			        //get row
			        Point pt = new Point(event.x, event.y);

			        final TableItem item = table.getItem(pt);
			        if (item == null) {
			          return;
			        }
			        int index = table.indexOf(item); 
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
			        	text = new Text(table, SWT.CENTER);
				        //text.setForeground(item.getForeground());
				        text.setText(item.getText(column));
				        text.selectAll();
				        text.setFocus();
				        
				        editor.minimumWidth = text.getBounds().width;

				        editor.setEditor(text, item, column);

				        final int col = column;
				        
				        text.addModifyListener(new ModifyListener() {
				          public void modifyText(ModifyEvent event) {
				            item.setText(col, text.getText());
				            if(mode == 1)
				            	editNode(index, text.getText());
				            else if (mode ==2) {
				            	editIniProperty(text.getText());
				            }
				          }
				        });
				        
				        text.addKeyListener(new KeyAdapter() {

				            @Override
				            public void keyReleased(KeyEvent arg0) {
				                // TODO Auto-generated method stub

				            }

				            @Override
				            public void keyPressed(KeyEvent arg0) {
				                if(arg0.keyCode == SWT.CR) {
				                    //do something here....
				                	item.setText(col, text.getText());
						            if(mode == 1)
						            	editNode(index, text.getText());
						            else if (mode ==2) {
						            	editIniProperty(text.getText());
						            }
						            text.dispose();
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
	
	
	public boolean hasTextNode(Node node) {
		if(node.hasChildNodes() && node.getFirstChild().getTextContent().trim().length() >0) {
			return true;
		}
		else
			return false;
	}

	
	public void editNode(int index, String text) {
		if(hasTextNode(selectedNode)) {
        	if(index == 0) {
        		selectedNode.getFirstChild().setTextContent(text);
        	}
        	else {
        		selectedNode.getAttributes().item(index-1).setNodeValue(text);
        	}
        }
        else {
        	selectedNode.getAttributes().item(index).setNodeValue(text);
        }
	}
	
	//specify the file type and create a tree depending on the file type
	public void createTree(String fileName) throws ParserConfigurationException, SAXException, IOException {
		switch (mode){
			case 1:
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setIgnoringElementContentWhitespace(true);
				DocumentBuilder db = dbf.newDocumentBuilder();
				document = db.parse(fileName);
				//create a tree
				Element root = document.getDocumentElement();
				if(root != null) {
					TreeItem item0 = new TreeItem(tree,SWT.NULL);
					item0.setText(root.getNodeName());
					mapping.put(item0,root);
					dfs(root,item0);
				}
				break;
			case 2:
				ini = new IniParser();
	    		FileReader openFile = null;
	    		openFile = new FileReader(fileName);
		        BufferedReader fileInput = new BufferedReader(openFile);
		        String str = null;
		        while((str = fileInput.readLine()) != null) {
		        	if(str.length() > 0) {
		        		ini.parse(str);
		        	}
		        }
		        fileInput.close();
		        
		        for(int i = 0; i < ini.section.size(); i++) {
					TreeItem section = new TreeItem(tree, SWT.NULL);
					section.setText(ini.section.get(i));
					for(int j = 0; j< ini.property.size(); j++) {
						if(ini.findSection(Integer.toString(j)) == ini.section.get(i)) {
							TreeItem property = new TreeItem(section, SWT.NULL);
							property.setText(ini.property.get(j));
						}
					}
				}
		        break;
		}
	}
	
	public void showProperty(TreeItem item){
		switch(mode) {
		case 1:
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
			break;
		case 2:
	        for (int i=0; i<ini.property.size(); i++) {
	        	String property = ini.property.get(i);
	            if(property == item.getText() && ini.findSection(Integer.toString(i))== item.getParentItem().getText()) {
	            	TableItem tItem = new TableItem(table, SWT.NONE);
	            	tItem.setText(new String[] {property, ini.content.get(i)});
	            	selectedProperty = i;
	            }
	        }
	        break;
	     default:
	    	break;
		}
	}
	
	public void editIniProperty(String editText) {
		ini.content.set(selectedProperty, editText);
	}
}
