import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

public class MDToPDF {
	
    public void OpenNewFile(String MDdir, String PDFdir, String name, String MdFileName) {
		File file = new File(MDdir + MdFileName);
		BufferedReader in = null;
		try {
			Document document = new Document(PageSize.A4, 36, 36, 50,50); 
			PdfWriter.getInstance(document, new FileOutputStream(new File(PDFdir + name)));
			AnalyseContainer mAnaly = new AnalyseContainer(document, MDdir);
			in = new BufferedReader(new FileReader(file));
			String tempbyte;
			document.add(Chunk.NEWLINE);
			tempbyte = in.readLine();
			List<String> buffer = null;
        while (tempbyte!= null) {
        		String nextLine;
        		if (buffer==null)
        			 nextLine = in.readLine();
        		else {
        			nextLine = buffer.get(0);
        			buffer.remove(0);
        			if (buffer.size() ==0) {
        				buffer = null;
        			}
        		}
            	System.out.println(tempbyte);
            	int number1 =FindSpecialChar('|', tempbyte, 0);
            	int number2 =FindSpecialChar('|', nextLine, 1);
            	if (number1 == number2 && number1 != 0) {
            		buffer = new ArrayList<String>();
            		int[] maxLength = new int[number1-1];
            		String[] splitRes= tempbyte.split("\\|");
    				for (int i=0; i<number1-1; i++) 
    					maxLength[i] = splitRes[i+1].length();
            		
            		while (true)
            		{
            			String temp = in.readLine();
            			if (FindSpecialChar('|', temp, 0) > 0) {
            				buffer.add(temp);
            				splitRes= temp.split("\\|");
            				for (int i=0; i<number1-1; i++) 
            					if (splitRes[i+1].length() > maxLength[i]) maxLength[i] = splitRes[i+1].length();
            				
            			} else {
            				buffer.add(temp);
            				break;
            			}
            		}
            		mAnaly.WordAnalyse(tempbyte, nextLine, maxLength);
            		nextLine = buffer.get(0);
            		buffer.remove(0);
            		tempbyte = nextLine;
            	}
            	else {
            		mAnaly.WordAnalyse(tempbyte, nextLine, null);
            		tempbyte = nextLine;
            	}
        }
        in.close();
        document.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}	
    }

	private static int FindSpecialChar(char ch, String l, int flag) {
		if (flag == 0) {
			int count = 0;
			
			if (l.indexOf('|') < 0) return 0;
			for (int i=0; i<l.length(); i++) {
				if (l.charAt(i) == '|') count++;
			}
			
			return count;
		}else {
			//System.out.println(l);
			if (l == null) return 0;
			int count = 0;
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


class AfterAnalyse {
	int level;
	String ResString;
	
	AfterAnalyse(int l, String S) {
		level = l;
		ResString = S;
	}
}
