package com.example.zhihudaily.model;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class TopStory implements Parcelable {
	public final String image_source;
	public final String title;
	public final String url;
	public final String image;
	public final String share_url;
	public final String ga_prefix;
	public final int id;

	private TopStory(String image_source, String title, String url,
			String image, String share_url, String ga_prefix, int id) {
		this.image_source = image_source;
		this.title = title;
		this.url = url;
		this.image = image;
		this.share_url = share_url;
		this.ga_prefix = ga_prefix;
		this.id = id;
	}

	public static TopStory getTopStory(JSONObject obj) {
		final String image_source = obj.optString("image_source");
		final String title = obj.optString("title");
		final String url = obj.optString("url");
		final String image = obj.optString("image");
		final String share_url = obj.optString("share_url");
		final String ga_prefix = obj.optString("ga_prefix");
		final int id = obj.optInt("id");

		return new TopStory(image_source, title, url, image, share_url,
				ga_prefix, id);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(image_source);
		dest.writeString(title);
		dest.writeString(url);
		dest.writeString(image);
		dest.writeString(share_url);
		dest.writeString(ga_prefix);
		dest.writeInt(id);
	}

	public static final Parcelable.Creator<TopStory> CREATOR = new Parcelable.Creator<TopStory>() {

		@Override
		public TopStory createFromParcel(Parcel source) {
			return new TopStory(source);
		}

		@Override
		public TopStory[] newArray(int size) {
			return new TopStory[size];
		}
	};

	private TopStory(Parcel in) {
		this.image_source = in.readString();
		this.title = in.readString();
		this.url = in.readString();
		this.image = in.readString();
		this.share_url = in.readString();
		this.ga_prefix = in.readString();
		this.id = in.readInt();
	}

}
