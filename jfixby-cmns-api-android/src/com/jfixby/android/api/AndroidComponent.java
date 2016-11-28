
package com.jfixby.android.api;

import com.jfixby.android.api.camera.AndroidCameraSetup;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.sys.SystemInfo;

public interface AndroidComponent {

	long getMaxHeapSize ();

	long getRecommendedHeapSize ();

	String getApplicationPrivateDirPathString ();

	AndroidCameraSetup getCameraSetup ();

	File getPrivateFolder ();

	File getCacheFolder ();

	DisplayMetrics getDisplayMetrics ();

	String getBrand ();

	String getModel ();

	String getHost ();

	String getVersionRelease ();

	AndroidAppVersion getAppVersion ();

	SystemInfo getSystemInfo ();

	String getSerial ();

	String getFingerPrint ();

	String getManufacturer ();

}
