package com.mersens.applicationlock.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.mersens.applicationlock.bean.AppInfo;

public class AppInfoDatas {
	private List<AppInfo> lists;
	private AppInfoDatas(Context context) {
		lists=getAppInfos(context);
	}
	private static AppInfoDatas mAppInfoDatas;

	public static synchronized AppInfoDatas getInstance(Context context) {
		if (mAppInfoDatas == null) {
			mAppInfoDatas = new AppInfoDatas(context);
		}
		return mAppInfoDatas;
	}

	private List<AppInfo> getAppInfos(Context context) {
		List<AppInfo> list = new ArrayList<AppInfo>();
		PackageManager mPackageManager = context.getPackageManager();
		List<PackageInfo> infos = mPackageManager.getInstalledPackages(0);
		for (PackageInfo info : infos) {
			AppInfo bean = new AppInfo();
			String packName = info.packageName;
			if (!packName.equals(context.getPackageName())) {
				String name = (String) info.applicationInfo
						.loadLabel(mPackageManager);
				Drawable appIcon = info.applicationInfo
						.loadIcon(mPackageManager);
				boolean inRom;
				boolean userApp;
				int flag = info.applicationInfo.flags;
				if ((flag & ApplicationInfo.FLAG_SYSTEM) == 0) {
					userApp = true;
				} else {
					userApp = false;
				}
				if ((flag & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
					inRom = true;
				} else {
					inRom = false;
				}
				bean.setAppIcon(appIcon);
				bean.setInRom(inRom);
				bean.setName(name);
				bean.setPackName(packName);
				bean.setUserApp(userApp);
				list.add(bean);
			}
		}

		return list;
	}

	public  List<AppInfo> getSystemAppInfo() {
		List<AppInfo> systemInfoList = new ArrayList<AppInfo>();
		for (AppInfo info : lists) {
			if (!info.isUserApp()) {
				systemInfoList.add(info);
			}
		}
		return systemInfoList;
	}

	public List<AppInfo> getUserAppInfo() {
		List<AppInfo> userInfoList = new ArrayList<AppInfo>();
		for (AppInfo info : lists) {
			if (info.isUserApp()) {
				userInfoList.add(info);
			}
		}
		return userInfoList;
	}

}
