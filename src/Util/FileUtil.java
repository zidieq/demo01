package Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class FileUtil {
	/**
	 * 将给定的路径的文件的文本内容读出来
	 * 
	 * @param path
	 * @return
	 */
	public static String getFileContext(String path) {
		// 输入流

		try {
			return getFileContext(new FileInputStream(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getFileContext(InputStream in) {
		BufferedReader br = null;
		StringBuilder sb = null;
		try {
			// 输入流

			br = new BufferedReader(new InputStreamReader(in, "utf-8"));
			sb = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null) {
				// 按行读
				sb.append(line).append("\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 释放资源
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// 输出
		return sb.toString();
	}

	public static boolean println(String text, String filePath) {
		PrintWriter out = null;
		try {
			String fileName = filePath == null ? String.valueOf(System.currentTimeMillis()) + ".txt" : filePath;
			// File file = new File(filePath + File.separator + fileName);
			File file = new File(filePath);
			out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, true), "utf-8"));
			out.println(text);
			out.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
		}
		return false;
	}
}
