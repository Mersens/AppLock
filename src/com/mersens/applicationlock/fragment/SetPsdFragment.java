package com.mersens.applicationlock.fragment;

import java.util.HashMap;
import java.util.Map;

import com.mersens.applicationlock.main.R;
import com.mersens.applicationlock.utils.MD5Utils;
import com.mersens.applicationlock.utils.SharePreferenceUtil;
import com.mersens.applicationlock.view.Lock9View;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SetPsdFragment extends Fragment {
	private Lock9View lock9View;
	private TextView textview;
	private boolean isFirst = true;
	private boolean isSecond = false;
	private Map<String, String> map = null;
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
		textview = (TextView) v.findViewById(R.id.textView);
		textview.setText(R.string.set_psd);
		lock9View = (Lock9View) v.findViewById(R.id.lock_9_view);
		lock9View.setCallBack(new Lock9View.CallBack() {
			@Override
			public void onFinish(String password) {
				doDealWithPsd(password);
			}
		});
	}
	private void doDealWithPsd(String psd) {
		if (psd.length() <= 3) {
			textview.setText(R.string.no_length);
			return;
		}
		if (isFirst && !isSecond) {
			isFirst = false;
			isSecond = true;
			map = new HashMap<String, String>();
			map.put("password", psd);
			textview.setText(R.string.set_psd_again);
			ShowToast(R.string.set_psd_again);
			return;
		}
		if (isSecond && !isFirst) {
			if (psd.equals(map.get("password"))) {
				ShowToast(R.string.set_psd_success);
				SharePreferenceUtil.getInstance(
						getActivity().getApplicationContext()).setUserPsd(MD5Utils.md5Encode(psd));
				textview.setText(R.string.set_psd_success);
				getActivity().finish();
			} else {
				textview.setText(R.string.difference_psd);
				ShowToast(R.string.difference_psd);
			}
		}
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
