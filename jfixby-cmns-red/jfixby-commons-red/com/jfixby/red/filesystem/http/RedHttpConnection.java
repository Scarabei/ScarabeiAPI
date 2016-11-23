
package com.jfixby.red.filesystem.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.io.IO;
import com.jfixby.cmns.api.net.http.HttpConnection;
import com.jfixby.cmns.api.net.http.HttpConnectionInputStream;
import com.jfixby.cmns.api.net.http.HttpConnectionOutputStream;
import com.jfixby.cmns.api.net.http.HttpConnectionSpecs;
import com.jfixby.cmns.api.net.http.HttpURL;
import com.jfixby.cmns.api.net.http.METHOD;

public class RedHttpConnection implements HttpConnection {

	private final HttpURL url;
	private final boolean use_agent;
	final Map<String, String> requestProperties = Collections.newMap();

	private HttpURLConnection java_connection;
	private URL java_url;
	private RedHttpConnectionInputStream red_input_stream;
	private RedHttpConnectionOutputStream red_output_stream;
// private final int code = -1;
	private boolean doInput = true;
	private boolean doOutput = true;
	METHOD method = METHOD.GET;
	private boolean useCaches;
	private boolean defaultUseCaches;
	private boolean octetStream;
	private int connectionTimeout;
	private int readTimeout;

	public RedHttpConnection (final HttpConnectionSpecs specs) {
		this.url = specs.getURL();
		this.use_agent = specs.getUseAgent();
		this.requestProperties.putAll(specs.listRequestProperties());
		this.doInput = specs.doInput();
		this.doOutput = specs.doOutput();
		this.method = specs.getMethod();
		this.useCaches = specs.useCaches();
		this.defaultUseCaches = specs.defaultUseCaches();
		this.octetStream = specs.octetStream();
		this.connectionTimeout = specs.getConnectionTimeout();
		this.readTimeout = specs.getReadTimeout();

	}

	public RedHttpConnection (final HttpURL url, final boolean use_agent) {
		this.url = url;
		this.use_agent = use_agent;
	}

	@Override
	public void open () throws IOException {
		this.java_url = new java.net.URL(this.url.getURLString());

		this.java_connection = (HttpURLConnection)this.java_url.openConnection();

		this.java_connection.setRequestMethod(this.method.toJavaString());
		this.java_connection.setUseCaches(this.useCaches);
		this.java_connection.setDefaultUseCaches(this.defaultUseCaches);
		this.java_connection.setDoInput(this.doInput);
		this.java_connection.setDoOutput(this.doOutput);

// this.code = this.java_connection.getResponseCode();
		if (this.use_agent) {
			this.java_connection.addRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
			this.java_connection.addRequestProperty("Accept-Language", "ru-RU,ru;q=0.8");

		}

		if (this.octetStream) {
			this.java_connection.setRequestProperty("Content-Type", "application/octet-stream");
		}

		for (final String key : this.requestProperties.keys()) {
			this.java_connection.addRequestProperty(key, this.requestProperties.get(key));
		}

		this.java_connection.setConnectTimeout(this.connectionTimeout);
		this.java_connection.setReadTimeout(this.readTimeout);

		this.java_connection.connect();
	}

	@Override
	public HttpConnectionInputStream getInputStream () {
		if (this.red_input_stream == null) {
			this.red_input_stream = new RedHttpConnectionInputStream(this.java_connection);
		}
		return this.red_input_stream;
	}

	@Override
	public HttpConnectionOutputStream getOutputStream () {
		Debug.checkTrue("Connection is not open " + this.url, this.java_connection != null);
		if (this.red_output_stream == null) {
			this.red_output_stream = new RedHttpConnectionOutputStream(this.java_connection);
		}
		return this.red_output_stream;
	}

	@Override
	public void close () {
		IO.forceClose(this.red_output_stream);
		IO.forceClose(this.red_input_stream);
	}

	@Override
	public int getResponseCode () throws IOException {
		return this.java_connection.getResponseCode();
	}

}
