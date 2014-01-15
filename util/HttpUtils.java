package com.example.zhihudaily.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import android.os.Build;
import android.util.Log;

/**访问网络
 * @author yangxiaoyi
 *	
 */
public class HttpUtils {
	private static final String TAG = "HttpUtils";
	/** 连接超时时间 */
	public static final int CONNECT_TIMEOUT = 10 * 1000;
	/** 读取超时 */
	public static final int READ_TIMEOUT = 10 * 1000;

	private static StringBuilder appender = null;
	private static String USER_AGENT = null;
	private static String ZA = null;

	static {
		appender = new StringBuilder();

		appender.append("ZhihuApi/1.0.0-beta ");
		appender.append("(Linux; Android ");
		appender.append(Build.VERSION.RELEASE);
		appender.append("; ");
		appender.append(Build.MODEL);
		appender.append(")");

		USER_AGENT = appender.toString();

		// clear
		appender.setLength(0);
		appender.trimToSize();

		appender.append("OS=");
		appender.append("Android ");
		appender.append(Build.VERSION.RELEASE);
		appender.append("&Platform=");
		appender.append(Build.MODEL);
		ZA = appender.toString();
	}

	/**用get方法获取网络数据
	 * @param url
	 * @param headers
	 * @return
	 */
	public static byte[] get(String url, Map<String, String> headers) {
		HttpURLConnection conn = null;

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("Accept-Encoding", "gzip");
		map.put("User-Agent", USER_AGENT);
		map.put("x-api-version", "2.0");
		map.put("x-app-version", "1.6.3");
		map.put("x-os", "Android " + Build.VERSION.RELEASE);
		map.put("x-device", Build.MODEL);
		map.put("za", ZA);

		if (headers != null && !headers.isEmpty()) {
			map.putAll(headers);
		}

		InputStream in = null;
		ByteArrayOutputStream out = null;

		try {
			URL u = new URL(url);
			conn = (HttpURLConnection) u.openConnection();

			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.setConnectTimeout(CONNECT_TIMEOUT);
			conn.setReadTimeout(READ_TIMEOUT);

			for (Map.Entry<String, String> header : map.entrySet()) {
				conn.setRequestProperty(header.getKey(), header.getValue());
			}

			final int code = conn.getResponseCode();
			if (code == HttpURLConnection.HTTP_OK) {
				final String contentEncoding = conn.getContentEncoding();
				boolean isCompress = contentEncoding.contains("gzip");
				if (isCompress) {
					// 压缩
					in = new GZIPInputStream(conn.getInputStream());
				} else {
					in = conn.getInputStream();
				}

				int length = conn.getContentLength();
				if (length != -1) {
					out = new ByteArrayOutputStream(isCompress ? length * 3
							: length);
				} else {
					out = new ByteArrayOutputStream(1024 * 10);
				}

				byte[] buf = new byte[1024];
				while ((length = in.read(buf)) != -1) {
					out.write(buf, 0, length);
				}

				return out.toByteArray();
			} else {
				Log.e(TAG, "code=" + code);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return new byte[0];
	}
}
