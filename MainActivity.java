package com.example.zhihudaily;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import cn.jpush.android.api.JPushInterface;

import com.example.zhihudaily.ui.LatestFragment;
import com.example.zhihudaily.ui.MenuFragment;

public class MainActivity extends FragmentActivity {
	private FragmentManager fragMgr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		JPushInterface.init(getApplicationContext()) ;
		JPushInterface.setDebugMode(true) ;
		// 必须写在setContentView()之前
		// requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);

		fragMgr = getSupportFragmentManager();

		fragMgr.beginTransaction().add(R.id.root_layout, new LatestFragment())
				.add(R.id.right_drawer, new MenuFragment()).commit();

	}

}
