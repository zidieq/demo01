package Desktop;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainShow {
	public static void main(String[] args) throws IOException {
		String str="";
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		String filePath="d:\\test\\test.zip";
		File fi=new File(filePath);
		if(!fi.exists()){
			fi.createNewFile();
		}
		System.out.println("please input something");
		while(!"bye".equals(str=br.readLine())){
			System.out.println(str);
		}
	}
}
