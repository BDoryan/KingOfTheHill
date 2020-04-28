package doryan.koth.fr.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;

public class FileUtils {
	
	public static void writeInFile(String message, File file) {
		if(file == null){
			return;
		}
		
		if (!file.exists()) {
			return;
		} else {
			file.delete();
			
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			PrintWriter out = new PrintWriter(new FileOutputStream(file), true);
			out.println(message);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static String readInFile(File file){
		if(!file.exists())
			return null;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));
			String reponse = null;
			try {
				reponse = in.readLine();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return reponse;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
