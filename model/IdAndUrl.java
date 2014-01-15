package com.example.zhihudaily.model;

import android.os.Parcel;
import android.os.Parcelable;

public class IdAndUrl implements Parcelable {
	public final int id;
	public final String url;

	public IdAndUrl(int id, String url) {
		this.id = id;
		this.url = url;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(url);
	}

	public static final Parcelable.Creator<IdAndUrl> CREATOR = new Parcelable.Creator<IdAndUrl>() {

		@Override
		public IdAndUrl createFromParcel(Parcel source) {
			return new IdAndUrl(source);
		}

		@Override
		public IdAndUrl[] newArray(int size) {
			return new IdAndUrl[size];
		}

	};

	private IdAndUrl(Parcel in) {
		id = in.readInt();
		url = in.readString();
	}

}
