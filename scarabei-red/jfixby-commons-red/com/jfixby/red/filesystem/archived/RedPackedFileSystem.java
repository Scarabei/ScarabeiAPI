
package com.jfixby.red.filesystem.archived;

import com.jfixby.cmns.api.err.Err;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.file.FileInputStream;
import com.jfixby.cmns.api.file.FileOutputStream;
import com.jfixby.cmns.api.file.FileSystem;
import com.jfixby.cmns.api.file.packing.CompressionSchema;
import com.jfixby.cmns.api.file.packing.PackedFileSystem;
import com.jfixby.cmns.api.util.path.AbsolutePath;
import com.jfixby.red.filesystem.AbstractFileSystem;

public class RedPackedFileSystem extends AbstractFileSystem implements FileSystem, PackedFileSystem {

	public static final String OS_SEPARATOR = "/";

	final private PackedFileSystemContent content;

	public RedPackedFileSystem (final CompressionSchema schema, final File archive) {
		this.content = new PackedFileSystemContent(schema, archive);
	}

	@Override
	public PackedFile newFile (final AbsolutePath<FileSystem> file_path) {
		if (file_path == null) {
			Err.reportError("Filepath is null.");
		}
		if (file_path.getMountPoint() != this) {
			Err.reportError("Path does not belong to this filesystem: " + file_path);
		}
		return new PackedFile(this, file_path);
	}

	@Override
	public FileOutputStream newFileOutputStream (final File output_file) {
		Err.reportError("Not supported (yet?)");
		return null;
	}

	@Override
	public FileOutputStream newFileOutputStream (final File output_file, final boolean append) {
		Err.reportError("Not supported (yet?)");
		return null;
	}

	@Override
	public FileInputStream newFileInputStream (final File input_file) {
		Err.reportError("Not supported (yet?)");
// final PackedFile v_file = (PackedFile)input_file;
// final FileData leaf = v_file.getContent();
// if (leaf == null) {
// throw new IOException("File not found: " + input_file);
// }
// return (FileInputStream)IO.newBufferInputStream(IO.newBuffer(leaf.getBytes()));
		return null;
	}

	@Override
	public String nativeSeparator () {
		return OS_SEPARATOR;
	}

	final private String name = "PackedFileSystem";

	public static String toNativePathString (final String string) {
		return string;
	}

	@Override
	public String toString () {
		return this.name;
	}

	public PackedFileSystemContent getContent () {
		return this.content;
	}

	public String getName () {
		return this.name;
	}

}