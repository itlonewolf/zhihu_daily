package com.example.zhihudaily;

import android.app.Application;
import android.preference.PreferenceManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ZhihuApp extends Application {

	private RequestQueue queue;

	@Override
	public void onCreate() {
		super.onCreate();

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

		queue = Volley.newRequestQueue(this);
	}

	public RequestQueue getRequestQueue() {
		return queue;
	}

}
