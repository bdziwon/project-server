import java.io.File;
import java.io.IOException;

public class ZipHandler {


	public String FolderToZip;
	public String ZippedFolderPath;
	public String ZipDestination;
	
	ZipHandler(String folderToZip, String zippedFolderPath, String zipDestination){
		this.FolderToZip=folderToZip;
		this.ZippedFolderPath=zippedFolderPath;
		this.ZipDestination=zipDestination;
	}
	
	static void HandleZip(ZipHandler zipHandler) throws IOException{
	
		FileZipper.zipFolder(zipHandler.FolderToZip,zipHandler.ZippedFolderPath);
		
		File file = new File(zipHandler.ZippedFolderPath);
		File file2 = new File(zipHandler.ZipDestination);
		file2.createNewFile();
		ZipSender.copyFile(file,file2);	
	}
	
	public static void main(String[] args) throws Exception {
		ZipHandler zipHandler=
				new ZipHandler("C:/Documents and Settings/janusz.JAN/workspace/FileHandler/src"
						,"C:/Documents and Settings/janusz.JAN/workspace/FileHandler/src/bla.zip"
						,"C:/Documents and Settings/janusz.JAN/workspace/FileHandler/src/blaDestination.zip");
		HandleZip(zipHandler);
		

/*		
		
		String zipPath="C:/Documents and Settings/janusz.JAN/workspace/FileHandler/src/bla.zip";
		FileZipper.zipFolder("C:/Documents and Settings/janusz.JAN/workspace/FileHandler/src"
				, zipPath);
		
		String input= zipPath;
		String output= "C:/Documents and Settings/janusz.JAN/workspace/FileHandler/src/blaDest.zip";		
		
		File file = new File(input);
		File file2 = new File(output);
		file2.createNewFile();

		ZipSender.copyFile(file, file2);
	*/
	}

}
