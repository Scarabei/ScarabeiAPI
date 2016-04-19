
package com.jfixby.red.java.gc;

import com.jfixby.cmns.api.err.Err;
import com.jfixby.cmns.api.java.gc.Bait;
import com.jfixby.cmns.api.java.gc.BaitInfo;
import com.jfixby.cmns.api.java.gc.GCFisher;
import com.jfixby.cmns.api.java.gc.GCFisherComponent;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.sys.settings.SystemSettings;

public class RedGCFisher implements GCFisherComponent {

	static long bait_id = -1;
	private long delayPeriod;

	@Override
	synchronized public BaitInfo throwBait (final long size_in_bytes) {
		bait_id++;
		final RedBait bait = new RedBait(bait_id, size_in_bytes, this.delayPeriod);
		final RedBaitInfo info = bait.getInfo();
		return info;
	}

	@Override
	public void onBaitCaptured (final Bait bait) {
		final String message = "GC bait captured " + bait.getInfo();
		L.e(message);
		// L.e(new Error(message));
		GCFisher.throwBait();
	}

	@Override
	public BaitInfo throwBait () {

		final long DEFAULT_BAIT_SIZE = SystemSettings.getLongParameter(GCFisher.DefaultBaitSize);
		return this.throwBait(DEFAULT_BAIT_SIZE);
	}

	private int parse (final String config_line) {
		try {
			final int size = Integer.parseInt(config_line.toLowerCase().replaceAll("mb", ""));
			return size;
		} catch (final Throwable e) {
			Err.reportError("Failed to read GCFisher.DefaultBaitSize: " + config_line);
		}
		return 1;
	}

	@Override
	public void setGCDelay (final long delayPeriod) {
		this.delayPeriod = delayPeriod;
	}

}
