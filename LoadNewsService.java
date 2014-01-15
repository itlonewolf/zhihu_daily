package com.example.zhihudaily;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.example.zhihudaily.model.Content;
import com.example.zhihudaily.model.Latest;
import com.example.zhihudaily.model.News;
import com.example.zhihudaily.model.TopStory;
import com.example.zhihudaily.provider.ZhihuContract;
import com.example.zhihudaily.util.HttpUtils;
import com.example.zhihudaily.util.PrefUtils;

public class LoadNewsService extends IntentService {
	private static final String TAG = "LoadNewsService" ;
	private static final String SERVICE_NAME = "load_news_service";

	public static final String URL_KEY = "url_key";

	public static final String DATA_TYPE = "data_type";

	public static void loadLatest(Context context) {
		Intent latestIntent = new Intent(context, LoadNewsService.class);
		latestIntent.putExtra(URL_KEY,
				"http://news-at.zhihu.com/api/2/news/latest");
		latestIntent.putExtra(DATA_TYPE, DataType.LATEST.toString());

		Log.w(TAG, "loadLatest>>>启动下载");
		context.startService(latestIntent);
	}

	public static void loadNews(Context context, String url) {
		Intent newsIntent = new Intent(context, LoadNewsService.class);
		newsIntent.putExtra(URL_KEY, url);
		newsIntent.putExtra(DATA_TYPE, DataType.NEWS.toString());
		Log.w(TAG, "loadNews>>>下载");
		context.startService(newsIntent);
	}

	public LoadNewsService() {
		super(SERVICE_NAME);
	}

	public static enum DataType {
		/** 今日新闻列表 */
		LATEST() {
			@Override
			public String toString() {
				return "LATEST";
			}
		},
		/** 一篇新闻 */
		NEWS() {
			@Override
			public String toString() {
				return "NEWS";
			}
		};
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		final DataType type = DataType
				.valueOf(intent.getStringExtra(DATA_TYPE));
		final String url = intent.getStringExtra(URL_KEY);

		Log.d(TAG, "onHandleIntent") ;
		if (!TextUtils.isEmpty(url)) {
			final byte[] data = HttpUtils.get(url, null);
			if (data.length > 0) {
				final String json = new String(data);
				switch (type) {
				case LATEST:
					handleLatest(json);
					break;
				case NEWS:
					handleNews(json);
					break;
				}
			}
		}
	}

	private void handleNews(String json) {
		try {
			Content content = Content.getContent(new JSONObject(json));
			
			ContentValues values = new ContentValues();
			values.put("body", content.body);
			values.put("image_source", content.image_source);
			values.put("title", content.title);
			values.put("url", content.url);
			values.put("image", content.image);
			values.put("share_url", content.share_url);
			values.put("id", content.id);
			values.put("ga_prefix", content.ga_prefix);
			values.put("thumbnail", content.thumbnail);

			Intent updateIntent = new Intent(getApplicationContext(),
					UpdateDatabaseService.class);
			updateIntent.putExtra(UpdateDatabaseService.URI_KEY,
					ZhihuContract.CONTENT_URI_INSERT_CONTENT);
			updateIntent.putExtra(UpdateDatabaseService.CONTENT_KEY, values);
			Log.d(TAG, "UpdateDatabaseService》》》handleNews启动服务，将数据插入到数据库中") ;
			startService(updateIntent);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void handleLatest(String json) {
		try {
			Latest latest = Latest.getLatest(new JSONObject(json));

			// 保存TopStory的id
			int[] topStroiesIds = new int[latest.top_stories.size()];
			for (int i = 0; i < topStroiesIds.length; i++) {
				topStroiesIds[i] = latest.top_stories.get(i).id;
			}
			PrefUtils.saveTopStoriesIds(getApplicationContext(), topStroiesIds);

			Intent updateIntent = new Intent(getApplicationContext(),
					UpdateDatabaseService.class);
			updateIntent.putExtra(UpdateDatabaseService.URI_KEY,
					ZhihuContract.CONTENT_BULK_INSERT_NEWS);
			ArrayList<ContentValues> cvs = new ArrayList<ContentValues>();
			for (News news : latest.news) {
				ContentValues cv = new ContentValues();
				cv.put("image_source", news.image_source);
				cv.put("title", news.title);
				cv.put("url", news.url);
				cv.put("image", news.image);
				cv.put("share_url", news.share_url);
				cv.put("thumbnail", news.thumbnail);
				cv.put("ga_prefix", news.ga_prefix);
				cv.put("id", news.id);
				cv.put("date", latest.date);
				cvs.add(cv);
			}
			for (TopStory ts : latest.top_stories) {
				ContentValues cv = new ContentValues();
				cv.put("image_source", ts.image_source);
				cv.put("title", ts.title);
				cv.put("url", ts.url);
				cv.put("image", ts.image);
				cv.put("share_url", ts.share_url);
				cv.put("ga_prefix", ts.ga_prefix);
				cv.put("id", ts.id);
				cv.put("date", latest.date);
				cvs.add(cv);
			}
			updateIntent.putParcelableArrayListExtra(
					UpdateDatabaseService.CONTENT_KEY, cvs);
			Log.d(TAG, "UpdateDatabaseService》》》handleLatest》》启动服务，将数据插入到数据库中") ;
			startService(updateIntent);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}