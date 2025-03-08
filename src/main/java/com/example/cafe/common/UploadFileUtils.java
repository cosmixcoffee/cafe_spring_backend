package com.example.cafe.common;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.UUID;

import org.springframework.util.FileCopyUtils;

public class UploadFileUtils {
	static void makeDir(String uploadPath, String... paths) {
		if (new File(paths[paths.length -1]).exists()) {
			return;
		}
		for (String path : paths) {
			File dirPath = new File(uploadPath + path);
			if (!dirPath.exists()) {
				dirPath.mkdir();
			}
		}
	}
	
	static String calcPath(String upload_path) {
		Calendar cal = Calendar.getInstance();
		String year = "/" + cal.get(Calendar.YEAR);
		String month = year + "/" + new DecimalFormat("00").format(cal.get(Calendar.MONTH) + 1);
		String path = month + "/" + new DecimalFormat("00").format(cal.get(Calendar.DATE) + 1);
		makeDir(upload_path, year, month, path);
		return path;
	}
	
	public static String UploadFile(String uploadPath, String originalName, byte[] fileData) throws Exception {
		UUID uid = UUID.randomUUID();
		String filename = uid.toString() + "_" + originalName;
		String path = calcPath(uploadPath);
		File target = new File(uploadPath+ path, filename);
		FileCopyUtils.copy(fileData, target);
		String str = uploadPath + path + "/" + filename;
		return str;
	}
}
