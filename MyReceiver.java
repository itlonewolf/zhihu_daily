package com.example.zhihudaily;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.zhihudaily.ui.Demo;

import cn.jpush.android.api.JPushInterface;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {

	private static final String TAG = "MyReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();
		final Bundle bundle = intent.getExtras();
		String data = null;
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context) ;
		builder.setContentText("通知") ;
		builder.setSmallIcon(R.drawable.ic_launcher) ;
		Intent intent2 = new Intent(context, Demo.class) ;
		builder.setContentIntent(PendingIntent.getActivity(context, 0, intent2, PendingIntent.FLAG_ONE_SHOT)) ;

		if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(action)) {
			Log.d(TAG, "收到的是通知");
			data = bundle.getString(JPushInterface.EXTRA_EXTRA);

		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(action)) {
			Log.d(TAG, "收到的是消息");
			data = bundle.getString(JPushInterface.EXTRA_EXTRA);
		}

		if (!TextUtils.isEmpty(data)) {
			try {
				final JSONObject json = new JSONObject(data);
				String url = json.getString("url") ;
				Log.i(TAG, "成功拿到url>>>" + url) ;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		else {
			Log.w(TAG, "没有拿到url") ;
		}

	}

}
