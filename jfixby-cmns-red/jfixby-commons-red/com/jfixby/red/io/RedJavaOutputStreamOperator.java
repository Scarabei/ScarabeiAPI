
package com.jfixby.red.io;

import java.io.IOException;
import java.io.OutputStream;

import com.jfixby.cmns.api.io.IO;
import com.jfixby.cmns.api.io.JavaOutputStreamOperator;
import com.jfixby.cmns.api.io.OutputStreamOpener;

public class RedJavaOutputStreamOperator implements JavaOutputStreamOperator {

	private OutputStream java_Output_stream;
	private final OutputStreamOpener opener;

	public RedJavaOutputStreamOperator (final OutputStreamOpener opener) {
		this.opener = opener;
	}

	@Override
	public void closeStream () {
		IO.forceClose(this.java_Output_stream);
		this.java_Output_stream = null;
	}

	@Override
	public OutputStream getJavaStream () throws IOException {
		if (this.java_Output_stream == null) {
			this.java_Output_stream = this.opener.open();
		}
		return this.java_Output_stream;
	}

}