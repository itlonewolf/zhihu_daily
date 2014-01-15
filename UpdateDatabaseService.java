package com.example.zhihudaily;

import java.util.List;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.example.zhihudaily.provider.ZhihuContract;
import com.example.zhihudaily.provider.ZhihuProvider;

public class UpdateDatabaseService extends IntentService {
	private static final String TAG = "UpdateDatabaseService" ;
	public static final String SERVICE_NAME = "UpdateDatabaseService";

	public static final String URI_KEY = "uri_key";
	public static final String CONTENT_KEY = "content_key";

	public UpdateDatabaseService() {
		super(SERVICE_NAME);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		Uri uri = (Uri) intent.getExtras().get(URI_KEY);
		switch (ZhihuProvider.sMatcher.match(uri)) {
		case ZhihuContract.CODE_BULK_INSERT:
			List<ContentValues> cvs = intent
					.getParcelableArrayListExtra(CONTENT_KEY);
			Log.i(TAG, "onHandleIntent批量插入到数据库中") ;
			getContentResolver().bulkInsert(uri, convertList(cvs));
			break;
		case ZhihuContract.CODE_INSERT_CONTENT:
			ContentValues values = intent.getExtras()
					.getParcelable(CONTENT_KEY);
			Log.i(TAG, "onHandleIntent单个插入到数据库中") ;
			getContentResolver().insert(uri, values);
			break;
		}

	}

	private ContentValues[] convertList(List<ContentValues> list) {
		ContentValues[] cvs = new ContentValues[list.size()];
		for (int i = 0; i < cvs.length; i++) {
			cvs[i] = list.get(i);
		}

		return cvs;
	}

}
