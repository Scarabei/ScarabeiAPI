
package com.jfixby.red.android.filesystem;

import com.jfixby.android.api.Android;
import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.List;
import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.file.FileSystem;
import com.jfixby.cmns.api.file.LocalFileSystemComponent;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.util.JUtils;
import com.jfixby.cmns.api.util.path.AbsolutePath;
import com.jfixby.cmns.api.util.path.RelativePath;
import com.jfixby.red.filesystem.AbstractLocalFileSystem;

public class AndroidFileSystem extends AbstractLocalFileSystem implements LocalFileSystemComponent {

	String application_home_path_string = System.getProperty("user.dir");

	public AndroidFileSystem () {
// L.d("System.getProperty(user.dir)", System.getProperty("user.dir"));
		this.application_home_path_string = Android.getApplicationPrivateDirPathString();
		L.d("applicationPrivateDirPathString", this.application_home_path_string);
	}

	public static final String OS_SEPARATOR = "/";

	@Override
	public AndroidFile newFile (final java.io.File file) {
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
	public AndroidFile newFile (final String java_file_path) {
		final java.io.File f = new java.io.File(Debug.checkNull("java_file_path", java_file_path));
		return this.newFile(f);
	}

	@Override
	public AndroidFile newFile (final AbsolutePath<FileSystem> file_path) {
		if (file_path == null) {
			throw new Error("Filepath is null.");
		}
		if (file_path.getMountPoint() != this) {
			L.e("file_path", file_path);
			L.e("FileSystem", file_path.getMountPoint());
			throw new Error("Path does not belong to this filesystem: " + this);
		}
		return new AndroidFile(file_path, this);
	}

	@Override
	public String nativeSeparator () {
		return OS_SEPARATOR;
	}

	@Override
	public String toString () {
		return "AndroidFileSystem";
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
			throw new Error("Path does not belong to this filesystem: " + this);
		}
		final AndroidFile win_f = (AndroidFile)file;
		return win_f.getJavaFile();
	}

	@Override
	public String toAbsolutePathString (final AbsolutePath<FileSystem> file_path) {
		if (file_path == null) {
			throw new Error("Filepath is null.");
		}
		if (file_path.getMountPoint() != this) {
			L.e("file_path", file_path);
			L.e("FileSystem", file_path.getMountPoint());
			throw new Error("Path does not belong to this filesystem: " + this);
		}
// return new AndroidFile(file_path, this).getAbsoluteWindowsPathString();
		return new AndroidFile(file_path, this).getAbsolutePathString();
	}

	@Override
	public File WorkspaceFolder () {
		final String application_home_path_string = System.getProperty("user.dir");
		final java.io.File workspace_mount_point = (new java.io.File(application_home_path_string)).getParentFile();
		return this.newFile(workspace_mount_point);
	}

}
