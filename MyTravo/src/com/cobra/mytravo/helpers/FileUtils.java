package com.cobra.mytravo.helpers;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.util.EncodingUtils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;

public class FileUtils {
	private String SDPATH;

	private int FILESIZE = 4 * 1024;

	public String getSDPATH() {
		return SDPATH;
	}

	public FileUtils() {
		// 得到当前外部存储设备的目录( /SDCARD )
		SDPATH = Environment.getExternalStorageDirectory() + "/";
	}

	/**
	 * 在SD卡上创建文件
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public File createSDFile(String fileName) throws IOException {
		File file = new File(SDPATH + fileName);
		file.createNewFile();
		return file;
	}

	/**
	 * 在SD卡上创建目录
	 * 
	 * @param dirName
	 * @return
	 */
	public File createSDDir(String dirName) {
		File dir = new File(SDPATH + dirName);
		dir.mkdir();
		return dir;
	}

	/**
	 * 判断SD卡上的文件夹是否存在
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		return file.exists();
	}

	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 * 
	 * @param path
	 * @param fileName
	 * @param input
	 * @return
	 */
	public File write2SDFromInput(String path, String fileName,
			InputStream input) {
		File file = null;
		OutputStream output = null;
		try {
			createSDDir(path);
			file = createSDFile(path + fileName);
			output = new FileOutputStream(file);
			byte[] buffer = new byte[FILESIZE];

			int length;
			while ((length = (input.read(buffer))) > 0) {
				output.write(buffer, 0, length);
			}

			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	public static String readAssetFile(Context context, String fileName) {
		try {
			InputStream inputStream = context.getAssets().open(fileName);
			int length = inputStream.available();
			byte[] buffer = new byte[length];
			inputStream.read(buffer);
			String data = EncodingUtils.getString(buffer, "GB2312");
			inputStream.close();
			return data;
		} catch (FileNotFoundException e) {
			//LogUtil.error("FileUtils", fileName + " not found");
		} catch (Exception e) {
			//LogUtil.error("FileUtils", "readAssetFile", e);
		}
		return null;
	}
	
	public static String getFileName(String filePath) {
		if(TextUtils.isEmpty(filePath)) {
			return "";
		}
		int index = filePath.lastIndexOf("/");
		if(index >= 0) {
			return filePath.substring(index + 1);
		} else {
			return filePath;
		}
	}
	
	public static boolean saveFile(byte[] data, String filePath) {
		try {
			File file = new File(filePath);
			if(!file.exists()) {
				file.createNewFile();
			}
			BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(file));
			os.write(data);
			os.flush();
			os.close();
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}
	
	public static boolean mkdirIfNotExist(String path) {
		File file = new File(path);
		boolean ret = true;
		if(!file.exists()) {
			try {
				ret = file.mkdirs();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return ret;
	}
	
	public static String getSDCardRootDirectory() {
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			String dir = Environment.getExternalStorageDirectory() + "/" + "feedback";
			if(mkdirIfNotExist(dir)) {
				return dir;
			}
		}
		return null;
	}
	
	public static String getLogDirectory() {
		return getDirectory("log");
	}
	
	public static String getImageDirectory() {
		return getDirectory("image");
	}
	
	public static String getAppIconDirectory(Context context) {
//		return getDirectory("appicon");
		String dir = context.getFilesDir().getAbsolutePath() + "/appicon";
		if(mkdirIfNotExist(dir)) {
			return dir;
		} else {
			return null;
		}
	}
	
	public static String getDirectory(String name) {
		String rootDir = FileUtils.getSDCardRootDirectory();
		if(!TextUtils.isEmpty(rootDir)) {
			String dir = rootDir + "/" + name;
			if(mkdirIfNotExist(dir)) {
				return dir;
			}
		}
		return null;
	}
	
	public static byte[] stream2ByteArray(InputStream is) {
		try {
			ByteArrayOutputStream swapStream = new ByteArrayOutputStream();  
	        byte[] buff = new byte[1024];  
	        int rc = 0;  
	        while ((rc = is.read(buff, 0, 1024)) != -1) {  
	            swapStream.write(buff, 0, rc);  
	        }  
	        return swapStream.toByteArray();  
		} catch (Exception e) {
			return null;
		}
	}
	
	public static Bitmap getAppIcon(Context context, String pkgName) {
		String iconDir = getAppIconDirectory(context);
		if(!TextUtils.isEmpty(iconDir)) {
			String iconPath = iconDir + "/" + pkgName + ".png";
			if(new File(iconPath).exists()) {
				return BitmapFactory.decodeFile(iconPath);
			}
		}
		//return BitmapFactory.decodeResource(context.getResources(), R.drawable.feedback_default);
		return null;
	}
	
	public static void copyIconsToSDCard(final Context context) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				//boolean loaded = UserModel.getInstance(context).isLoadedIcon();
				boolean loaded;
				loaded = false;
				if (!loaded) {
					String dir = FileUtils.getAppIconDirectory(context);
					if (!TextUtils.isEmpty(dir)) {
						try {
							String[] files = context.getAssets().list("appicon");
							if (files != null && files.length > 0) {
								for (String fileName : files) {
									String filePath = dir + "/" + fileName;
									if(!new File(filePath).exists()) {
										InputStream is = context.getAssets().open(
												"appicon/" + fileName);
										FileUtils.saveFile(
												FileUtils.stream2ByteArray(is),
												filePath);
									}
								}
							}
							//UserModel.getInstance(context).saveLoadedIcon(true);
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}
			}
		}).start();
	}
}
