import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.itextpdf.awt.geom.AffineTransform;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPTableEvent;
import com.itextpdf.text.pdf.PdfPatternPainter;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.PdfImageObject;

public class AnalyseContainer {

    Document document;
    Stack GlobalST;
    int LineBlank;
    int isHref;
    int AsteriskCount;
    int TableColumn;
    int TableLineCount;
    int[] column_length;
    int TableTotalLength;
    boolean isCode;
    boolean isQuote;
    boolean isBlockCode;
    boolean isBlockCodeEnd;
    boolean isTable; 
    StringBuffer buffer;
    PdfPTable quoteTable;
    
    String filedir;
    
    
    AnalyseContainer(Document d, String fd) throws DocumentException, IOException {
    		document = d;  
		document.open();
		GlobalST = new Stack();
		LineBlank = 0;
		isHref = 0;
		isCode = false;
		isQuote = false;
		isBlockCode = false;
		isBlockCodeEnd = false;
		isTable = false;
		filedir = fd;
    }
    
    public int WordAnalyse(String line, String nextLine, int[] length) throws DocumentException, IOException {
    		BaseFont bfChinese = BaseFont.createFont("bin/MSYH.TTF",BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED); 
    		Font FontChinese_1 = new Font(bfChinese, 24, Font.BOLD); 
    	    Font FontChinese_2 = new Font(bfChinese, 18, Font.BOLD); 
    	    Font FontChinese_3 = new Font(bfChinese, 14,	Font.BOLD); 
    	    Font FontChinese_4 = new Font(bfChinese, 12,	Font.BOLD); 
    	    Font FontChinese_5 = new Font(bfChinese, 10,	Font.BOLD); 
    	    Font FontChinese_default = new Font(bfChinese, 10, Font.NORMAL); 
    	    PdfPTable  normalTable = null;
    	    
    	    isHref = 0;
    	    AsteriskCount = 0;
		Stack st = new Stack();
		Stack stNumber = new Stack();
		List<Chunk> word = new ArrayList<Chunk>();
		
		StringBuffer WordBuffer = new StringBuffer();
		Font temp = FontChinese_default;
		Paragraph par = new Paragraph();
		par.setSpacingAfter(1.0f);
		par.setSpacingBefore(1.0f);
		
		boolean isAddLine = false;
		
		char[] str = line.toCharArray();
		int[] deleteWord = new int[str.length];
		if (str.length == 0) {
			if (isTable) {
				document.add(new Paragraph(" ", FontChinese_default));
				isTable = false;
			}
			return 0;
		}
		
		int index = 0;
		int offset = 0;
		
		while (str[index] == ' '  && !isBlockCode) {
			index ++;
			offset ++;
			if (index >= str.length) return 0;
		}//读掉所有空格
		int blank = index;
		if (blank>=4 && str[index] != '-' && str[index]!= '*' && str[index]!='+') {
			if (isCode == false) {
				isCode = true;
				buffer = new StringBuffer(line.substring(4));
				buffer.append("\n");
			} else {
				buffer.append(line.substring(4));
				buffer.append("\n");
			}
			return 0;
		}
		if (str[index] == '`' && str[index+1] == '`' && str[index+2] == '`') {
			if (!isBlockCode) {
				buffer = new StringBuffer();
				isBlockCode = true;
				return 0;
			} else {
				isBlockCode = !isBlockCode;
				isBlockCodeEnd = true;
			}
		}
		
		if (isBlockCode) {
			buffer.append(line);
			buffer.append("\n");
			return 0;
		}
		
		if ((blank < 4 && isCode) || isBlockCodeEnd){
			BaseFont codeBase = BaseFont.createFont("bin/SourceCodePro-Regular.ttf",BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED); 
    			Font FontCode = new Font(codeBase, 8, Font.BOLD); 
			String code = String.valueOf(buffer);
			int pos = 0;
			int nextPos = code.indexOf("\n", 0);
			while (nextPos >= 0) {
				nextPos =  code.indexOf("\n", pos);
				PdfPTable codeBlock = new PdfPTable(1);   
				codeBlock.setWidthPercentage(100f);  
				int isTail = code.indexOf("\n", nextPos+1);
				
				codeBlock.getDefaultCell().setBorderWidthBottom(0);
				codeBlock.getDefaultCell().setBorderWidthTop(0);
				codeBlock.getDefaultCell().setBorderWidthLeft(0);
				codeBlock.getDefaultCell().setBorderWidthRight(0);
				
				if (pos == 0 && isTail < 0) {
					codeBlock.setSpacingBefore(10.0f);
					codeBlock.setSpacingAfter(10.0f);
					codeBlock.getDefaultCell().setCellEvent(new SpecialWholeRoundedCell());
					codeBlock.getDefaultCell().setPaddingTop(5.0f);
					codeBlock.getDefaultCell().setPaddingBottom(8.0f);
				}
				else if (pos == 0 && isTail >= 0)  {
					codeBlock.setSpacingBefore(10.0f);
					codeBlock.getDefaultCell().setCellEvent(new SpecialHeaderRoundedCell());
					codeBlock.getDefaultCell().setPaddingTop(5.0f);
				} else if (isTail < 0) {
					codeBlock.getDefaultCell().setCellEvent(new SpecialTailRoundedCell());
					codeBlock.setSpacingAfter(10.0f);
					codeBlock.getDefaultCell().setPaddingBottom(8.0f);
				}//最后一行加底边
				else {
					codeBlock.getDefaultCell().setCellEvent(new SpecialMiddleRoundedCell());
					codeBlock.setSpacingBefore(0);
					codeBlock.getDefaultCell().setPaddingTop(3.0f);
					codeBlock.getDefaultCell().setPaddingBottom(3.0f);
				}
				codeBlock.getDefaultCell().setPaddingLeft(8.0f);
				codeBlock.getDefaultCell().setBackgroundColor(new BaseColor(250, 250, 250));
				
				Phrase p;
				if (nextPos >= 0) {
					p= new Phrase(code.substring(pos, nextPos),FontCode);
					codeBlock.addCell(p);   
				}
				document.add(codeBlock);
				pos = nextPos+1;
			}
			isCode = false;
			if 	(isBlockCodeEnd) {
				isBlockCodeEnd = false;
				return 0;
			}
		}//处理代码块
		
		while (str[index] == '#') {
			st.add(str[index]);
			index ++;
			offset ++;
		}//确认大标题
		
		boolean isTableHead = false;
		if (st.size() == 0 && isTable == false) {
			int number1 = FindSpecialChar('|', line, 0);
			int number2 = FindSpecialChar('|', nextLine, 1);
			//System.out.println(number1 + "         " + number2);
			if (number1 == number2 && number1 != 0) {
				isTable = true;
				TableColumn = number1-1;
				normalTable = new PdfPTable(TableColumn); 
				TableTotalLength = 0;
				for (int i=0; i<length.length; i++) {
					TableTotalLength += length[i];
				}
				normalTable.setWidthPercentage((float) (TableTotalLength * 1.5));
				normalTable.setWidths(length);
				normalTable.setHorizontalAlignment(Element.ALIGN_LEFT);
				normalTable.setSpacingBefore(10);
				normalTable.getDefaultCell().setBorderColor(new BaseColor(200, 200, 200));
				normalTable.getDefaultCell().setBorderWidth(0.9f);
				normalTable.getDefaultCell().setPadding(10);
				normalTable.getDefaultCell().setPaddingBottom(12);
				isTableHead = true;
				TableLineCount = 0;
				column_length = length;
			} else isTable = false;
		} 
		else if (st.size() == 0 && isTable == true) {
			int number1 = FindSpecialChar('|', line, 0);
			int number2 = FindSpecialChar('|', nextLine, 0);
			normalTable = new PdfPTable(TableColumn);
			normalTable.setWidthPercentage((float) (TableTotalLength * 1.5));
			normalTable.setWidths(column_length);
			normalTable.setHorizontalAlignment(Element.ALIGN_LEFT);
			normalTable.getDefaultCell().setBorderColor(new BaseColor(200, 200, 200));
			normalTable.getDefaultCell().setBorderWidth(0.9f);
			normalTable.getDefaultCell().setPadding(10);
			normalTable.getDefaultCell().setPaddingBottom(12);
			TableLineCount ++;
			if (TableLineCount % 2 == 0)
				normalTable.getDefaultCell().setBackgroundColor(new BaseColor(250,250,250));
			if (number2 == 0) {
				normalTable.setSpacingAfter(5);
			}
		} else {
			isTable = false;
		}
		
		if (st.size() == 0 && (str[index] == '>') && !isCode) {
			
			offset ++;
			quoteTable = new PdfPTable(1);   
			quoteTable.setWidthPercentage(100f);   
			if (!isQuote)
				quoteTable.setSpacingBefore(10.0f);
			
			quoteTable.getDefaultCell().setBorderColor(new BaseColor(230, 230, 230));
			quoteTable.getDefaultCell().setBorderWidthLeft(3);
			quoteTable.getDefaultCell().setBorderWidthBottom(0);
			quoteTable.getDefaultCell().setBorderWidthRight(0);
			quoteTable.getDefaultCell().setBorderWidthTop(0);
			quoteTable.getDefaultCell().setPadding(7);
			FontChinese_default.setColor(new BaseColor(150, 150, 150));
			index ++;
			isQuote = true;
			while (str[index] == ' ' ) {
				index ++;
				offset ++;
				if (index >= str.length) return 0;
			}
		} else {
			isQuote = false;
		}
		
		
		if (st.size() == 0 && (str[index] == '-' || str[index] == '*' || str[index] == '+') && str[index+1] == ' ') {
			Font dotfont = new Font(temp);
			if (blank == 0){
				par.setIndentationLeft(10.0f);
				par.add(new Chunk("●   ", dotfont));
			}
			else {
				par.setIndentationLeft(25.0f);
				par.add(new Chunk("○   ", dotfont));
			}
			offset += 2;
			index += 2;
			
		} //确认是否为无序序列
		else if (st.contains('#')) {
			switch (st.size()) {
			case 1:
				if (str[index] == ' ') {
					index ++;
					offset ++;
					temp = FontChinese_1;
					//par.setLeading(30.0f);
					par.setSpacingAfter(25.0f);
					par.setSpacingBefore(25.0f);
				}
				while (str[index] == ' ') {
					index ++;
					offset ++;
				}
				st.remove(0);
				break;
			case 2:
				if (str[index] == ' ') {
					index ++;
					offset ++ ;
					temp = FontChinese_2;
					par.setSpacingAfter(10.0f);
					par.setSpacingBefore(25.0f);
					isAddLine = true;
				}
				while (str[index] == ' ') {
					index ++;
					offset++;
				}
				st.remove(0);
				st.remove(0);
				
				break;
			case 3:
				if (str[index] == ' ') {
					index ++;
					offset ++;
					temp = FontChinese_3;
					par.setSpacingAfter(15.0f);
					par.setSpacingBefore(15.0f);
				}
				while (str[index] == ' ') {
					index ++;
					offset++;
				}
				st.remove(0);
				st.remove(0);
				st.remove(0);
				break;
			
			case 4:
				if (str[index] == ' ') {
					index ++;
					offset ++;
					temp = FontChinese_4;
					par.setSpacingAfter(15.0f);
					par.setSpacingBefore(15.0f);
				}
				while (str[index] == ' ') {
					index ++;
					offset++;
				}
				st.remove(0);
				st.remove(0);
				st.remove(0);
				st.remove(0);
				break;
			
			case 5:
				if (str[index] == ' ') {
					index ++;
					offset ++;
					temp = FontChinese_5;
					par.setSpacingAfter(15.0f);
					par.setSpacingBefore(15.0f);
				}
				while (str[index] == ' ') {
					index ++;
					offset++;
				}
				st.remove(0);
				st.remove(0);
				st.remove(0);
				st.remove(0);
				st.remove(0);
				break;

			default :
				temp = FontChinese_default;
				break;
			}
		}
		LineBlank = blank;
		
		if (isTableHead) {
			temp.setStyle(Font.BOLD);
		}
		int TableColumnCount = 0;
		while (index < str.length) {
			Font tempfont = new Font(temp);
			word.add(new Chunk(String.valueOf(str[index]), tempfont));
			
			switch(str[index]) {
				case '|': {
					int pos = st.search('|');
					if (pos <0) {
						st.add('|');
						stNumber.add(index);
						break;
					}
					while (!st.peek().equals('|')) {
						st.pop();
						stNumber.pop();
					}
					int indexPos = (int)stNumber.peek();
					st.pop();
					stNumber.pop();
					
					Phrase newphrase = new Phrase();
					for (int i=indexPos + 1; i< index; i++) {
						if (deleteWord[i] == 0)
							newphrase.add(word.get(i));
						deleteWord[i - offset] = 1;
					}
					deleteWord[indexPos - offset] = 1;
					deleteWord[index - offset] = 1;
					normalTable.addCell(newphrase);
					TableColumnCount ++;
					
					st.add('|');
					stNumber.add(index);	
					break;
				}
				case '`':
					int poss = st.search('`');
					if (poss < 0) {
						st.add(str[index]);
						stNumber.add(index);
						break;
					}
					if (!st.peek().equals('`')) {
						st.pop();
						stNumber.pop();
					}
					st.pop();
					int numberindex =(int) stNumber.peek();
					stNumber.pop();
					
					for (int i=numberindex+1; i<index; i++) {
						deleteWord[i-offset] = 2;
					}
					deleteWord[numberindex] = 1;
					deleteWord[index] = 1;
					break;
				case '~':
					int flag = 1;
					//System.out.println(st.size());
					for (int i=0; i<st.size(); i++) {
						if (st.get(i).equals('~')) {
							if (st.size() > i+2) {
								System.out.println(st.get(i+1) +" " + st.get(st.size()-1));
								
								if (st.get(i+1).equals('~') & st.get(st.size()-1).equals('~')) 
								{
									int firstPos = (int)stNumber.get(i+1);
									int lastPos = (int)stNumber.get(stNumber.size()-1);
									System.out.println(firstPos + " " + lastPos);
									for (int pos=firstPos+1; pos<lastPos; pos++) {
										Font newfont = word.get(pos-offset).getFont();
										newfont.setStyle(newfont.getStyle() | Font.STRIKETHRU);
										word.get(pos-offset).setFont(newfont);
									}
									
									deleteWord[firstPos - offset] = 1;
									deleteWord[firstPos-1 - offset] = 1;
									deleteWord[lastPos - offset] = 1;
									deleteWord[lastPos+1 - offset] = 1;

									st.pop();
									stNumber.pop();
									st.remove(i+1);
									st.remove(i);
									stNumber.remove(i+1);
									stNumber.remove(i);
									flag = 0;
									offset += 4;
								}	
							}
						}
					}
					if (flag == 0) {
						break;
					}
					st.add(str[index]);
					stNumber.add(index);
					break;
				case '*':
					int pos2 = st.search('*');
					if (pos2 < 0) {
						st.add(str[index]);
						stNumber.add(index);
						AsteriskCount = 1;
						break;
					}
					if ((int)stNumber.peek() == index - 1 && AsteriskCount >= 1 && str[index-AsteriskCount] == '*' && (int)stNumber.get(st.indexOf('*')) + AsteriskCount >= index - AsteriskCount ) {
						st.add(str[index]);
						stNumber.add(index);
						if (AsteriskCount < 3)
							AsteriskCount += 1;
						System.out.println("111   " + AsteriskCount);
						break;
					}
					if (str.length >=  index+2 && str[index+1] == '*') {
						System.out.println("112   " + index);
						st.add(str[index]);
						stNumber.add(index);
						break;
					} 
					while (str[index - AsteriskCount + 1] != '*') {
						AsteriskCount -= 1;
					}
					
					for (int i = 1; i<AsteriskCount; i++) {
						st.pop();
						stNumber.pop();
					}
					System.out.println("113");
					int sigPos = st.indexOf('*')+2;
					while (!st.peek().equals('*') || st.size()-1 > sigPos) {
						st.pop();
						stNumber.pop();
					}
					
					st.pop();
					int Firstpos = (int)stNumber.peek();
					int Lastpos = index - AsteriskCount + 1;
					stNumber.pop();
					for (int i=Firstpos+1; i<Lastpos;i++) {
						System.out.println(Firstpos + " " + Lastpos);
						Font newfont = word.get(i-offset).getFont();
						switch (AsteriskCount) {
							case 1:
								newfont.setStyle(Font.ITALIC | newfont.getStyle());
								break;
							case 2:
								newfont.setStyle(Font.BOLD | newfont.getStyle());
								break;
							case 3:
								newfont.setStyle(Font.ITALIC | Font.BOLD | newfont.getStyle());
								break;
						}
						word.get(i-offset).setFont(newfont);
					}
					for (int i=0; i<AsteriskCount; i++) {
						deleteWord[Firstpos - offset - i] = 1;
						deleteWord[Lastpos - offset + i] = 1;
					}
					for (int i=0; i<AsteriskCount-1; i++) {
						st.pop();
						stNumber.pop();
					}
					AsteriskCount = 0;
					System.out.println(st.size() + " " + stNumber.size());
					break;
				case '_':
					int pos3 = st.search('_');
					if (pos3 < 0) {
						st.add(str[index]);
						stNumber.add(index);
						AsteriskCount = 1;
						break;
					}
					//System.out.println((int)stNumber.peek());
					//System.out.println(index - 1);
					//System.out.println(AsteriskCount);
					//System.out.println(str[index-AsteriskCount]);
					//System.out.println(st.indexOf('_'));
					if ((int)stNumber.peek() == index - 1 && AsteriskCount >= 1 && str[index-AsteriskCount] == '_' && (int)stNumber.get(st.indexOf('_')) + AsteriskCount >= index - AsteriskCount ) {
						st.add(str[index]);
						stNumber.add(index);
						if (AsteriskCount < 3)
							AsteriskCount += 1;
						System.out.println("111   " + AsteriskCount);
						break;
					}
					if (str.length >=  index+2 && str[index+1] == '_') {
						System.out.println("112   " + index);
						st.add(str[index]);
						stNumber.add(index);
						break;
					} 
					while (str[index - AsteriskCount + 1] != '_') {
						AsteriskCount -= 1;
					}
					
					for (int i = 1; i<AsteriskCount; i++) {
						st.pop();
						stNumber.pop();
					}
					System.out.println("113");
					int sigPos_ = st.indexOf('_')+2;
					while (!st.peek().equals('_') || st.size()-1 > sigPos_) {
						st.pop();
						stNumber.pop();
					}
					
					st.pop();
					int Firstpos_ = (int)stNumber.peek();
					int Lastpos_ = index - AsteriskCount + 1;
					stNumber.pop();
					for (int i=Firstpos_+1; i<Lastpos_;i++) {
						System.out.println(Firstpos_ + " " + Lastpos_);
						Font newfont = word.get(i-offset).getFont();
						switch (AsteriskCount) {
							case 1:
								newfont.setStyle(Font.ITALIC | newfont.getStyle());
								break;
							case 2:
								newfont.setStyle(Font.BOLD | newfont.getStyle());
								break;
							case 3:
								newfont.setStyle(Font.ITALIC | Font.BOLD | newfont.getStyle());
								break;
						}
						word.get(i-offset).setFont(newfont);
					}
					for (int i=0; i<AsteriskCount; i++) {
						deleteWord[Firstpos_ - offset - i] = 1;
						deleteWord[Lastpos_ - offset + i] = 1;
					}
					for (int i=0; i<AsteriskCount-1; i++) {
						st.pop();
						stNumber.pop();
					}
					AsteriskCount = 0;
					break;
				case '[':
					st.add(str[index]);
					stNumber.add(index);
					break;
				case ']':
					st.add(str[index]);
					stNumber.add(index);
					break;
				case '(':
					if (st.search('[') <0) {
						st.add(str[index]);
						stNumber.add(index);
						break;
					}
					else if (st.peek().equals(']') && st.search('[')>-1 ) {
						isHref = 1;
					}
					st.add(str[index]);
					stNumber.add(index);
					break;
				case ')':
					if (isHref == 1) {
						int posi1= st.indexOf('[');
						int posi2 = st.indexOf(']');
						int thead = (int)stNumber.get(posi1)+1;
						int ttail = (int)stNumber.get(posi2)-1;
						
						System.out.println(thead + " " + ttail);
						
						int posi3 = st.indexOf('(');
						
						int hhead = (int)stNumber.get(posi3)+1;
						int htail = index;
						String url = String.valueOf(str).substring(hhead, htail);
						int refpos = url.indexOf('"');
						String ref;
						if (refpos < 0) {
							ref = null;
						} else {
							ref = url.substring(refpos, url.indexOf('"', refpos));
							url = url.substring(0, refpos-1);
						}
						System.out.println(url);
						int isPic = url.indexOf("png");
						if (isPic <0) isPic = url.indexOf("jpg");
						//if (isPic <0) isPic = url.indexOf("svg");
						if (isPic >= 0) {
							if (url.indexOf("/") < 0) {
								url = filedir + url;
							}
							Image img = Image.getInstance(url);//选择图片
							img.setAlignment(Element.ALIGN_LEFT);
							float width = img.getWidth();
							if (width > 200) {
								float per = 40000/width;
								img.scalePercent(per);
							}
								
						    document.add(img);
						    return 0;
						}
						if (thead > ttail) {
							break;
						}
					    
						//URL picURL = new URL(url);
						//if (isPic >= 0) {
						//	DataInputStream dataInputStream = new DataInputStream(picURL.openStream());
						//	PdfImageObject imgObject = new PdfImageObject(dataInputStream);
						//}
						for (int i=thead; i<=ttail; i++) {
							Font newfont = word.get(i-offset).getFont();
							newfont.setStyle(newfont.getStyle()|Font.UNDERLINE);
							newfont.setColor(new BaseColor(70,130,180));
							word.get(i-offset).setFont(newfont);
							Anchor anchor = new Anchor(url);
							if (refpos >= 0) {
								anchor.setReference(ref);
							}
							word.get(i-offset).setAnchor(url);
						}
						for (int i=hhead-2; i<=htail; i++) {
							deleteWord[i-offset] = 1;
						}
						deleteWord[thead-1-offset] = 1;
						
					}
					isHref = 0;
			}
			index ++;
		}
		//System.out.println(isTable);
		PdfPTable cc = new PdfPTable(1);  
		
		cc.getDefaultCell().setBorderWidthBottom(0);
		cc.getDefaultCell().setBorderWidthTop(0);
		cc.getDefaultCell().setBorderWidthLeft(0);
		cc.getDefaultCell().setBorderWidthRight(0);
		cc.getDefaultCell().setCellEvent(new SpecialWholeRoundedCell());
		
		if (isQuote) {
			Phrase p = new Phrase();
			p.add(new Chunk("  ", FontChinese_default));
			for (int i=0; i<word.size(); i++) {
				if (deleteWord[i] != 1)
					p.add(word.get(i));
				
			}
			quoteTable.addCell(p);
			document.add(quoteTable);
		} else if (isTable) {
			document.add(normalTable);
		} else {
			for (int i=0; i<word.size(); i++) {
				if (deleteWord[i] == 0)
					par.add(word.get(i));
				else if (deleteWord[i] == 2) {
					Phrase newSentence = new Phrase();
					int count=0;
					while (deleteWord[i] == 2) {
						newSentence.add(word.get(i));
						count ++;
						i++;
					}
					cc.setWidthPercentage((float) (count * 1.5));
					cc.addCell(newSentence);
					par.add(cc);
				}
			}
			document.add(par);
		}
		
		
		if (isAddLine) {
			PdfPTable tt = new PdfPTable(1);   
			tt.setWidthPercentage(100f);   
			//tt.setSpacingBefore(10.0f);
			tt.getDefaultCell().setBorderColor(new BaseColor(230, 230, 230));
			tt.getDefaultCell().setBorderWidth(0);
			tt.getDefaultCell().setBorderWidthTop(1);
			tt.getDefaultCell().setPadding(7);
			tt.addCell("");    
			document.add(tt);
		}
		
		/*for (int i=0; i<st.size(); i++)
			System.out.println(st.get(i));
		for (int i=0; i<stNumber.size(); i++)
			System.out.println(stNumber.get(i));*/
		if (isTableHead == true)
			return 1;
		return 0;
    }
    
    class SpecialHeaderRoundedCell implements PdfPCellEvent {
        public void cellLayout(PdfPCell cell, Rectangle position,
            PdfContentByte[] canvases) {
            PdfContentByte canvas = canvases[PdfPTable.LINECANVAS];
            canvas.setColorStroke(new BaseColor(220, 220, 220));
            float llx = position.getLeft();
            float lly = position.getBottom();
            float urx = position.getRight();
            float ury = position.getTop();
            float r = 4;
            float b = 0.4477f;
            canvas.moveTo(urx, lly);
            canvas.lineTo(urx, ury - r);
            canvas.curveTo(urx, ury - r * b, urx - r * b, ury, urx - r, ury);
            canvas.lineTo(llx + r, ury);
            canvas.curveTo(llx + r * b, ury, llx, ury - r * b, llx, ury - r);
            canvas.lineTo(llx, lly);
            canvas.stroke();
        }
    }
    
    class SpecialMiddleRoundedCell implements PdfPCellEvent {
        public void cellLayout(PdfPCell cell, Rectangle position,
            PdfContentByte[] canvases) {
            PdfContentByte canvas = canvases[PdfPTable.LINECANVAS];
            canvas.setColorStroke(new BaseColor(220, 220, 220));
            float llx = position.getLeft();
            float lly = position.getBottom();
            float urx = position.getRight();
            float ury = position.getTop();
            float r = 4;
            float b = 0.4477f;
            canvas.moveTo(llx, lly);
            canvas.lineTo(llx, ury);
           
            canvas.moveTo(urx, lly);
            canvas.lineTo(urx, ury);
            canvas.stroke();
        }
    }
    
    class SpecialTailRoundedCell implements PdfPCellEvent {
        public void cellLayout(PdfPCell cell, Rectangle position,
            PdfContentByte[] canvases) {
            PdfContentByte canvas = canvases[PdfPTable.LINECANVAS];
            float llx = position.getLeft();
            float lly = position.getBottom();
            float urx = position.getRight();
            float ury = position.getTop();
            float r = 4;
            float b = 0.4477f;
            canvas.setColorStroke(new BaseColor(220, 220, 220));
            canvas.moveTo(llx, ury);
            canvas.lineTo(llx, lly + r);
            canvas.curveTo(llx, lly + r * b, llx + r * b, lly, llx + r, lly);
            canvas.lineTo(urx - r, lly);
            canvas.curveTo(urx - r * b, lly, urx, lly + r*b, urx, lly + r);
            canvas.lineTo(urx, ury);
            canvas.stroke();
        }
    }
    
    class SpecialWholeRoundedCell implements PdfPCellEvent {
        public void cellLayout(PdfPCell cell, Rectangle position,
            PdfContentByte[] canvases) {
            PdfContentByte canvas = canvases[PdfPTable.LINECANVAS];
            float llx = position.getLeft();
            float lly = position.getBottom();
            float urx = position.getRight();
            float ury = position.getTop();
            float r = 4;
            float b = 0.4477f;
            canvas.setColorStroke(new BaseColor(220, 220, 220));
            canvas.moveTo(llx, ury-r);
            canvas.curveTo(llx, ury-r*b, llx + r*b, ury, llx + r, ury);
            canvas.lineTo(urx-r, ury);
            canvas.curveTo(urx - r*b, ury, urx, ury - r * b, urx, ury - r);
            canvas.lineTo(urx, lly + r);
            canvas.curveTo(urx, lly + r * b, urx -  r * b, lly, urx - r, lly);
            canvas.lineTo(llx + r, lly);
            canvas.curveTo(llx + r * b, lly, llx, lly + r*b, llx, lly + r);
            canvas.lineTo(llx, ury-r);
            canvas.stroke();
        }
    }
    
    private int FindSpecialChar(char ch, String l, int flag) {
    		if (flag == 0) {
    			int count = 0;
    			
    			if (l.indexOf('|') < 0) return 0;
    			for (int i=0; i<l.length(); i++) {
    				if (l.charAt(i) == '|') count++;
    			}
    			
    			return count;
    		}else {
    			int count = 0;
    			if (l == null) return 0;
    			if (l.indexOf('|') < 0) return 0;
    			for (int i=0; i<l.length(); i++) {
    				char temp = l.charAt(i);
    				if (temp != '|' && temp != ' ' && temp != ':' && temp!='-')
    					return 0;
    				if (l.charAt(i) == '|') count++;
    			}
    			return count;
    		}
    }
}
