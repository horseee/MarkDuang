
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class MarkDuang {
	 public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setSize(800, 800);
		frame.setLocation(30, 50);
		frame.setTitle("unknown");
		FileInf NowFile = new FileInf("unknown", "null");
		
		StyleContext sc = new StyleContext();
	    final DefaultStyledDocument doc = new DefaultStyledDocument(sc);
	    JTextPane editor = new JTextPane(doc);
	    final Style DefaultStyle = sc.addStyle("Heading2", null);
	    StyleConstants.setForeground(DefaultStyle, Color.white);
	    StyleConstants.setFontSize(DefaultStyle, 16);
	    StyleConstants.setFontFamily(DefaultStyle, "Monospace");
	    StyleConstants.setBold(DefaultStyle, false);
	    StyleConstants.setLeftIndent(DefaultStyle, 20);
	    StyleConstants.setSpaceAbove(DefaultStyle, 6);
	    StyleConstants.setSpaceBelow(DefaultStyle, 0);
	    doc.setParagraphAttributes(0, 1, DefaultStyle, false);
	    
		JScrollPane jscroll = new JScrollPane(editor);
		jscroll.isOpaque();
		jscroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
		jscroll.setBorder(BorderFactory.createEmptyBorder());
		jscroll.getViewport().setBackground(new Color(40,40,40));
		
		DefaultCaret caret = (DefaultCaret)editor.getCaret();
		
		editor.setBackground(new Color(40,40,40));
		editor.getDocument().addDocumentListener(new SyntaxHighlighter(editor, frame, NowFile));
		
		frame.getContentPane().add(jscroll);
	
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		frame.setUndecorated(false);
		frame.setVisible(true);
	    
	    editor.addKeyListener(new KeyListener() {
	    		boolean KeyS = false;
	    		boolean KeyO = false;
	    		boolean KeyP = false;
	    		boolean KeyCtrl = false;
	    		
	    		FileDialog d1 = new FileDialog(frame, "Open File", FileDialog.LOAD);  
	    		FileDialog d2 = new FileDialog(frame, "Save File", FileDialog.SAVE); 
	    		
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				int keyCode=e.getKeyCode();  //获取按的键盘上的健  
				System.out.println(keyCode);
				if (keyCode == KeyEvent.VK_S )
					KeyS = true;
				if (keyCode == KeyEvent.VK_O)
					KeyO = true;
				if (keyCode == KeyEvent.VK_P)
					KeyP = true;
				if (keyCode == KeyEvent.VK_CONTROL || keyCode == 0)
					KeyCtrl = true;
				
				if (KeyCtrl && KeyS)  {
					String Dir = NowFile.GetFileDir();
					String FileName = NowFile.GetFileName();
					if (FileName.equals("unknown")) {
						d2.setVisible(true);  
						Dir = d2.getDirectory();
						FileName = d2.getFile();
					}
					
		            System.out.println( Dir + FileName );  
		            KeyCtrl =false;
		            KeyS = false;
		            d2.setVisible(false);
		            
		            
		            if (Dir!=null && FileName!=null) {
			            	Document docs = editor.getDocument();
			            	File file = new File(Dir + FileName);
			            	NowFile.SetFileDir(Dir);
			            	NowFile.SetFileName(FileName);
			            	frame.setTitle(FileName);
			    
				        try {
				        		FileWriter fw = new FileWriter(file);
							System.out.println(docs.getText(0, docs.getLength()-1));
							fw.write(docs.getText(0, docs.getLength()-1));
							fw.close();
							frame.setTitle(NowFile.GetFileName());
				        } catch (BadLocationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (Exception rede) {
							rede.printStackTrace();
						}
		            }
				}
				
				// 保存pdf
				if (KeyCtrl && KeyP)  {
					String MDDir = NowFile.GetFileDir();
					String FileName = NowFile.GetFileName();
					String PDFName ;
					
					d2.setVisible(true);  
					String PDFDir = d2.getDirectory();
					PDFName = d2.getFile();
					d2.setVisible(false);
					
					if (FileName.equals("unknown")) {
						int pos = PDFName.indexOf(".pdf");
						FileName = PDFName.substring(0, pos) + ".md";
						MDDir = PDFDir;
					} 
					
		            System.out.println(PDFDir + FileName);  
		            KeyCtrl =false;
		            KeyS = false;
		            
		            
		            if (PDFDir!=null && PDFName!=null) {
		            
			            	Document docs = editor.getDocument();
			            	File file = new File(MDDir + FileName);
			            	frame.setTitle(FileName);
			    
				        try {
				        		FileWriter fw = new FileWriter(file);
							System.out.println(docs.getText(0, docs.getLength()-1));
							fw.write(docs.getText(0, docs.getLength()-1));
							fw.close();
				        } catch (BadLocationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (Exception rede) {
							rede.printStackTrace();
						}
				        
				        MDToPDF NewPDF = new MDToPDF();
			            NewPDF.OpenNewFile(MDDir,PDFDir, PDFName, FileName);
		            }        
				}
				
				if (KeyCtrl && KeyO)  {
					d1.setVisible(true);  
		            System.out.println(d1.getDirectory() + d1.getFile());  
		            KeyCtrl = false;
		            KeyO = false;
		            d1.setVisible(false);
		            
		            if (d1.getDirectory() != null || d1.getFile() != null) {
		            		String FileName = d1.getDirectory() + d1.getFile();
		            		File file = new File(FileName); 		
		            		Document docs = editor.getDocument();//获得文本对象
		            		NowFile.SetFileName(d1.getFile());
		            		NowFile.SetFileDir(d1.getDirectory());
		       
			            try {
			            		docs.remove(0,  docs.getLength()-1);
			            		BufferedReader in = new BufferedReader(new FileReader(file));
			            		String tempbyte;
			            		while ((tempbyte = in.readLine())!=null) {
			            			docs.insertString(docs.getLength(),tempbyte , null);
			            			docs.insertString(docs.getLength(),"\n", null);
			            		}
			            		NowFile.SetModified(false);
			            		frame.setTitle(d1.getFile());
			            	
			            			
			            } catch (BadLocationException loce) {
			                loce.printStackTrace();
			            } catch (Exception rede) {
			            		rede.printStackTrace();
			            }
		            }
		            
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				int keyCode=e.getKeyCode();  //获取按的键盘上的健  
				System.out.println(keyCode);
				if (keyCode == KeyEvent.VK_S)
					KeyS = false;
				if (keyCode == KeyEvent.VK_P)
					KeyP = false;
				if (keyCode == KeyEvent.VK_O)
					KeyO = false;
				if (keyCode == KeyEvent.VK_CONTROL)
					KeyCtrl = false;
			}  
	    	
	    });
	   

	}
}

