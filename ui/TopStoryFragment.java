package com.example.zhihudaily.ui;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.NetworkImageView;
import com.example.zhihudaily.R;
import com.example.zhihudaily.ZhihuApp;
import com.example.zhihudaily.provider.ZhihuContract;

public class TopStoryFragment extends Fragment implements
		LoaderCallbacks<Cursor> {

	private final int TOP_STORY_LOADER_ID = 0;

	private int id;

	private static final String ID_KEY = "id_key";

	private TextView titleView;
	private NetworkImageView imgView;

	private ImageLoader imgLoader;

	public static TopStoryFragment getTopStoryFragment(int id) {
		Bundle data = new Bundle();
		data.putInt(ID_KEY, id);
		TopStoryFragment tsf = new TopStoryFragment();
		tsf.setArguments(data);
		return tsf;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle data = getArguments();
		if (data == null) {
			throw new RuntimeException("miss id");
		}
		id = data.getInt(ID_KEY);

		imgLoader = new ImageLoader(
				((ZhihuApp) getActivity().getApplication()).getRequestQueue(),
				new ImageCache() {

					@Override
					public void putBitmap(String url, Bitmap bitmap) {

					}

					@Override
					public Bitmap getBitmap(String url) {
						return null;
					}
				});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.page_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		imgView = (NetworkImageView) view.findViewById(R.id.page_img);
		titleView = (TextView) view.findViewById(R.id.page_title);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		this.getLoaderManager().initLoader(TOP_STORY_LOADER_ID, null, this);
	}

	@Override
	public void onStop() {
		super.onStop();
		imgView.setImageUrl(null, imgLoader);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle data) {
		return new CursorLoader(getActivity(),
				ZhihuContract.getQueryUri(this.id), null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
		if (c != null) {
			while (c.moveToNext()) {
				final int titleIndex = c.getColumnIndex("title");
				final int imageIndex = c.getColumnIndex("image");
				final String title = c.getString(titleIndex);
				final String image = c.getString(imageIndex);
				titleView.setText(title);
				imgView.setImageUrl(image, imgLoader);
			}
			c.close();
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {

	}
}
