package com.mersens.applicationlock.fragment;

import com.mersens.applicationlock.main.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public abstract class  SingleFragmentActivity extends FragmentActivity {
	
	protected abstract Fragment creatFragment();
	@Override
	protected void onCreate(Bundle arg0) { 
		super.onCreate(arg0);
		setContentView(R.layout.layout_container);
		initDatas();
	}

	@SuppressLint("Recycle")
	private void initDatas() {
		FragmentManager fm=getSupportFragmentManager();
		Fragment fragment=fm.findFragmentById(R.id.fragmentContainer);
		if(fragment==null){
			fragment=creatFragment();
			fm.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
		}
	}
}
