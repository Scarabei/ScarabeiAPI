
package com.jfixby.red.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.jfixby.cmns.api.err.Err;
import com.jfixby.cmns.api.io.IO;
import com.jfixby.cmns.api.io.JavaInputStreamOperator;
import com.jfixby.cmns.api.java.ByteArray;

public class JavaFileInputStreamOperator implements JavaInputStreamOperator {

	private static final long SIZE_LIMIT = Integer.MAX_VALUE;
	private final File file;
	private InputStream is;

	public JavaFileInputStreamOperator (final File file) {
		this.file = file;
	}

	@Override
	public void closeStream () {
		IO.forceClose(this.is);
		this.is = null;
	}

	@Override
	public InputStream getJavaStream () throws IOException {
		if (this.is == null) {
			this.is = new FileInputStream(this.file);
			this.is = new BufferedInputStream(this.is);
		}
		return this.is;
	}

	@Override
	public boolean isReadAllSupported () {
		return false;
	}

	@Override
	public ByteArray readAll () throws IOException {
		Err.throwNotImplementedYet();
		return null;
	}

}