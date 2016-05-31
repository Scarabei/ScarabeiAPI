
package com.jfixby.red.io;

import java.io.File;

import com.jfixby.cmns.api.file.FileInputStream;

public class JavaFileInputStream extends AbstractRedInputStream<JavaFileInputStreamOperator> implements FileInputStream {

	private final File file;

	public JavaFileInputStream (final File file) {
		super(new JavaFileInputStreamOperator(file));
		this.file = file;
// if (this.toString().contains("GenericFont.otf")) {
// throw new Error();
// }
	}

	@Override
	public String toString () {
		return "JavaFileInputStream=" + this.file + "";
	}

	@Override
	public long getFileSize () {
		return this.file.length();
	}

}
