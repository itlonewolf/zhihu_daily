package com.example.zhihudaily.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DBUtils {

	public static final String DB_NAME = "zhihu";
	public static final int VERSION = 1;

	public static final String NEWS_TABLE = "news";
	public static final String CONTENT_TABLE = "content";

	/** 新闻表 DDL */
	public static final String NEWS_TABLE_DDL = "CREATE TABLE IF NOT EXISTS "
			+ NEWS_TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ " image_source TEXT, " + "title TEXT, " + "url TEXT, "
			+ "image TEXT, " + "share_url TEXT, " + "thumbnail TEXT, "
			+ "ga_prefix TEXT, id INTEGER UNIQUE, date TEXT)";

	public static final String CONTENT_TABLE_DDL = "CREATE TABLE IF NOT EXISTS "
			+ CONTENT_TABLE
			+ "(_id INTEGER PRIMARY KEY, body TEXT NOT NULL,"
			+ " image_source TEXT, title TEXT,url TEXT, image TEXT,"
			+ " share_url TEXT, id INTEGER UNIQUE, ga_prefix TEXT, thumbnail TEXT)";

	private final static SimpleDateFormat SDF = new SimpleDateFormat(
			"yyyyMMdd", Locale.US);

	public static String getDate(long mills) {
		return SDF.format(new Date(mills));
	}
}
