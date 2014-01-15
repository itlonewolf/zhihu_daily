package com.example.zhihudaily.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Content {
	public final String body;
	public final String image_source;
	public final String title;
	public final String url;
	public final String image;
	public final String share_url;
	public final int id;
	public final String ga_prefix;
	public final List<Object> js;
	public final String thumbnail;
	public final List<String> css;

	private Content(String body, String image_source, String title, String url,
			String image, String share_url, int id, String ga_prefix,
			List<Object> js, String thumbnail, List<String> css) {
		this.body = body;
		this.image_source = image_source;
		this.title = title;
		this.url = url;
		this.image = image;
		this.share_url = share_url;
		this.id = id;
		this.ga_prefix = ga_prefix;
		this.js = js;
		this.thumbnail = thumbnail;
		this.css = css;
	}

	public static Content getContent(JSONObject obj) {
		final String body = obj.optString("body");
		final String image_source = obj.optString("image_source");
		final String title = obj.optString("title");
		final String url = obj.optString("url");
		final String image = obj.optString("image");
		final String share_url = obj.optString("share_url");
		final int id = obj.optInt("id");
		final String ga_prefix = obj.optString("ga_prefix");
		// FIXME
		final List<Object> js = new ArrayList<Object>();
		final String thumbnail = obj.optString("thumbnail");
		final List<String> css = new ArrayList<String>();
		JSONArray cssArray = obj.optJSONArray("css");
		for (int i = 0, length = cssArray.length(); i < length; i++) {
			css.add(cssArray.optString(i));
		}

		return new Content(body, image_source, title, url, image, share_url,
				id, ga_prefix, js, thumbnail, css);
	}

}
