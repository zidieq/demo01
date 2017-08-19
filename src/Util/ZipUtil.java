package Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JFileChooser;

public class ZipUtil {
	public static void main(String[] args) {
		JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		if (jfc.showOpenDialog(null) == jfc.FILES_ONLY) {
			String srcPath = jfc.getSelectedFile().getAbsolutePath();
			fastZip(srcPath);
			// String desPath = jfc.getSelectedFile().getParent() +
			// File.separator;
			// String zipName = jfc.getSelectedFile().getName();
			// if (FileToZip(srcPath, desPath, zipName)) {
			// System.out.println("ok");
			// } else {
			// System.out.println("There is something wrong with it ");
			// }
		}
	}

	/**
	 * 将指定的文件或文件夹压缩至同一目录下，并以原文件或文件夹名命名
	 * 
	 * @param srcPath
	 * @return
	 */
	public static boolean fastZip(String srcPath) {
		File jfc = new File(srcPath);
		String srcPathstr = jfc.getAbsolutePath();
		String desPath = jfc.getParent() + File.separator;
		String zipName = jfc.getName();
		System.out.println(srcPathstr + "," + desPath + "," + zipName);
		if (FileToZip(srcPathstr, desPath, zipName)) {
			System.out.println("ok");
			return true;
		} else {
			System.out.println("There is something wrong with it ");
			return false;
		}
	}

	/**
	 * 将指定位置的文件或文件夹，压缩后以zipFileName为名，存放到desPath下面
	 * 
	 * @param srcPath
	 * @param desPath
	 * @param zipFileName
	 * @return
	 */
	public static boolean FileToZip(String srcPath, String desPath, String zipFileName) {
		try {
			ZipOutputStream zop = new ZipOutputStream(new FileOutputStream(new File(desPath, zipFileName + ".zip")));
			File srcFile = new File(srcPath);
			addToZip(null, zop, srcFile);
			zop.close();
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private static void addToZip(String zipFileName, ZipOutputStream zop, File srcFile)
			throws FileNotFoundException, IOException {
		if (srcFile.isDirectory()) {
			File[] files = srcFile.listFiles();
			for (File file : files) {
				addToZip(zipFileName != null ? zipFileName + File.separator + srcFile.getName() : srcFile.getName(),
						zop, file);
			}
		} else {
			addFileToZip(zipFileName, zop, srcFile);
		}
	}

	private static void addFileToZip(String zipFileName, ZipOutputStream zop, File file)
			throws FileNotFoundException, IOException {
		InputStream in = new FileInputStream(file);
		String entryPath = (zipFileName != null ? zipFileName + File.separator + file.getName() : file.getName());
		System.out.println("entryPath:" + file.getAbsolutePath());
		zop.putNextEntry(new ZipEntry(entryPath));
		byte[] buf = new byte[1024 * 10];
		int len = -1;
		while ((len = in.read(buf)) > -1) {
			zop.write(buf, 0, len);
		}
		zop.closeEntry();
		in.close();
	}

}
