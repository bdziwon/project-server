import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ZipSender {

	public final static int BUF_SIZE = 1024;
	public static int ind=1;
	

	public static void copyFile(File file, File file2) throws IOException{
		
		FileInputStream fileInputStream  = new FileInputStream(file);
		FileOutputStream fileOutputStream  = new FileOutputStream(file2);
		

		byte[] b=ZipSender.readBuff(fileInputStream);
		
		while(ind!=-1){
			ZipSender.WriteBuff(fileOutputStream,b,ind);
			b=ZipSender.readBuff(fileInputStream);
			}
		
		fileInputStream.close();
		fileOutputStream.close();		
	}
	

	public static byte[] readBuff(FileInputStream fileInputStream)throws IOException {
		
		byte[] buf=new byte[BUF_SIZE];

		ind=fileInputStream.read(buf);
		if(ind==-1)fileInputStream.close();
		
		return buf;
	}

	public static void WriteBuff(FileOutputStream fileOutputStream
			, byte[] buf,int index) throws IOException {
		  
		if(index!=-1)fileOutputStream.write(buf, 0, index);
		else fileOutputStream.close();		
	}
	
	

	  public static void main(String[] args) throws Exception {
	  
		File file = new File("C:/Documents and Settings/janusz.JAN/workspace/servers.zip");
		File file2 = new File("C:/Documents and Settings/janusz.JAN/workspace/FileHandler/src/FileRead22222.zip");
		file2.createNewFile();
		ZipSender.copyFile(file, file2);
		

	  }

}