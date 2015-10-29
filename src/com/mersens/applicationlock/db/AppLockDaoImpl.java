package com.mersens.applicationlock.db;

import java.util.ArrayList;
import java.util.List;

import com.mersens.applicationlock.service.MyService;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
public class AppLockDaoImpl implements AppLockDao  {
	private DBHelper helper = null;
	private Context context;
	public AppLockDaoImpl(Context context){
		helper = new DBHelper(context);
		this.context=context;
	}
	@Override
	public boolean find(String name) {
		boolean flag=false;
		SQLiteDatabase database=helper.getReadableDatabase();
		Cursor cursor=database.rawQuery("select * from applock where name=?", new String[]{name});
		if(cursor.moveToNext()){
			flag=true;
		}
		cursor.close();
		database.close();
		return flag;
	}

	@Override
	public List<String> findAll() {
		List<String> names=new ArrayList<String>();
		SQLiteDatabase database=helper.getReadableDatabase();
		Cursor cursor=database.rawQuery("select name from applock" , null);
		while(cursor.moveToNext()){
			names.add(cursor.getString(0));
		}
		cursor.close();
		database.close();
		return names;
	}

	@Override
	public void add(String name) {
		SQLiteDatabase database=helper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("name", name);
		database.insert("applock", null, values);
		database.close();
		Intent intent=new Intent();
		intent.setAction(MyService.CHANGED);
		context.sendBroadcast(intent);
	}

	@Override
	public void delete(String name) {
		SQLiteDatabase database=helper.getWritableDatabase();
		database.delete("applock", "name=?", new String[]{name});
		database.close();
		Intent intent=new Intent();
		intent.setAction(MyService.CHANGED);
		context.sendBroadcast(intent);
		
	}
	
	

}
