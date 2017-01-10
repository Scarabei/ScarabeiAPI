
package com.jfixby.scarabei.red.ios.filesystem;

import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.List;
import com.jfixby.scarabei.api.debug.Debug;
import com.jfixby.scarabei.api.err.Err;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.file.FileSystem;
import com.jfixby.scarabei.api.file.LocalFileSystemComponent;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.util.JUtils;
import com.jfixby.scarabei.api.util.path.AbsolutePath;
import com.jfixby.scarabei.api.util.path.RelativePath;
import com.jfixby.scarabei.red.filesystem.AbstractLocalFileSystem;

public class iOSFileSystem extends AbstractLocalFileSystem implements LocalFileSystemComponent {

	final String application_home_path_string = System.getProperty("user.dir");

	public iOSFileSystem () {

	}

	public static final String OS_SEPARATOR = "/";

	@Override
	public iOSFile newFile (final java.io.File file) {
		return this.newFile(this.resolve(file));
	}

	private AbsolutePath<FileSystem> resolve (java.io.File file) {
		Debug.checkNull("file", file);
		file = file.getAbsoluteFile();
		final String path_string = file.getAbsolutePath();
		final List<String> steps = Collections.newList(path_string.split(OS_SEPARATOR));
		final RelativePath relative = JUtils.newRelativePath(steps);
		final AbsolutePath<FileSystem> path = JUtils.newAbsolutePath((FileSystem)this, relative);
		return path;
	}

	//
	@Override
	public iOSFile newFile (final String java_file_path) {
		final java.io.File f = new java.io.File(Debug.checkNull("java_file_path", java_file_path));
		return this.newFile(f);
	}

	@Override
	public iOSFile newFile (final AbsolutePath<FileSystem> file_path) {
		if (file_path == null) {
			Err.reportError("Filepath is null.");
		}
		if (file_path.getMountPoint() != this) {
			L.e("file_path", file_path);
			L.e("FileSystem", file_path.getMountPoint());
			Err.reportError("Path does not belong to this filesystem: " + this);
		}
		return new iOSFile(file_path, this);
	}

	@Override
	public String nativeSeparator () {
		return OS_SEPARATOR;
	}

	@Override
	public String toString () {
		return "iOSFileSystem";
	}

	@Override
	public File ApplicationHome () {
		return this.newFile(this.application_home_path_string);
	}

	@Override
	public java.io.File toJavaFile (final File file) {
		Debug.checkNull("file", file);
		final AbsolutePath<FileSystem> file_path = file.getAbsoluteFilePath();
		if (file_path.getMountPoint() != this) {
			L.e("file_path", file_path);
			L.e("FileSystem", file_path.getMountPoint());
			Err.reportError("Path does not belong to this filesystem: " + this);
		}
		final iOSFile win_f = (iOSFile)file;
		return win_f.getJavaFile();
	}

	@Override
	public String toAbsolutePathString (final AbsolutePath<FileSystem> file_path) {
		if (file_path == null) {
			Err.reportError("Filepath is null.");
		}
		if (file_path.getMountPoint() != this) {
			L.e("file_path", file_path);
			L.e("FileSystem", file_path.getMountPoint());
			Err.reportError("Path does not belong to this filesystem: " + this);
		}
		return new iOSFile(file_path, this).getAbsolutePathString();
	}

}
