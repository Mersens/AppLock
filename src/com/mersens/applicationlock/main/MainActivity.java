package com.mersens.applicationlock.main;

import java.util.ArrayList;
import java.util.List;

import com.mersens.applicationlock.fragment.SystemApplicationFrqagment;
import com.mersens.applicationlock.fragment.UserApplicationFragment;
import com.mersens.applicationlock.service.MyService;
import com.mersens.applicationlock.utils.ServiceUtils;
import com.mersens.applicationlock.utils.SharePreferenceUtil;
import com.mersens.applicationlock.view.DialogTips;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {
	private ViewPager viewPager;
	private ImageView imageView;
	private TextView tv_mr, tv_lv;
	private List<Fragment> fragments;
	private int offset = 0;
	private int currIndex = 0;
	private int bmpW;
	private int selectedColor, unSelectedColor;
	private static final int pageSize = 2;
	private TextView tv_exit;
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		boolean isRunningService = ServiceUtils.isRunningService(
				MainActivity.this,
				"com.mersens.applicationlock.service.MyService");
		if (!isRunningService) {
			intent = new Intent(this, MyService.class);
			startService(intent);
		}
		initView();
	}

	@Override
	protected void onDestroy() {
		// stopService(intent);
		super.onDestroy();
	}

	private void initView() {
		tv_exit = (TextView) findViewById(R.id.tv_exit);
		tv_exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				confirmExit();

			}
		});
		selectedColor = getResources()
				.getColor(R.color.tab_title_pressed_color);
		unSelectedColor = getResources().getColor(
				R.color.tab_title_normal_color);
		InitImageView();
		InitTextView();
		InitViewPager();

	}

	private void InitViewPager() {
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		fragments = new ArrayList<Fragment>();
		fragments.add(new UserApplicationFragment());
		fragments.add(new SystemApplicationFrqagment());
		viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(),
				fragments));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	/**
	 * 初始化头标
	 * 
	 */
	private void InitTextView() {
		tv_mr = (TextView) findViewById(R.id.tab_1);
		tv_lv = (TextView) findViewById(R.id.tab_2);
		tv_mr.setTextColor(selectedColor);
		tv_lv.setTextColor(unSelectedColor);

		tv_mr.setText(R.string.user_app);
		tv_lv.setText(R.string.system_app);

		tv_mr.setOnClickListener(new MyOnClickListener(0));
		tv_lv.setOnClickListener(new MyOnClickListener(1));

	}

	private void InitImageView() {
		imageView = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(),
				R.drawable.tab_selected_bg).getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / pageSize - bmpW) / 2;// 计算偏移量--(屏幕宽度/页卡总数-图片实际宽度)/2
													// = 偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		imageView.setImageMatrix(matrix);// 设置动画初始位置
	}

	/**
	 * 头标点击监听
	 */
	private class MyOnClickListener implements OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		public void onClick(View v) {

			switch (index) {
			case 0:
				tv_mr.setTextColor(selectedColor);
				tv_lv.setTextColor(unSelectedColor);
				break;
			case 1:
				tv_mr.setTextColor(unSelectedColor);
				tv_lv.setTextColor(selectedColor);
				break;
			}
			viewPager.setCurrentItem(index);
		}
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		int two = one * 2;// 页卡1 -> 页卡3 偏移量

		public void onPageScrollStateChanged(int index) {
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		public void onPageSelected(int index) {
			Animation animation = new TranslateAnimation(one * currIndex, one
					* index, 0, 0);// 显然这个比较简洁，只有一行代码。
			currIndex = index;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			imageView.startAnimation(animation);

			switch (index) {
			case 0:
				tv_mr.setTextColor(selectedColor);
				tv_lv.setTextColor(unSelectedColor);
				break;
			case 1:
				tv_mr.setTextColor(unSelectedColor);
				tv_lv.setTextColor(selectedColor);
				break;
			}
		}
	}

	class MyPagerAdapter extends FragmentPagerAdapter {
		private List<Fragment> fragmentList;

		public MyPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
			super(fm);
			this.fragmentList = fragmentList;
		}

		@Override
		public Fragment getItem(int arg0) {
			return (fragmentList == null || fragmentList.size() == 0) ? null
					: fragmentList.get(arg0);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return null;
		}

		@Override
		public int getCount() {
			return fragmentList == null ? 0 : fragmentList.size();
		}
	}

	@Override
	public void onBackPressed() {
		confirmExit();
	}
	
	@Override
	protected void onStart() {
		unlock();
		super.onStart();
	}

	public void unlock() {
		String psd = SharePreferenceUtil.getInstance(getApplicationContext())
				.getUserPsd();
		boolean isLock = SharePreferenceUtil.getInstance(
				getApplicationContext()).isLock();
		if (TextUtils.isEmpty(psd)) {
			Intent intent = new Intent(MainActivity.this, PsdActivity.class);
			intent.putExtra("type", PsdActivity.SETPSD);
			startActivity(intent);
		} else {
			if (isLock && !TextUtils.isEmpty(psd)) {
				Intent intent = new Intent(MainActivity.this, PsdActivity.class);
				intent.putExtra("type", PsdActivity.UNLOCK);
				startActivity(intent);
			}
		}
	}

	public void confirmExit() {
		DialogTips dialog = new DialogTips(MainActivity.this, "退出", "是否退出软件？",
				"确定", true, true);
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				SharePreferenceUtil.getInstance(getApplicationContext())
						.setLock(true);
				finish();
			}
		});
		dialog.show();
		dialog = null;
	}
}
