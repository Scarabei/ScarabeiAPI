
package com.jfixby.scarabei.api.mobile;

public class MobileAppVersion {
	public String code;
	public String name;
	public String package_name;

	@Override
	public String toString () {
		return "MobileAppVersion [package_name=" + this.package_name + ", name=" + this.name + ", code=" + this.code + "]";
	}

}
