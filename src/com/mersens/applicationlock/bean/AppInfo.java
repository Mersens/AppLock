package com.mersens.applicationlock.bean;

import android.graphics.drawable.Drawable;

public class AppInfo {
	private String name;
	private String packName;
	private Drawable appIcon;
	private boolean inRom;
	private boolean userApp;
	
	public AppInfo(){
		
	}
	
	@Override
	public String toString() {
		return "AppInfo [name=" + name + ", packName=" + packName
				+ ", appIcon=" + appIcon + ", inRom=" + inRom + ", userApp="
				+ userApp + "]";
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPackName() {
		return packName;
	}
	public void setPackName(String packName) {
		this.packName = packName;
	}
	public Drawable getAppIcon() {
		return appIcon;
	}
	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}
	public boolean isInRom() {
		return inRom;
	}
	public void setInRom(boolean inRom) {
		this.inRom = inRom;
	}
	public boolean isUserApp() {
		return userApp;
	}
	public void setUserApp(boolean userApp) {
		this.userApp = userApp;
	}


}
