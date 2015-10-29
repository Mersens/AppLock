package com.mersens.applicationlock.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static final int VERSION=1;
	private static final String NAME="applock.db";
	private static final String SQL_CREAT="create table applock(_id integer primary key autoincrement,name varchar(20))";
	private static final String SQL_DROP="drop table if exists applock";

	public DBHelper(Context context) {
		super(context, NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREAT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DROP);
		db.execSQL(SQL_CREAT);
	}
}
