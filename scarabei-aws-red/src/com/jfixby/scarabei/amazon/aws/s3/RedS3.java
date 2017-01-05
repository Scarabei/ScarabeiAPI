
package com.jfixby.scarabei.amazon.aws.s3;

import com.jfixby.scarabei.aws.api.S3;
import com.jfixby.scarabei.aws.api.S3FileSystem;
import com.jfixby.scarabei.aws.api.S3FileSystemConfig;

public class RedS3 implements S3 {

	@Override
	public S3FileSystemConfig newFileSystemConfig () {
		return new RedS3FileSystemConfig();
	}

	@Override
	public S3FileSystem newFileSystem (final S3FileSystemConfig specs) {
		return new RedS3FileSystem(specs);
	}

}