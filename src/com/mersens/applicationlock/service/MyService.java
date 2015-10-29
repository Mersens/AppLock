package com.mersens.applicationlock.service;

import java.util.List;

import com.mersens.applicationlock.db.AppLockDao;
import com.mersens.applicationlock.db.AppLockDaoImpl;
import com.mersens.applicationlock.main.LockScreenActivity;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class MyService extends Service {
	private boolean flag;
	private String packName;
	private AppLockDao dao;
	private ActivityManager am;
	private String tempPackName;
	private Intent intent;
	private List<String> packNames;
	private MyReceiver receiver;
	public static final String CHANGED = "com.mersens.dbChanged";
	public static final String LOCK = "com.mersens.applock";
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		initDatas();
	}

	public void initDatas() {

		dao = new AppLockDaoImpl(getApplicationContext());
		receiver = new MyReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		intentFilter.addAction(Intent.ACTION_SCREEN_ON);
		intentFilter.addAction(CHANGED);
		intentFilter.addAction(LOCK);
		registerReceiver(receiver, intentFilter);
		intent=new Intent(MyService.this,LockScreenActivity.class);//
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		packNames = dao.findAll();
		flag = true;
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		lock();
	}

	@Override
	public void onDestroy() {
		destory();
		super.onDestroy();

	}

	public void destory() {
		unregisterReceiver(receiver);
		receiver = null;
		flag = false;
	}

	public class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(LOCK)) {
				tempPackName = intent.getStringExtra("packName");
			} else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				tempPackName = null;
				flag = false;
			} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
				flag = true;
				lock();
			} else if (intent.getAction().equals(CHANGED)) {
				packNames = dao.findAll();
			}
		}
	}

	private void lock() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (flag) {
					ComponentName runningActivity = am.getRunningTasks(1)
							.get(0).topActivity;
					packName = runningActivity.getPackageName();
					if (packNames.contains(packName)) {
						if (packName.equals(tempPackName)) {
						} else {
							tempPackName = null;
							intent.putExtra("packName", packName);
							startActivity(intent);
						}
					} else if (!packName.equals(getPackageName())) {
						tempPackName = null;
					}
				}
			}
		}).start();
	}
}
