package com.example.zhihudaily;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.example.zhihudaily.model.IdAndUrl;
import com.example.zhihudaily.ui.ContentFragment;

public class ContentActivity extends FragmentActivity {
	private ViewPager vp;

	public static final String POS_KEY = "pos_key";
	private int position;
	public static final String ID_AND_URL_KEY = "id_and_url_key";
	private ArrayList<IdAndUrl> idAndUrl = new ArrayList<IdAndUrl>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_content);

		Intent intent = getIntent();
		if (intent != null) {
			position = intent.getIntExtra(POS_KEY, 0);

			ArrayList<IdAndUrl> temp = intent
					.getParcelableArrayListExtra(ID_AND_URL_KEY);
			if (temp != null) {
				idAndUrl.addAll(temp);
			} else {
				throw new RuntimeException("传入数据无效");
			}
		}

		vp = (ViewPager) findViewById(R.id.content_vp);

		vp.setAdapter(new ContentAdapter());

		vp.setCurrentItem(position);
	}

	private class ContentAdapter extends FragmentStatePagerAdapter {

		public ContentAdapter() {
			super(getSupportFragmentManager());
		}

		@Override
		public Fragment getItem(int position) {
			return ContentFragment.getContentFragment(
					idAndUrl.get(position).url, idAndUrl.get(position).id);
		}

		@Override
		public int getCount() {
			return idAndUrl.size();
		}

	}

}
