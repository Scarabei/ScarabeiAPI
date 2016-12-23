
package com.jfixby.cmns.adopted.gdx.json.red;

import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.json.JsonString;

public class GdxJsonString implements JsonString {

	@Override
	public int hashCode () {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.raw_json_string == null) ? 0 : this.raw_json_string.hashCode());
		return result;
	}

	@Override
	public boolean equals (final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final GdxJsonString other = (GdxJsonString)obj;
		if (this.raw_json_string == null) {
			if (other.raw_json_string != null) {
				return false;
			}
		} else if (!this.raw_json_string.equals(other.raw_json_string)) {
			return false;
		}
		return true;
	}

	final private String raw_json_string;

	public GdxJsonString (final String raw_json_string) {
		Debug.checkNull("raw_json_string", raw_json_string);
		this.raw_json_string = raw_json_string;
	}

	@Override
	public char[] toCharArray () {
		return this.raw_json_string.toCharArray();
	}

	@Override
	public String toString () {
		return this.raw_json_string;
	}

}