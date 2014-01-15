package com.example.zhihudaily.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Latest implements Parcelable {
	public final String date;
	public final List<News> news;
	public final boolean is_today;
	public final List<TopStory> top_stories;
	public final String display_date;

	private Latest(String date, List<News> news, boolean is_today,
			List<TopStory> top_stories, String display_date) {
		this.date = date;
		this.news = news;
		this.is_today = is_today;
		this.top_stories = top_stories;
		this.display_date = display_date;
	}

	public static Latest getLatest(JSONObject obj) {
		//
		final String date = obj.optString("date");
		//
		final JSONArray newsArray = obj.optJSONArray("news");
		final ArrayList<News> news = new ArrayList<News>();
		if (newsArray != null) {
			for (int i = 0, length = newsArray.length(); i < length; i++) {
				JSONObject newsObj = newsArray.optJSONObject(i);
				if (newsObj != null) {
					news.add(News.getNews(newsObj));
				}
			}
		}
		//
		final boolean is_today = obj.optBoolean("is_today");
		//
		final JSONArray tsArray = obj.optJSONArray("top_stories");
		final ArrayList<TopStory> top_stories = new ArrayList<TopStory>();
		if (tsArray != null) {
			for (int i = 0, length = tsArray.length(); i < length; i++) {
				JSONObject tsObj = tsArray.optJSONObject(i);
				if (tsObj != null) {
					top_stories.add(TopStory.getTopStory(tsObj));
				}
			}
		}
		//
		final String display_date = obj.optString("display_date");

		return new Latest(date, news, is_today, top_stories, display_date);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(date);
		dest.writeList(news);
		dest.writeInt(is_today ? 1 : 0);
		dest.writeList(top_stories);
		dest.writeString(display_date);
	}

	public static final Parcelable.Creator<Latest> CREATOR = new Parcelable.Creator<Latest>() {

		@Override
		public Latest createFromParcel(Parcel source) {
			return new Latest(source);
		}

		@Override
		public Latest[] newArray(int size) {
			return new Latest[size];
		}

	};

	private Latest(Parcel in) {
		this.date = in.readString();
		this.news = new ArrayList<News>();
		ArrayList<News> newsTemp = new ArrayList<News>();
		in.readList(newsTemp, null);
		this.news.addAll(newsTemp);
		this.is_today = in.readInt() == 1;
		this.top_stories = new ArrayList<TopStory>();
		ArrayList<TopStory> topTemp = new ArrayList<TopStory>();
		in.readList(topTemp, null);
		top_stories.addAll(topTemp);
		this.display_date = in.readString();
	}

}
