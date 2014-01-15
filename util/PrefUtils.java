package com.example.zhihudaily.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtils {
	private static final String PREF_NAME = "zhihu_pref";

	/** 保存TopStories的id */
	private static final String PREF_IDS = "ids";

	public static void saveTopStoriesIds(Context context, int[] ids) {
		SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);

		pref.edit().putString(PREF_IDS, ids2String(ids)).apply();
	}

	public static int[] loadTopStoriesIntArray(Context context) {
		SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		return string2Ids(pref.getString(PREF_IDS, ""));
	}

	public static String loadTopStories(Context context) {
		SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		return pref.getString(PREF_IDS, "");
	}

	private static String ids2String(int[] ids) {
		StringBuilder appender = new StringBuilder();
		for (int id : ids) {
			appender.append(id).append(",");
		}
		// 删除最后一个逗号
		appender.deleteCharAt(appender.length() - 1);

		return appender.toString();
	}

	private static int[] string2Ids(String ids) {
		String[] stringIds = ids.split(",", -1);
		int[] intIds = new int[stringIds.length];
		for (int i = 0; i < stringIds.length; i++) {
			intIds[i] = Integer.parseInt(stringIds[i]);
		}
		return intIds;
	}
}
