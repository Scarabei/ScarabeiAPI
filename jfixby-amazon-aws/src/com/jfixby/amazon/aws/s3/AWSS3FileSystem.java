
package com.jfixby.amazon.aws.s3;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.List;
import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.err.Err;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.file.FileInputStream;
import com.jfixby.cmns.api.file.FileOutputStream;
import com.jfixby.cmns.api.file.FileSystem;
import com.jfixby.cmns.api.io.IO;
import com.jfixby.cmns.api.io.InputStream;
import com.jfixby.cmns.api.io.InputStreamOpener;
import com.jfixby.cmns.api.java.ByteArray;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.util.JUtils;
import com.jfixby.cmns.api.util.path.AbsolutePath;
import com.jfixby.cmns.api.util.path.RelativePath;
import com.jfixby.red.filesystem.AbstractFileSystem;

public class AWSS3FileSystem extends AbstractFileSystem implements FileSystem {

	private final String bucketName;
	private final AmazonS3Client s3;
	final private String toString;

	public AWSS3FileSystem (final AWSS3FileSystemConfig specs) {
		this.bucketName = Debug.checkNull("getBucketName()", specs.getBucketName());
		this.s3 = new AmazonS3Client();
		this.toString = "S3BucketFileSystem[" + this.bucketName + "]";
	}

	public static final String OS_SEPARATOR = "/";

	@Override
	public S3File newFile (final AbsolutePath<FileSystem> file_path) {
		if (file_path == null) {
			throw new Error("Filepath is null.");
		}
		if (file_path.getMountPoint() != this) {
			L.e("file_path", file_path);
			L.e("FileSystem", this.ROOT());
			throw new Error("Path does not belong to this filesystem: " + file_path);
		}
		return new S3File(file_path, this);
	}

	@Override
	public FileOutputStream newFileOutputStream (final File output_file) {
		throw new Error("Read-only file system!");
	}

	@Override
	public FileOutputStream newFileOutputStream (final File output_file, final boolean append) {
		throw new Error("Read-only file system!");
	}

	@Override
	public FileInputStream newFileInputStream (final File input_file) {
		if (input_file == null) {
			throw new Error("Input file is null.");
		}
		if (input_file.getFileSystem() != this) {
			throw new Error("Input file does not belong to this filesystem: " + input_file);
		}

		return new S3FileInputStream((S3File)input_file);
	}

	@Override
	public String nativeSeparator () {
		return OS_SEPARATOR;
	}

	@Override
	public String toString () {
		return this.toString;
	}

	@Override
	public boolean isReadOnlyFileSystem () {
		return false;
	}

	public String getBucketName () {
		return this.bucketName;
	}

	public AmazonS3Client getAmazonS3Client () {
		return this.s3;
	}

	public S3ObjectInfo retrieveInfo (final RelativePath relative) {
		if (relative.isRoot()) {
			return this.retrieveRootInfo();
		}

		final S3ObjectInfo parentInfo = this.retrieveFolderInfo(relative.parent());
		final boolean isFolder = parentInfo.listSubfolders().contains(relative.getLastStep());
		final boolean isFile = parentInfo.listFiles().contains(relative.getLastStep());
		final boolean exists = isFolder || isFile;

		if (!exists) {
			return null;
		}

		if (isFolder) {
			return this.retrieveFolderInfo(relative);
		}

		return this.retrieveFileInfo(relative);

	}

	private S3ObjectInfo retrieveFileInfo (final RelativePath relative) {
		final ListObjectsRequest request = new ListObjectsRequest().withBucketName(this.bucketName);
		request.withPrefix(relative.toString());
		request.setDelimiter(RelativePath.SEPARATOR);
		final ObjectListing objectListing = this.s3.listObjects(request);
		final S3ObjectInfo info = new S3ObjectInfo(objectListing.getObjectSummaries().get(0));
		return info;
	}

	private S3ObjectInfo retrieveFolderInfo (final RelativePath relative) {
		if (relative.isRoot()) {
			return this.retrieveRootInfo();
		}
		final S3ObjectInfo info;

		final ListObjectsRequest request = new ListObjectsRequest().withBucketName(this.bucketName);
		final String prefix = relative.toString() + RelativePath.SEPARATOR;

// L.d("prefix", prefix);
		request.withPrefix(prefix);
		request.setDelimiter(RelativePath.SEPARATOR);
		final ObjectListing objectListing = this.s3.listObjects(request);

		final List<String> prefixes = Collections.newList(objectListing.getCommonPrefixes());
//

//
		final List<S3ObjectSummary> summs = Collections.newList(objectListing.getObjectSummaries());
//

		if (summs.size() == 0) {
			prefixes.print("prefixes:" + prefix);
			summs.print("summs");
			Err.reportError("Failed to read folder: " + prefix);
		}

		info = new S3ObjectInfo(summs.getElementAt(0));
// Sys.exit();

		final List<String> files = Collections.newList();
// final List<String> prefixes = Collections.newList();

		Collections.scanCollection(summs, (summ, i) -> {
			final String key = summ.getKey();
			final RelativePath keyPath = JUtils.newRelativePath(key);
			if (keyPath.equals(relative)) {
				return;
			}
			if (key.endsWith(RelativePath.SEPARATOR)) {
				prefixes.add(key);
			} else {
				files.add(key);
			}
		});
		info.addSubFolders(prefixes);
		info.addFiles(files);
// files.print("files:" + relative);

// info.print(relative + "");
		return info;
	}

	private S3ObjectInfo retrieveRootInfo () {
		final S3ObjectInfo info = new S3ObjectInfo();
		final ListObjectsRequest request = new ListObjectsRequest().withBucketName(this.bucketName);
		request.withPrefix("");
		request.setDelimiter(RelativePath.SEPARATOR);
		final ObjectListing objectListing = this.s3.listObjects(request);
// for (final S3ObjectSummary sum : objectListing.getObjectSummaries()) {
// L.d("", sum.getKey());
// }

		final List<String> prefixes = Collections.newList(objectListing.getCommonPrefixes());
		info.addSubFolders(prefixes);
		final List<S3ObjectSummary> summs = Collections.newList(objectListing.getObjectSummaries());
		final List<String> files = Collections.newList();
		Collections.convertCollection(summs, files, S3ObjectSummary -> new S3ObjectInfo(S3ObjectSummary).path.getLastStep());
		info.addFiles(files);
// info.print("root");
		return info;
	}

	boolean createS3Folder (final RelativePath relative) {
		if (relative.isRoot()) {
			return true;
		}
		RelativePath current = JUtils.newRelativePath();
		final List<String> steps = relative.steps();
		for (int i = 0; i < steps.size(); i++) {
			current = current.child(steps.getElementAt(i));
			this.makeFolder(current + RelativePath.SEPARATOR);
		}
		return true;
	}

	void makeFolder (final String s3Key) {
		L.d("makeFolder", s3Key);

		// create meta-data for your folder and set content-length to 0
		final ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0);
		// create empty content
		final ByteArrayInputStream emptyContent = new ByteArrayInputStream(new byte[0]);
		// create a PutObjectRequest passing the folder name suffixed by /
		final PutObjectRequest putObjectRequest = new PutObjectRequest(this.bucketName, s3Key, emptyContent, metadata);
		// send request to S3 to create folder
		final PutObjectResult result = this.s3.putObject(putObjectRequest);

	}

	void writeData (final RelativePath relative, final ByteArray bytes) {

		final String s3Key = relative.toString();

		L.d("writeData", s3Key);

		// create meta-data for your folder and set content-length to 0
		final ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(bytes.size());
// create empty content
		final ByteArrayInputStream emptyContent = new ByteArrayInputStream(bytes.toArray());
		// create a PutObjectRequest passing the folder name suffixed by /
		final PutObjectRequest putObjectRequest = new PutObjectRequest(this.bucketName, s3Key, emptyContent, metadata);
		// send request to S3 to create folder
		final PutObjectResult result = this.s3.putObject(putObjectRequest);

	}

	public byte[] readData (final String s3Key) throws IOException {

		final String bucketName = this.bucketName;
		final AmazonS3Client s3 = this.s3;

		final InputStreamOpener opener = new InputStreamOpener() {
			@Override
			public java.io.InputStream open () throws IOException {
				final GetObjectRequest request = new GetObjectRequest(bucketName, s3Key);
				final S3Object object = s3.getObject(request);
				final S3ObjectInputStream is = object.getObjectContent();
				return is;
			}
		};

		final InputStream stream = IO.newInputStream(opener);
		stream.open();
		final ByteArray data = stream.readAll();
		stream.close();
		return data.toArray();
	}

	void deleteS3File (final RelativePath relative) {

		this.s3.deleteObject(this.bucketName, relative.toString());
	}

	void deleteS3Folder (final RelativePath relative) {

		this.s3.deleteObject(this.bucketName, relative + RelativePath.SEPARATOR);
	}

}