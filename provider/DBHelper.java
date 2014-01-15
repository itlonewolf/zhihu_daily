package com.example.zhihudaily.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.zhihudaily.util.DBUtils;

public class DBHelper extends SQLiteOpenHelper {

	public DBHelper(Context context) {
		super(context, DBUtils.DB_NAME, null, DBUtils.VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DBUtils.NEWS_TABLE_DDL);
		db.execSQL(DBUtils.CONTENT_TABLE_DDL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
