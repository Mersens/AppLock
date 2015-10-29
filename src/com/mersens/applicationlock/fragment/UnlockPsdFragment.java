package com.mersens.applicationlock.fragment;

import com.mersens.applicationlock.main.R;
import com.mersens.applicationlock.service.MyService;
import com.mersens.applicationlock.utils.MD5Utils;
import com.mersens.applicationlock.utils.SharePreferenceUtil;
import com.mersens.applicationlock.view.Lock9View;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.widget.Toast;

public class UnlockPsdFragment extends Fragment {
	private Lock9View lock9View;
	private TextView textView;
	private int inputNum = 0;
	private  Toast mToast;
	private String packName=null;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_psd, null);
		packName=getActivity().getIntent().getStringExtra("packName");
		initViews(v);
		return v;
	}

	private void initViews(View v) {
		textView=(TextView) v.findViewById(R.id.textView);
		textView.setText(R.string.unlock_psd);
		lock9View = (Lock9View) v.findViewById(R.id.lock_9_view);
		lock9View.setCallBack(new Lock9View.CallBack() {
			@Override
			public void onFinish(String password) {
				doDealWithPsd(password);
			}
		});
	}

	@SuppressLint("ClickableViewAccessibility")
	private void doDealWithPsd(String password) {
		if (password.length() <= 3) {
			ShowToast(R.string.no_length);
			return;
		}

		String psd = SharePreferenceUtil.getInstance(
				getActivity().getApplicationContext()).getUserPsd();
		if (MD5Utils.md5Encode(password).equals(psd)) {
			ShowToast(R.string.psd_success);
			SharePreferenceUtil.getInstance(getActivity().getApplicationContext()).setLock(false);
			if(packName!=null){
				Intent intent=new Intent();
				intent.setAction(MyService.LOCK);
				intent.putExtra("packName", packName);
				getActivity().sendBroadcast(intent);
			}
			getActivity().finish();
		} else {
			inputNum = inputNum + 1;
			ShowToast(R.string.psd_error);
			textView.setText("密码错误！您还有" + (3 - inputNum)
					+ "次输入机会");
			if (inputNum == 3) {
				textView.setText(R.string.psd_error);
				lock9View.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						return true;
					}
				});
				return;
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
