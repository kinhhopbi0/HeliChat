package com.pdv.heli.common.android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;

import com.pdv.heli.app.HeliApplication;

public class BitmapStorage {

	public static final String IMAGE_DIR = "img";
	public static final String AVATAR_DIR_NAME = "avatar";

	private static final File ROOT_DIR = HeliApplication.getInstance()
			.getFilesDir();

	private static File imageDir = new File(ROOT_DIR.getAbsolutePath()
			+ File.separator + IMAGE_DIR);
	private static File avatarDir = new File(ROOT_DIR.getAbsolutePath()
			+ File.separator + IMAGE_DIR + File.separator + AVATAR_DIR_NAME);

	static {
		if (!imageDir.exists()) {
			imageDir.mkdir();
		}
		if (!avatarDir.exists()) {
			avatarDir.mkdir();
		}
	}

	public BitmapStorage(Context context) {

	}

	public Bitmap readAvatarById(int pUserId) {
		return null;
	}

	public void writeAvatarById(int pUserId, Bitmap bitmap) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(avatarDir.getAbsolutePath()
					+ File.separator + "ava_" + pUserId);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
