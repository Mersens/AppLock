package com.mersens.applicationlock.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtil {
	private static SharePreferenceUtil sp;
	private  SharedPreferences mSharedPreferences;
	private  SharedPreferences.Editor editor;
	public static final String PREFERENCE_NAME = "_sharedinfo";
	public static final String USER_PSD="user_psd";
	public static final String ISLOCK="isLock";
	private SharePreferenceUtil(Context context){
		mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		editor = mSharedPreferences.edit();
	}

	public static synchronized SharePreferenceUtil getInstance(Context context){
		if(sp==null){
			sp=new SharePreferenceUtil(context);
		}
		return sp;
	}
	
	public String getUserPsd(){
		return mSharedPreferences.getString(USER_PSD, null);
	}
	public void setUserPsd(String psd){
		editor.putString(USER_PSD, psd);
		editor.commit();
	}
	
	public boolean isLock(){
		return mSharedPreferences.getBoolean(ISLOCK, false);
	}
	public void setLock(Boolean isLock){
		editor.putBoolean(ISLOCK, isLock);
		editor.commit();
	}
}
