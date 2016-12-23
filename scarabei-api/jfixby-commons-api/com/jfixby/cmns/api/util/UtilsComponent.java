
package com.jfixby.cmns.api.util;

import com.jfixby.cmns.api.assets.ID;
import com.jfixby.cmns.api.collections.Collection;
import com.jfixby.cmns.api.collections.List;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.cmns.api.collections.Sequence;
import com.jfixby.cmns.api.collections.Set;
import com.jfixby.cmns.api.java.ByteArray;
import com.jfixby.cmns.api.util.path.AbsolutePath;
import com.jfixby.cmns.api.util.path.MountPoint;
import com.jfixby.cmns.api.util.path.RelativePath;

public interface UtilsComponent {

	RelativePath newRelativePath (String path_string);

	RelativePath newRelativePath (Collection<String> steps_list);

	RelativePath newRelativePath (java.util.List<String> steps_list);

	RelativePath newRelativePath ();

	<T extends MountPoint> AbsolutePath<T> newAbsolutePath (T mount_point, RelativePath relative);

	<T extends MountPoint> AbsolutePath<T> newAbsolutePath (T mount_point);

	<T> StateSwitcher<T> newStateSwitcher (T default_state);

	List<String> split (String input_string, String splitter);

	String newString (ByteArray data);

	<T> Set<T> intersectCollections (Collection<T> listA, Collection<T> listB);

	String truncated (String data, int begin_char, int end_char);

	boolean equalObjects (final Object a, final Object b);

	BinaryCode binaryCodeOf (int bits, int numberOfBits);

	EditableBinaryCode newBinaryCode ();

	ByteArray newByteArray (int size);

	ByteArray newByteArray (byte[] bytes);

	String newString (char[] chars);

	String newString (byte[] bytes);

	String newString (byte[] bytes, String encoding);

	String newString (ByteArray bytes, String encoding);

	String replaceAll (String input, Map<String, String> termsMapping);

	ProgressIndicator newProgressIndicator ();

	String prefix (String string, int offset);

	String wrapSequence (Sequence<String> seq, int size, String bracketLeft, String bracketRight);

	ID nameOf (Class<?> type);

}