
package com.jfixby.cmns.api.lang;

import java.util.Arrays;

import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.java.ByteArray;

public class RedByteArray implements ByteArray {

	final private byte[] bytes;

	@Override
	public String toString () {
		return "ByteArray(" + this.bytes.length + ") " + Arrays.toString(this.bytes) + "";
	}

	public RedByteArray (final byte[] bytes) {
		Debug.checkNull("bytes", bytes);
		this.bytes = bytes;
	}

	public RedByteArray (final int size) {
		this.bytes = new byte[size];
	}

	@Override
	public byte[] toArray () {
		return this.bytes;
	}

	@Override
	public long size () {
		return this.bytes.length;
	}

	@Override
	public int getByte (final int i) {
		return this.bytes[i];
	}

}