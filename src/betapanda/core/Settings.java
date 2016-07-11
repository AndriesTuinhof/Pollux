package betapanda.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Settings {

	public static String dataPath ="";
	private static int ServerPort =4242;
	
	public static float mouse_sensitivity =1f /6f;
	public static float FOV =85;
	
	public static void Initialize(File f){
		if(!f.exists()){
			System.out.println("Hi guys");
			createFile(f.getAbsolutePath());
		}
		
		try{
			InputStreamReader is =new InputStreamReader(new FileInputStream(f));
			BufferedReader r =new BufferedReader(is);
			String ss;
			
			String[] data;
			while((ss =r.readLine()) !=null){
				if(ss.startsWith("DataPath")){
					data =ss.split(" ");
					dataPath =data[1];
				}
				if(ss.startsWith("ServerPort")){
					data =ss.split(" ");
					ServerPort =Integer.parseInt(data[1]);
				}
			}
			r.close();
			System.out.println("[CLient] Setting file loaded.");
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("[CLient] Something went wrong during loading of the setting file.");
		}
	}
	
	protected static void createFile(String s){
		try{
			OutputStreamWriter os =new OutputStreamWriter(new FileOutputStream(new File(s)));
			BufferedWriter r =new BufferedWriter(os);
			r.write("DataPath Resources/");
			r.newLine();
			r.write("ServerPort "+ServerPort);
			r.newLine();
			r.close();
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("[CLient] Something went wrong during creating of a setting file.");
		}
	}
}
