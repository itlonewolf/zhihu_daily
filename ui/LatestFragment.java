package com.example.zhihudaily.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.NetworkImageView;
import com.example.zhihudaily.ContentActivity;
import com.example.zhihudaily.LoadNewsService;
import com.example.zhihudaily.R;
import com.example.zhihudaily.ZhihuApp;
import com.example.zhihudaily.model.IdAndUrl;
import com.example.zhihudaily.provider.ZhihuContract;
import com.example.zhihudaily.util.DBUtils;
import com.example.zhihudaily.util.PrefUtils;

/**
 * 显示新闻列表
 * 
 * @author ray
 * 
 */
public class LatestFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {
	private static final String TAG = "LatestFragment";

	private SimpleCursorAdapter adapter;

	private final int NEWS_LIST_LOADER_ID = 0;

	private ViewPager topStoryPager;
	private final int PAGER_ID = 1;

	private ArrayList<IdAndUrl> idAndUrl;

	private TopStoriesAdapter tsAdapter;
	
	private ImageLoader imageLoader;

	private final ContentObserver observer = new ContentObserver(null) {

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			getLoaderManager().getLoader(NEWS_LIST_LOADER_ID).forceLoad();
			Log.w(TAG, "onChange>>>数据改变了");
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapter = new LatestNewsAdapter(getActivity(), R.layout.news_item,
				null, new String[] { "title", "thumbnail" }, new int[] {
						R.id.title, R.id.img }, 0);
		// 注册观察者，接收提供者的更新通知
		getActivity().getContentResolver().registerContentObserver(
				ZhihuContract.CONTENT_URI_BASE, true, observer);

		Log.w(TAG, "LoadNewsService>>>启动下载服务");
		LoadNewsService.loadLatest(getActivity());

		// new ViewPager时必须提供一个>0的id
		topStoryPager = new ViewPager(getActivity());
		topStoryPager.setId(PAGER_ID);

		final int pagerHeightInDp = 200;// dp
		final float density = getResources().getDisplayMetrics().density;

		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				AbsListView.LayoutParams.MATCH_PARENT,
				Math.round((pagerHeightInDp * density)));
		topStoryPager.setLayoutParams(lp);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getView();
		// 如果显示此信息，则说明adapter中没有数据
		setEmptyText("万万没想到，加载不了数据！");

		// 必须在setAdapter()之前
		getListView().addHeaderView(topStoryPager);
		setListAdapter(adapter);

		setListShown(false);

		getLoaderManager().initLoader(NEWS_LIST_LOADER_ID, null, this);
		
		imageLoader = new ImageLoader(((ZhihuApp) getActivity()
				.getApplication()).getRequestQueue(), new ImageCache() {

			@Override
			public void putBitmap(String url, Bitmap bitmap) {

			}

			@Override
			public Bitmap getBitmap(String url) {
				return null;
			}
		});
		
		
	}

	//根据sharedpreference中的数据判断是否需要设置为夜间模式
	@Override
	public void onStart() {
		super.onStart();
		boolean isNightMode = PreferenceManager.getDefaultSharedPreferences(
				getActivity()).getBoolean("preference_key_dark_mode_open",
				false);

		if (isNightMode) {
			getView().setBackgroundColor(Color.GRAY);
		} else {
			getView().setBackgroundColor(Color.WHITE);
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle data) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+08"));
		// Calendar calendar = Calendar.getInstance(timezone) ;
		final String date = DBUtils.getDate(cal.getTimeInMillis());
		// Log.d("DATE", date);
		// 20131226
		return new CursorLoader(getActivity(),
				ZhihuContract.getQueryBtDateUri(date), null, null, null,
				"_id DESC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
		if (c != null && c.getCount() > 0) {
			// Log.d("CURSOR", "count=" + c.getCount());

			// Cursor oldCursor = adapter.swapCursor(c);
			// if (oldCursor != null) {
			// oldCursor.close();
			// }

			adapter.changeCursor(c);

			if (idAndUrl == null) {
				idAndUrl = new ArrayList<IdAndUrl>();
			}

			if (c.moveToFirst()) {
				idAndUrl.clear();
				final int idIndex = c.getColumnIndex("id");
				final int urlIndex = c.getColumnIndex("url");
				do {
					idAndUrl.add(new IdAndUrl(c.getInt(idIndex), c
							.getString(urlIndex)));
				} while (c.moveToNext());
			}

			if (isResumed()) {
				setListShown(true);
			} else {
				setListShownNoAnimation(true);
			}
			if (topStoryPager.getAdapter() == null) {
				tsAdapter = new TopStoriesAdapter();
				topStoryPager.setAdapter(tsAdapter);
			} else {
				tsAdapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.changeCursor(null);
	}

	private class TopStoriesAdapter extends FragmentStatePagerAdapter {
		private int[] ids;

		public TopStoriesAdapter() {
			super(getChildFragmentManager());
			ids = PrefUtils.loadTopStoriesIntArray(getActivity());
		}

		@Override
		public Fragment getItem(int position) {
			final int id = ids[position];

			return TopStoryFragment.getTopStoryFragment(id);
		}

		@Override
		public int getCount() {
			return ids.length;
		}

		@Override
		public void notifyDataSetChanged() {
			int[] newIntArray = PrefUtils.loadTopStoriesIntArray(getActivity());
			if (!Arrays.equals(ids, newIntArray)) {
				ids = newIntArray;
				super.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent contentIntent = new Intent(getActivity(), ContentActivity.class);
		contentIntent.putExtra(ContentActivity.POS_KEY,
				position - l.getHeaderViewsCount() - l.getFooterViewsCount());
		contentIntent.putParcelableArrayListExtra(
				ContentActivity.ID_AND_URL_KEY, idAndUrl);
		getActivity().startActivity(contentIntent);
	}

	private class LatestNewsAdapter extends SimpleCursorAdapter {
		private ViewBinder mViewBinder;

		public LatestNewsAdapter(Context context, int layout, Cursor c,
				String[] from, int[] to, int flags) {
			super(context, layout, c, from, to, flags);
		}

		/**
		 * 因为布局中的ImageView是NetworkImageView故此处需要复写SimpleCursorAdapter中的bindView方法
		 * ，其他不变，其实变得很少
		 */
		@Override
		public void bindView(View view, Context context, Cursor cursor) {

			final ViewBinder binder = mViewBinder;
			final int count = mTo.length;
			final int[] from = mFrom;
			final int[] to = mTo;

			for (int i = 0; i < count; i++) {
				final View v = view.findViewById(to[i]);
				Log.d(TAG, "<<>>" + to[i]);
				if (v != null) {
					boolean bound = false;
					if (binder != null) {
						bound = binder.setViewValue(v, cursor, from[i]);
					}

					if (!bound) {
						String text = cursor.getString(from[i]);
						if (text == null) {
							text = "";
						}

						if (v instanceof TextView) {
							setViewText((TextView) v, text);
						} else if (v instanceof ImageView) {
							Log.d(TAG, "》》加载了ImageView");
							// XXX
							((NetworkImageView) v).setImageUrl(text,
									imageLoader);
						} else {
							throw new IllegalStateException(
									v.getClass().getName()
											+ " is not a "
											+ " view that can be bounds by this SimpleCursorAdapter");
						}
					}
				}
			}

		}

	}

}
