package com.example.zhihudaily.provider;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

public final class ZhihuContract {
	public static final String AUTHORITIES = "com.example.zhihudaily.provider";

	// content://com.example.zhihudaily.provider
	public static final Uri CONTENT_URI_BASE = Uri.parse("content://"
			+ AUTHORITIES);

	/** 插入 */
	public static final String PATH_INSERT = "insert";
	public static final int CODE_INSERT = 0;
	// content://com.example.zhihudaily.provider/insert
	public static final Uri CONTENT_INSERT_NEWS = Uri.withAppendedPath(
			CONTENT_URI_BASE, PATH_INSERT);

	/** 批量插入 */
	public static final String PATH_BULK_INSERT = "insert_bulk";
	public static final int CODE_BULK_INSERT = 1;
	public static final Uri CONTENT_BULK_INSERT_NEWS = Uri.withAppendedPath(
			CONTENT_URI_BASE, PATH_BULK_INSERT);

	/** 查询一条 */
	public static final String PATH_QUERY = "query/#";
	public static final int CODE_QUERY = 2;
	public static final Uri CONTENT_QUERY = Uri.withAppendedPath(
			CONTENT_URI_BASE, PATH_QUERY);

	public static final Uri getQueryUri(int id) {
		return Uri.withAppendedPath(CONTENT_URI_BASE, "query/" + id);
	}

	/** 按照日期查询 */
	public static final String PATH_QUERY_DATE = "query_date/*";
	public static final int CODE_QUERY_DATE = 3;
	public static final Uri CONTENT_QUERY_DATE = Uri.withAppendedPath(
			CONTENT_URI_BASE, PATH_QUERY_DATE);

	public static final String PATH_QUERY_MULTI = "query_multi";
	public static final int CODE_QUERY_MULTI = 4;
	public static final Uri CONTENT_QUERY_MULTI = Uri.withAppendedPath(
			CONTENT_URI_BASE, PATH_QUERY_MULTI);

	public static final Uri getQueryBtDateUri(String date) {
		if (date.length() > 8) {
			date = date.substring(0, 8);
		}
		return Uri.withAppendedPath(CONTENT_URI_BASE, "query_date/" + date);
	}

	public static Cursor queryByDate(ContentResolver cr, String date) {
		return cr.query(CONTENT_QUERY_DATE, null, "date=?",
				new String[] { date }, null);
	}

	public static final String PATH_INSERT_CONTENT = "insert_content";
	public static final int CODE_INSERT_CONTENT = 5;
	public static final Uri CONTENT_URI_INSERT_CONTENT = Uri.withAppendedPath(
			CONTENT_URI_BASE, PATH_INSERT_CONTENT);

	public static final String PATH_QUERY_CONTENT = "query_content/#";
	public static final int CODE_QUERY_CONTENT = 6;
	public static final Uri CONTENT_URI_QUERY_CONTENT = Uri.withAppendedPath(
			CONTENT_URI_BASE, PATH_QUERY_CONTENT);

	public static Uri getContentQueryById(int id) {
		return Uri.withAppendedPath(CONTENT_URI_BASE, "query_content/" + id);
	}
}
