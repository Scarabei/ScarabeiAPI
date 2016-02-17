package com.jfixby.red.desktop.collections;

import com.jfixby.cmns.api.collections.CollectionsComponent;
import com.jfixby.cmns.api.collections.List;
import com.jfixby.red.collections.RedCollections;

public class DesktopCollections extends RedCollections implements CollectionsComponent {

	@Override
	public <T> List<T> newList() {
		return new DesktopList<T>();
	}

}
