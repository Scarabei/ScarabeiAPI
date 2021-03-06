
package com.jfixby.scarabei.red.db.simple;

import java.io.IOException;
import java.util.Set;

import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.List;
import com.jfixby.scarabei.api.db.Entry;
import com.jfixby.scarabei.api.db.Table;
import com.jfixby.scarabei.api.db.TableSchema;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.api.json.JsonString;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.red.db.simple.srlzd.SrlzdEntry;
import com.jfixby.scarabei.red.db.simple.srlzd.SrlzdTable;

public class SimpleTable implements Table {
	final SimpleDB db;
	final String tableName;
	private final SimpleTableSchema schema;

	final List<Entry> entries = Collections.newList();
	private final File entriesFile;

	SimpleTable (final SimpleDB simpleDB, final String tableName, final SimpleTableSchema schema) throws IOException {
		this.db = simpleDB;
		this.tableName = tableName;
		this.schema = schema;
		final String fileName = this.db.getDBName().child(tableName).child("json").toString();
		this.entriesFile = this.db.storageFolder.child(fileName);
		if (this.entriesFile.exists()) {
			this.readStorage();
		} else {
			this.writeStorage();
		}

	}

	private void writeStorage () throws IOException {
		final SrlzdTable table = new SrlzdTable();
		for (final Entry e : this.entries) {
			table.entries.add(this.srlzEntry(e));
		}
		final JsonString json = Json.serializeToString(table);
		L.d("writing", this.entriesFile);
		this.entriesFile.writeString(json.toString());
	}

	private SrlzdEntry srlzEntry (final Entry e) {
		final SrlzdEntry s = new SrlzdEntry();
		for (final String key : this.schema.columns) {
			s.values.put(key, e.getValue(key));
		}
		return s;
	}

	private void readStorage () throws IOException {
		L.d("reading", this.entriesFile);
		final String data = this.entriesFile.readToString();
		final SrlzdTable srlz = Json.deserializeFromString(SrlzdTable.class, data);
		for (final SrlzdEntry e : srlz.entries) {
			final Entry entry = this.deSrlzEntry(e);
			this.entries.add(entry);
		}
	}

	private Entry deSrlzEntry (final SrlzdEntry e) throws IOException {
		final SimpleEntry entry = this.newEntry();
		for (final String k : e.values.keySet()) {
			entry.set(k, e.values.get(k));
		}
		return entry;
	}

	@Override
	public SimpleEntry newEntry () {
		return new SimpleEntry(this);
	}

	@Override
	public TableSchema getSchema () {
		return this.schema;
	}

	@Override
	public Collection<Entry> listAll () throws IOException {
		return this.entries.copy();
	}

	@Override
	public void addEntry (final Entry entry) throws IOException {
		this.entries.add(this.copyValues(entry));
		this.writeStorage();
	}

	@Override
	public void addEntries (final Collection<Entry> batch) throws IOException {
		for (final Entry entry : batch) {
			this.entries.add(this.copyValues(entry));
		}
		this.writeStorage();
	}

	@Override
	public void clear () throws IOException {
		this.entries.clear();
		this.writeStorage();
	}

	@Override
	public void replaceEntries (final List<Entry> batch) throws IOException {
		for (final Entry newe : batch) {
			for (final Entry stored : this.entries) {
				if (this.isTheSame(newe, stored)) {
					this.copyValues(newe, stored);
				}
			}
		}
		this.writeStorage();
	}

	private Entry copyValues (final Entry from, final Entry to) {
		final SimpleEntry in = (SimpleEntry)from;
		final Set<String> vars = in.values.keySet();
		for (final String key : vars) {
			final Object val = from.getValue(key);
			to.setValue(key, val);
		}
		return to;
	}

	private Entry copyValues (final Entry from) {
		return this.copyValues(from, this.newEntry());
	}

	@Override
	public Collection<Entry> findEntries (final String key, final Object value) throws IOException {
		final List<Entry> result = Collections.newList();
		for (final Entry e : this.entries) {
			final Object val = e.getValue(key);
			if (val.equals(value)) {
				result.add(this.copyValues(e));
			}
		}
		return result;
	}

	@Override
	public boolean deleteEntry (final String key, final Object value) throws IOException {
		final Collection<Entry> toDelete = this.findEntries(key, value);
		if (toDelete.size() == 0) {
			return false;
		}
		this.deleteEntries(toDelete);
		return true;
	}

	@Override
	public boolean deleteEntry (final Entry entry) throws IOException {
		for (final Entry e : this.entries) {
			if (this.isTheSame(e, entry)) {
				this.entries.remove(e);
				this.writeStorage();
				return true;
			}
		}
		return false;
	}

	private boolean isTheSame (final Entry x, final Entry y) {
		return x.equals(y);
	}

	@Override
	public void deleteEntries (final Collection<Entry> paramEntries) throws IOException {
		for (final Entry d : paramEntries) {
			for (final Entry e : this.entries) {
				if (this.isTheSame(e, d)) {
					this.entries.remove(e);
					break;
				}
			}
		}
		this.writeStorage();
	}

	@Override
	public String getName () {
		return this.tableName;
	}

	public void drop () throws IOException {
		this.entries.clear();
		this.entriesFile.delete();
	}
}
