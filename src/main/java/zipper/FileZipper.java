package zipper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileZipper {


	static public void zipFolder(String srcFolder, String destZipFile) throws IOException{
		ZipOutputStream zipOutputStream = null;
		FileOutputStream fileOutputStream = null;

		fileOutputStream = new FileOutputStream(destZipFile);
		zipOutputStream = new ZipOutputStream(fileOutputStream);

		addFolderToZip("", srcFolder, zipOutputStream);
		zipOutputStream.flush();
		zipOutputStream.close();
	}

	static private void addFileToZip(String path, String srcFile
			, ZipOutputStream zipOutputStream) throws IOException{
		File folder = new File(srcFile);
	  
		if (folder.isDirectory()) {
			addFolderToZip(path, srcFile, zipOutputStream);
			} 
		else {
			  
			byte[] buffer = new byte[1024];
			int length;
			FileInputStream fileInputStream = new FileInputStream(srcFile);
			zipOutputStream.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
			  
			while ((length = fileInputStream.read(buffer))>0){
				zipOutputStream.write(buffer, 0, length);
				}
			}
	}

	static private void addFolderToZip(String path
			, String srcFolder, ZipOutputStream zip) throws IOException{
		File folder = new File(srcFolder);

		for (String fileName : folder.list()) {
			
			if (path.equals("")) {
				addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
				} 
			else {
				addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
				}
			
		}
	}

}
