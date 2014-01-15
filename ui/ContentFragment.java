package com.example.zhihudaily.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.zhihudaily.LoadNewsService;
import com.example.zhihudaily.R;
import com.example.zhihudaily.provider.ZhihuContract;

public class ContentFragment extends Fragment implements
		LoaderCallbacks<Cursor> {
	private static final String URL_KEY = "url_key";
	private String url;
	private static final String ID_KEY = "id_key";
	private int id;

	private static final int LOAD_CONTENT_LOADER_ID = 0;

	private WebView contentWebView;

	private boolean isLargeFont;
	private boolean isNightMode;
	private boolean isNonImageMode;

	public static ContentFragment getContentFragment(String url, int id) {
		Bundle data = new Bundle();
		data.putString(URL_KEY, url);
		data.putInt(ID_KEY, id);
		ContentFragment cf = new ContentFragment();
		cf.setArguments(data);
		return cf;
	}

	private final ContentObserver observer = new ContentObserver(null) {
		public void onChange(boolean selfChange) {
			if (getActivity() != null && isAdded()) {
				getLoaderManager().getLoader(LOAD_CONTENT_LOADER_ID)
						.forceLoad();
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActivity().getContentResolver().registerContentObserver(
				ZhihuContract.CONTENT_URI_INSERT_CONTENT, true, observer);

		Bundle data = getArguments();
		if (data != null) {
			url = data.getString(URL_KEY);
			id = data.getInt(ID_KEY);
		}

		if (TextUtils.isEmpty(url)) {
			Toast.makeText(getActivity(), "url无效", Toast.LENGTH_SHORT).show();
		}

		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(getActivity());

		isLargeFont = pref.getBoolean("preference_key_big_font_mode_open",
				false);
		isNightMode = pref.getBoolean("preference_key_dark_mode_open", false);
		isNonImageMode = pref.getBoolean("preference_key_no_image_mode_open",
				false);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.content_fragment, container, false);
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		contentWebView = (WebView) view.findViewById(R.id.content_webview);
		contentWebView.getSettings().setJavaScriptEnabled(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getLoaderManager().initLoader(LOAD_CONTENT_LOADER_ID, null, this);
	}

	@Override
	public void onResume() {
		super.onResume();

		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(getActivity());

		boolean newIsLargeFont = pref.getBoolean(
				"preference_key_big_font_mode_open", false);
		boolean newIsNightMode = pref.getBoolean(
				"preference_key_dark_mode_open", false);
		isNonImageMode = pref.getBoolean("preference_key_no_image_mode_open",
				false);
		if (newIsLargeFont != isLargeFont || newIsNightMode != isNightMode) {
			isLargeFont = newIsLargeFont;
			isNightMode = newIsNightMode;

			getLoaderManager().getLoader(LOAD_CONTENT_LOADER_ID).forceLoad();
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle data) {
		return new CursorLoader(getActivity(),
				ZhihuContract.getContentQueryById(this.id), null, null, null,
				null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
		if (c != null && c.getCount() > 0) {
			final int bodyIndex = c.getColumnIndex("body");
			while (c.moveToNext()) {
				final String body = c.getString(bodyIndex);

				NetworkInfo info = getNetworkInfo(getActivity());
				// 先判断有没有网
				// 如果有网：是不是wifi?
				// ---->是wifi：不阻塞
				// ---->不是wifi：阻塞
				// 没网：阻塞
				contentWebView.getSettings().setBlockNetworkImage(
						hasNetwork(info) ? (isWifiNetwork(info) ? false
								: isNonImageMode) : true);
				contentWebView.loadDataWithBaseURL("file:///android_asset/",
						prepareHtml(isLargeFont, isNightMode, this.id, body),
						"text/html", "utf-8", null);
			}
			c.close();
		} else {
			LoadNewsService.loadNews(getActivity(), url);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> laoder) {

	}

	private static String prepareHtml(boolean large, boolean night, int newsId,
			String body) {
		final String line1 = "<!doctype html><html><head><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width,user-scalable=no\"><link href=\"news_qa.min.css\" rel=\"stylesheet\"><style>.headline .img-place-holder{height:0}</style><script src=\"img_replace.js\"></script></head><body className=\"%s\">";
		final String line2 = "<script src=\"large-font.js\"></script>";
		final String line3 = "<script src=\"night.js\"></script>";
		final String line4 = "<script>window.news_id=%s;</script><script src=\"http://daily.zhihu.com/js/zepto.min.js\"></script><script src=\"http://news-at.zhihu.com/js/hot-comments.ios.3.js\"></script>";
		final String line5 = "</body></html>";
		StringBuilder arguments = new StringBuilder("");
		if (large) {
			arguments.append("large ");
		}
		if (night) {
			arguments.append("night ");
		}

		StringBuilder id = new StringBuilder();
		id.append(String.format(line4, String.valueOf(newsId)));

		StringBuilder html = new StringBuilder(2048);
		html.append(String.format(line1, arguments.toString()));// line1
		html.append(body);
		if (large) {
			html.append(line2);
		}
		if (night) {
			html.append(line3);
		}
		html.append(id);// line4
		html.append(line5);

		return html.toString();
	}

	private NetworkInfo getNetworkInfo(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		return connMgr.getActiveNetworkInfo();
	}

	private boolean hasNetwork(NetworkInfo info) {
		return info != null && info.isConnected();
	}

	private boolean isWifiNetwork(NetworkInfo info) {
		return info.getType() == ConnectivityManager.TYPE_WIFI;
	}
}
