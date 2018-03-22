package com.example.zhhr.dynamicloaddexdemo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

public class LoadUtil {
	private DexClassLoader mDexClassLoader;

	private Class<?> mToastClass;
	private Object mInstanceObject;
	private Method getInstanceMethod,showToastMethod;

	private final String mOriginPath = "dongtai_dex.jar";//assets的原始文件
	private final String mOutPath = "/dongtai_dex.jar";//file目录下的文件


	private static LoadUtil mInstance;

	private Context mContext;

	public LoadUtil(Context context){
		mContext = context;
	}
	/**
	 * 获取类型实例
	 * @return
	 */
	public static LoadUtil getInstance(Context context){
		if (mInstance == null) {
			synchronized (LoadUtil.class) {
				if (mInstance == null) {
					mInstance = new LoadUtil(context);
				}
			}
		}
		return mInstance;
	}

	/**
	 * 初始化
	 */
	@SuppressLint("NewApi") public boolean init() {
		try {
			descryptFile(mContext);
			String destFilePath = mContext.getFilesDir().getAbsolutePath() + mOutPath;
			File opFile = new File(destFilePath);
			Log.d("zhhr", opFile.getAbsolutePath());
			if (!opFile.exists()) {
				return false;
			}
			//首先获取实例
			mDexClassLoader = new DexClassLoader(opFile.toString()
					, mContext.getFilesDir().getAbsolutePath()
					, null
					, ClassLoader.getSystemClassLoader().getParent());
			//加载其中的类
			mToastClass = mDexClassLoader.loadClass("com.example.dongtai.ToastUtil");
			//获取到单例模式的方法
			getInstanceMethod = mToastClass.getMethod("getInstance",Context.class);
			showToastMethod = mToastClass.getMethod("showToast");
			//获取实例对象
			mInstanceObject = getInstanceMethod.invoke(mToastClass,mContext);
			//执行showTosat方法
			showToastMethod.invoke(mInstanceObject);


		} catch (Exception e) {
			Log.d("zhhr", e.toString());
			return false;
		}
		return true;
	}

	/**
	 * 将assets目录下的资源拷贝到file目录下
	 * @param context
	 * @throws IOException
	 */
	private boolean descryptFile(Context context) throws IOException{
		File destFile = new File(context.getFilesDir().getAbsolutePath() + mOutPath);
		if (destFile.exists()) {
			long s = 0;
			FileInputStream fis = null;
			fis = new FileInputStream(destFile);
			s = fis.available();
			if (s > 20) {// 文件大小，方式重复拷贝dex文件,20是随便给的
				return true;
			}
		}
		InputStream assetsFileInputStream = null;
		try {
			assetsFileInputStream = context.getAssets().open(mOriginPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!destFile.getParentFile().exists()) {
			destFile.getParentFile().mkdirs();
		}
		destFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(destFile);
		int readNum = 0;
		while((readNum = assetsFileInputStream.read()) != -1){
			fos.write(readNum);
		}
		fos.close();
		assetsFileInputStream.close();
		return true;
	}

}
