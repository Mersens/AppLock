package com.mersens.applicationlock.main;

import com.mersens.applicationlock.fragment.SetPsdFragment;
import com.mersens.applicationlock.fragment.SingleFragmentActivity;
import com.mersens.applicationlock.fragment.UnlockPsdFragment;
import com.mersens.applicationlock.utils.SharePreferenceUtil;
import com.mersens.applicationlock.view.DialogTips;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
public class PsdActivity extends SingleFragmentActivity {
	public static final String SETPSD = "setpsd";
	public static final String UNLOCK = "unlock";
	private String type = null;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

	}

	@Override
	protected Fragment creatFragment() {
		type = getIntent().getStringExtra("type");
		if (SETPSD.equals(type)) {
			return new SetPsdFragment();
		} else {
			return new UnlockPsdFragment();
		}

	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		confirmExit();
	}

	public void confirmExit() {

		DialogTips dialog = new DialogTips(PsdActivity.this, "退出", "是否退出软件？",
				"确定", true, true);
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				SharePreferenceUtil.getInstance(getApplicationContext()).setLock(true);
				finish();
			}
		});
		dialog.show();
		dialog = null;
	}
}
