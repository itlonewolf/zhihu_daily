package com.example.zhihudaily.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.zhihudaily.util.DBUtils;

public class ZhihuProvider extends ContentProvider {

	public static final UriMatcher sMatcher;

	static {
		sMatcher = new UriMatcher(UriMatcher.NO_MATCH);

		sMatcher.addURI(ZhihuContract.AUTHORITIES, ZhihuContract.PATH_INSERT,
				ZhihuContract.CODE_INSERT);
		sMatcher.addURI(ZhihuContract.AUTHORITIES,
				ZhihuContract.PATH_BULK_INSERT, ZhihuContract.CODE_BULK_INSERT);
		sMatcher.addURI(ZhihuContract.AUTHORITIES, ZhihuContract.PATH_QUERY,
				ZhihuContract.CODE_QUERY);
		sMatcher.addURI(ZhihuContract.AUTHORITIES,
				ZhihuContract.PATH_QUERY_DATE, ZhihuContract.CODE_QUERY_DATE);
		sMatcher.addURI(ZhihuContract.AUTHORITIES,
				ZhihuContract.PATH_QUERY_MULTI, ZhihuContract.CODE_QUERY_MULTI);

		sMatcher.addURI(ZhihuContract.AUTHORITIES,
				ZhihuContract.PATH_INSERT_CONTENT,
				ZhihuContract.CODE_INSERT_CONTENT);

		sMatcher.addURI(ZhihuContract.AUTHORITIES,
				ZhihuContract.PATH_QUERY_CONTENT,
				ZhihuContract.CODE_QUERY_CONTENT);
	}

	private DBHelper dbHelper;

	@Override
	public boolean onCreate() {
		dbHelper = new DBHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase rdb = dbHelper.getReadableDatabase();
		switch (sMatcher.match(uri)) {
			case ZhihuContract.CODE_QUERY:
				final long id = ContentUris.parseId(uri);
				return rdb.query(DBUtils.NEWS_TABLE, null, "id=?",
						new String[] { String.valueOf(id) }, null, null, null);
			case ZhihuContract.CODE_QUERY_DATE:
				String date = uri.getLastPathSegment();
				return rdb.query(DBUtils.NEWS_TABLE, null, "date=?",
						new String[] { date }, null, null, sortOrder);
			case ZhihuContract.CODE_QUERY_MULTI:
				return rdb.query(DBUtils.NEWS_TABLE, projection, selection,
						selectionArgs, null, null, null);
			case ZhihuContract.CODE_QUERY_CONTENT:
				final long newsId = ContentUris.parseId(uri);
				return rdb.query(DBUtils.CONTENT_TABLE, null, "id=?",
						new String[] { String.valueOf(newsId) }, null, null, null);
			}
		return null;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		int rows = 0;
		boolean success = false;
		switch (sMatcher.match(uri)) {
			case ZhihuContract.CODE_BULK_INSERT:
				SQLiteDatabase wdb = dbHelper.getWritableDatabase();
				try {
					wdb.beginTransaction();
					for (ContentValues value : values) {
						rows++;
						wdb.replace(DBUtils.NEWS_TABLE, null, value);
					}
					wdb.setTransactionSuccessful();
					success = true;
				} finally {
					wdb.endTransaction();
				}
				break;
		}
		if (success) {
			getContext().getContentResolver().notifyChange(uri, null, false);
		}

		return success ? rows : -1;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase wdb = dbHelper.getWritableDatabase();
		boolean isList = false;
		long id = 0;
//		CursorLoader
		switch (sMatcher.match(uri)) {
			case ZhihuContract.CODE_INSERT:
				isList = true;
				id = wdb.replace(DBUtils.NEWS_TABLE, null, values);
				break;
			case ZhihuContract.CODE_INSERT_CONTENT:
				isList = false;
				id = wdb.replace(DBUtils.CONTENT_TABLE, null, values);
				break;
		}
		if (id != -1) {
			getContext().getContentResolver().notifyChange(uri, null, false);

			if (isList) {
				return Uri.withAppendedPath(ZhihuContract.CONTENT_QUERY,
						String.valueOf(id));
			} else {
				return ZhihuContract.getContentQueryById((int) id);
			}
		} else {
			return null;
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
