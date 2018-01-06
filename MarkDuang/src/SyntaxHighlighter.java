

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

class SyntaxHighlighter implements DocumentListener {
	private Set<String> keywords;
	private Style keyword1Style;
	private Style keyword2Style;
	private Style keyword3Style;
	private Style keyword4Style;
	private Style keywordlistStyle;
	private Style keywordquoteStyle;
	private Style keywordcodeStyle;
	private Style normalStyle;
	FileInf fileinfo;
	JFrame frame;

	public SyntaxHighlighter(JTextPane editor, JFrame fr, FileInf f) {
		fileinfo = f;
		frame = fr;
		
		// 准备着色使用的样式
		keyword1Style = ((StyledDocument) editor.getDocument()).addStyle("Keyword_Style", null);
		keyword2Style = ((StyledDocument) editor.getDocument()).addStyle("Keyword_Style", null);
		keyword3Style = ((StyledDocument) editor.getDocument()).addStyle("Keyword_Style", null);
		keyword4Style = ((StyledDocument) editor.getDocument()).addStyle("Keyword_Style", null);
		keywordlistStyle = ((StyledDocument) editor.getDocument()).addStyle("Keyword_Style", null);
		keywordquoteStyle = ((StyledDocument) editor.getDocument()).addStyle("Keyword_Style", null);
		keywordcodeStyle = ((StyledDocument) editor.getDocument()).addStyle("Keyword_Style", null);
		normalStyle = ((StyledDocument) editor.getDocument()).addStyle("normalword_Style", null);
		
		StyleConstants.setForeground(keyword1Style, new Color(135,206,250));
		StyleConstants.setFontSize(keyword1Style, 20);
		StyleConstants.setBold(keyword1Style, true);
		StyleConstants.setSpaceAbove(keyword1Style, 6);
	    StyleConstants.setSpaceBelow(keyword1Style, 0);
		StyleConstants.setFontFamily(keyword1Style, "Monospace");
		
		StyleConstants.setForeground(keyword2Style, new Color(241,180,241));
		StyleConstants.setFontSize(keyword2Style, 16);
		StyleConstants.setSpaceAbove(keyword2Style, 6);
	    StyleConstants.setSpaceBelow(keyword2Style, 0);
	    StyleConstants.setFontFamily(keyword2Style, "Monospace");
		
		StyleConstants.setForeground(keyword3Style, new Color(166,238,188));
		StyleConstants.setFontSize(keyword3Style, 16);
		StyleConstants.setSpaceAbove(keyword3Style, 6);
	    StyleConstants.setSpaceBelow(keyword3Style, 0);
	    StyleConstants.setFontFamily(keyword3Style, "Monospace");
		
		StyleConstants.setForeground(keyword4Style, new Color(244,164,96));
		StyleConstants.setFontSize(keyword4Style, 16);
		StyleConstants.setSpaceAbove(keyword4Style, 6);
	    StyleConstants.setSpaceBelow(keyword4Style, 0);
	    StyleConstants.setFontFamily(keyword4Style, "Monospace");
		
		StyleConstants.setForeground(normalStyle, Color.WHITE);
		StyleConstants.setFontSize(normalStyle, 16);
		StyleConstants.setSpaceAbove(normalStyle, 6);
	    StyleConstants.setSpaceBelow(normalStyle, 0);
	    StyleConstants.setFontFamily(normalStyle, "Monospace");
		
		StyleConstants.setForeground(keywordlistStyle, new Color(135,206,250));
		StyleConstants.setFontSize(keywordlistStyle, 16);
		StyleConstants.setBold(keywordlistStyle, true);
		StyleConstants.setSpaceAbove(keywordlistStyle, 6);
	    StyleConstants.setSpaceBelow(keywordlistStyle, 0);
		StyleConstants.setFontFamily(keywordlistStyle, "Monospace");
		
		StyleConstants.setForeground(keywordquoteStyle, new Color(255,140,105));
		StyleConstants.setFontSize(keywordquoteStyle, 16);
		StyleConstants.setBold(keywordquoteStyle, true);
		StyleConstants.setSpaceAbove(keywordquoteStyle, 6);
	    StyleConstants.setSpaceBelow(keywordquoteStyle, 0);
		StyleConstants.setFontFamily(keywordquoteStyle, "Monospace");
		
		StyleConstants.setForeground(keywordcodeStyle, new Color(161,161,161));
		StyleConstants.setFontSize(keywordcodeStyle, 16);
		StyleConstants.setBold(keywordcodeStyle, false);
		StyleConstants.setSpaceAbove(keywordcodeStyle, 6);
	    StyleConstants.setSpaceBelow(keywordcodeStyle, 0);
		StyleConstants.setFontFamily(keywordcodeStyle, "Monospace");

		keywords = new HashSet();
		keywords.add("* ");
		keywords.add("+ ");
		keywords.add("- ");
		keywords.add("> ");
		keywords.add("``` ");
	}

	public void colouring(StyledDocument doc, int pos, int len) throws BadLocationException {
		int dealCount = 0;
		while (dealCount < len) {
			if (pos > doc.getLength()) break;
			int start = indexOfWordStart(doc, pos);
			int end = indexOfWordEnd(doc, pos);
			char ch;
			System.out.println(start + " " + end);
			int styleNumber = 0;
			int Asy = 0;
			
			int index = start+1;
			while (getCharAt(doc, index) == ' ') index++;
			ch = getCharAt(doc, index);
			if (index - start - 1 >= 4 && ch != '+'  && ch != '-' && ch != '*') {
				System.out.println("code");
				pos = end + 1;
				dealCount = end+1 - start;
				while (start < end) {
					SwingUtilities.invokeLater(new ColouringTask(doc, start, 1, keywordcodeStyle));
					++start;
				}
				continue;
			}
			
			if (start+1 < end) {
				String word = doc.getText(start+1, end- start - 1);
				//System.out.println(word);
				if (word.indexOf("# ") >= 0 || word.indexOf("## ")>= 0 || word.indexOf("### ")>= 0 || word.indexOf("#### ")>= 0 || word.indexOf("##### ")>= 0)
					styleNumber = 1; //大标题
				Asy = FindAsy(word);
				
				if (FindHref(word) == 1)
					styleNumber = 3;
				if (FindPic(word) == 1) 
					styleNumber = 2;
			}
			pos = end + 1;
			dealCount = end+1 - start;
			int isInsideCount = 0;
			
			ch = getCharAt(doc, index);
			if (ch == '*' || ch == '-' || ch == '`' || ch == '>' || ch == '+') {
				if (ch == '`' || (getCharAt(doc, index+1) == ' '))  {
					for (int i=start+1; i < index; i++) 
						SwingUtilities.invokeLater(new ColouringTask(doc, start, 1, normalStyle));
					start = colouringWord(doc, index);
				}
					
			} 
			if (start == -1) start = 0;
			while (start < end) {
				ch = getCharAt(doc, start);
				//System.out.println(ch + " "+ Asy);
				if ((ch == '*'||ch =='_') && Asy > 0) {
					SwingUtilities.invokeLater(new ColouringTask(doc, start, 1, keyword4Style));
					isInsideCount++;
					start++;
					continue;
				}
				switch (styleNumber) {
					case 0:
						if (Asy != 0 && (isInsideCount / Asy) % 2 == 1 && isInsideCount != 0)
							SwingUtilities.invokeLater(new ColouringTask(doc, start, 1, keyword4Style));
						else
							SwingUtilities.invokeLater(new ColouringTask(doc, start, 1, normalStyle));
						break;
					case 1:
						SwingUtilities.invokeLater(new ColouringTask(doc, start, 1, keyword1Style));
						break;
					case 2:
						SwingUtilities.invokeLater(new ColouringTask(doc, start, 1, keyword2Style));
						break;
					case 3:
						SwingUtilities.invokeLater(new ColouringTask(doc, start, 1, keyword3Style));
						break;
				}
				++start;
			}
			if (end == doc.getLength() - 1) 
				break;
		}
		
		
	}

	public int FindAsy(String s) {
		int pos = s.indexOf("***");
		if ((s.indexOf("***", pos+1) - pos) > 1) {
			return 3;
		}
		pos = s.indexOf("**");
		if ((s.indexOf("**", pos+1) - pos) >1) {
			return 2;
		}
		pos = s.indexOf("*");
		if ((s.indexOf("*", pos+1) - pos) > 1) {
			return 1;
		}
		pos = s.indexOf("___");
		if ((s.indexOf("___", pos+1) - pos) > 1) {
			return 3;
		}
		pos = s.indexOf("__");
		if ((s.indexOf("__", pos+1) - pos) >1) {
			return 2;
		}
		pos = s.indexOf("_");
		if ((s.indexOf("_", pos+1) - pos) > 1) {
			return 1;
		}
		return 0;
	}
	
	public int FindHref(String s) {
		int pos = s.indexOf("[");
		if (pos >= 0) {
			pos = s.indexOf("](", pos);
			if (pos > 0) {
				pos = s .indexOf(")", pos);
				if (pos > 0)
					return 1;
			}
		}
		return 0;
	}
	
	public int FindPic(String s) {
		int pos = s.indexOf("![");
		if (pos >= 0) {
			pos = s.indexOf("](", pos);
			if (pos > 0) {
				pos = s .indexOf(")", pos);
				if (pos > 0)
					return 1;
			}
		}
		return 0;
	}
	/**
	 * 对单词进行着色, 并返回单词结束的下标.
	 */
	public int colouringWord(StyledDocument doc, int pos) throws BadLocationException {
		int wordEnd;
		for (wordEnd = pos; wordEnd < doc.getLength() && getCharAt(doc, wordEnd) != ' '; wordEnd ++);
		String word = doc.getText(pos, wordEnd + 1 - pos);
		if (keywords.contains(word)) {
			// 如果是关键字, 就进行关键字的着色, 否则使用普通的着色.
			// 这里有一点要注意, 在insertUpdate和removeUpdate的方法调用的过程中, 不能修改doc的属性.
			// 但我们又要达到能够修改doc的属性, 所以把此任务放到这个方法的外面去执行.
			// 实现这一目的, 可以使用新线程, 但放到swing的事件队列里去处理更轻便一点.
			if (getCharAt(doc, pos) == '>')
				SwingUtilities.invokeLater(new ColouringTask(doc, pos, wordEnd - pos, keywordquoteStyle));
			else if (getCharAt(doc, pos) == '`')
				SwingUtilities.invokeLater(new ColouringTask(doc, pos, wordEnd - pos, keywordcodeStyle));
			else
				SwingUtilities.invokeLater(new ColouringTask(doc, pos, wordEnd - pos, keywordlistStyle));
		} else {
			SwingUtilities.invokeLater(new ColouringTask(doc, pos, wordEnd - pos, normalStyle));
		}

		return wordEnd;
	}

	/**
	 * 取得在文档中下标在pos处的字符.
	 * 如果pos为doc.getLength(), 返回的是一个文档的结束符, 不会抛出异常. 如果pos<0, 则会抛出异常.
	 * 所以pos的有效值是[0, doc.getLength()]
	 */
	public char getCharAt(Document doc, int pos) throws BadLocationException {
		return doc.getText(pos, 1).charAt(0);
	}

	/**
	 * 取得下标为pos时, 它所在的单词开始的下标. Â±wor^dÂ± (^表示pos, Â±表示开始或结束的下标)
	 * @throws BadLocationException
	 */
	public int indexOfWordStart(Document doc, int pos) throws BadLocationException {
		// 从pos开始向前找到第一个非单词字符.
		for (; pos >= 0 && getCharAt(doc, pos)!='\n'; --pos);

		return pos;
	}

	/**
	 * 取得下标为pos时, 它所在的单词结束的下标. Â±wor^dÂ± (^表示pos, Â±表示开始或结束的下标)
	 * @return
	 * @throws BadLocationException
	 */
	public int indexOfWordEnd(Document doc, int pos) throws BadLocationException {
		// 从pos开始向前找到第一个非单词字符.
		for (; doc.getLength() > pos && getCharAt(doc, pos)!='\n'; ++pos) ;
		return pos;
	}

	/**
	 * 如果一个字符是字母, 数字, 下划线, 则返回true.
	 */
	public boolean isWordCharacter(Document doc, int pos) throws BadLocationException {
		char ch = getCharAt(doc, pos);
		if (ch == '*' || ch == '`'|| ch == '_' || ch == '>' || ch == ' '|| ch == '#') { return true; }
		return false;
	}

	@Override
	public void changedUpdate(DocumentEvent e) {

	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		try {
			colouring((StyledDocument) e.getDocument(), e.getOffset(), e.getLength());
			fileinfo.SetModified();
			frame.setTitle("﹡ " + fileinfo.GetFileName());
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		try {
			// 因为删除后光标紧接着影响的单词两边, 所以长度就不需要了
			colouring((StyledDocument) e.getDocument(), e.getOffset(), 0);
			fileinfo.SetModified();
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
	}
}