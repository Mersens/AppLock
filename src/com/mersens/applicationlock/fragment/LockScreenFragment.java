package com.mersens.applicationlock.fragment;

import com.mersens.applicationlock.main.R;
import com.mersens.applicationlock.view.Lock9View;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class LockScreenFragment extends Fragment {
	private Lock9View lock9View;
	private  Toast mToast;
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_psd, null);
		initViews(v);
		return v;
	}

	private void initViews(View v) {
		lock9View = (Lock9View) v.findViewById(R.id.lock_9_view);
		lock9View.setCallBack(new Lock9View.CallBack() {
			@Override
			public void onFinish(String password) {
				doDealWithPsd(password);
			}
		});
	}
	private void doDealWithPsd(String psd) {

	}
	public void ShowToast(int text) {
		if (mToast == null) {
			mToast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(text);
		}
		mToast.show();
	}
	
}
