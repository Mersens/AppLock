package com.mersens.applicationlock.main;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.mersens.applicationlock.fragment.SingleFragmentActivity;
import com.mersens.applicationlock.fragment.UnlockPsdFragment;

public class LockScreenActivity extends SingleFragmentActivity{
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent=new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addCategory("android.intent.category.MONKEY");
		startActivity(intent);
	}
	@Override
	protected Fragment creatFragment() {
		return new UnlockPsdFragment();
	}

}
