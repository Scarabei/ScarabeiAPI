
package com.jfixby.cmns.api.file;

import java.io.IOException;

public interface FileConverter {

	public boolean convert (File inputFile, File outputFile) throws IOException;

}
