package anglerquiz.capture_questions;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Utils {
	static public File getSaveFilePath()
	{
//		System.out.println(System.getProperty("os.name"));
		String saveFilePath=System.getProperty("user.home")+File.separator;
		if (System.getProperty("os.name").equals("Linux"))
			saveFilePath+=".";
		saveFilePath+="angeltrainer";
		File saveFileP =new File(saveFilePath);
		if (!saveFileP.exists())saveFileP.mkdir();
		return saveFileP;
	}
	
	static public List<File> getCapturedFiles()
	{
		File[] xmlFiles=getSaveFilePath().listFiles(new FileFilter() {
			
			public boolean accept(File arg0) {
				if (arg0.isFile())
				{
					String extension=arg0.getName().substring(arg0.getName().lastIndexOf("."));
					if (extension.equalsIgnoreCase(".xml")) return true;
				}
				return false;
			}
		});
		List saveFiles=Arrays.asList(xmlFiles);
		Collections.reverse(saveFiles);
		return saveFiles;
	}
	
	static public File getNextFreeSaveFile()
	{
		List<File> existingFiles=getCapturedFiles();
		int maxEncounteredSaveFile=0;
		for (File file:existingFiles)
		{
			String fileId=file.getName().substring(file.getName().lastIndexOf("-")+1);
			fileId=fileId.substring(0,fileId.lastIndexOf("."));
			maxEncounteredSaveFile=Math.max(maxEncounteredSaveFile, Integer.parseInt(fileId));
		}
		return new File(getSaveFilePath()+File.separator+"capture-"+(maxEncounteredSaveFile+1)+".xml");
	}
}
