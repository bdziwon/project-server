package zipper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class FileUnzipper {


	public static void unzip(File source, String out) throws IOException {

		try (ZipInputStream zipInputStream =
				new ZipInputStream(new FileInputStream(source))) {

			ZipEntry entry = zipInputStream.getNextEntry();

			while (entry != null) {

				File file = new File(out, entry.getName());

	            if (entry.isDirectory()) {
	            		file.mkdirs();
	            		}
	            else {
	            	File parent = file.getParentFile();

	            	if (!parent.exists()) {
	            				parent.mkdirs();
	            				}

	            	try (BufferedOutputStream bufferedOutputStream =
	            			new BufferedOutputStream(new FileOutputStream(file))) {

	            		byte[] buffer = new byte[Math.toIntExact(entry.getSize())];

	            		int location;

	            		while ((location = zipInputStream.read(buffer)) != -1) {
	            			bufferedOutputStream.write(buffer, 0, location);
	                    	}
	            	}

	            	}
	            entry = zipInputStream.getNextEntry();
	        	}
	    }
	}

	public static void main(String[] args) throws IOException {

		File file= new File("C:/Documents and Settings/janusz.JAN/workspace/FileHandler/.settings.zip");
		unzip(file,"C:/Documents and Settings/janusz.JAN/workspace/FileHandler/bin/");
	}
}
