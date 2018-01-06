

import javax.swing.text.Style;
import javax.swing.text.StyledDocument;

public class ColouringTask implements Runnable {
	private StyledDocument doc;
	private Style style;
	private int pos;
	private int len;

	public ColouringTask(StyledDocument doc, int pos, int len, Style style) {
		this.doc = doc;
		this.pos = pos;
		this.len = len;
		this.style = style;
	}

	public void run() {
		try {
			// 这里就是对字符进行着色
			doc.setCharacterAttributes(pos, len, style, true);
		} catch (Exception e) {}
	}
}
