package com.jfixby.cmns.adopted.gdx.json;

abstract public class ReadOnlySerializer<T> implements JsonSerializer<T> {
	public void write (GdxJson json, T object, Class knownType) {
	}

	abstract public T read (GdxJson json, JsonValue jsonData, Class type);
}