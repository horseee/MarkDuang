
public class FileInf {
	private String FileName;
	private String FileDir;
	private boolean isModified;
	
	FileInf(String n, String d) {
		FileName = n;
		FileDir = d;
		isModified = false;
	}
	
	public String GetFileName() {return FileName;}
	public String GetFileDir() {return FileDir;}
	public boolean GetIsModified() {return isModified;}
	
	public void SetFileName(String name) {
		FileName = name;
		isModified = false;
	}
	
	public void SetFileDir(String dir) {
		FileDir = dir;
	}
	
	public void SetModified() {
		isModified = true;
	}
	
	public void SetModified(boolean n) {
		isModified = false;
	}
}
