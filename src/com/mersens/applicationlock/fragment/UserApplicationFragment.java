package com.mersens.applicationlock.fragment;

import java.util.List;

import com.mersens.applicationlock.adapter.MyAdapter;
import com.mersens.applicationlock.bean.AppInfo;
import com.mersens.applicationlock.db.AppLockDao;
import com.mersens.applicationlock.db.AppLockDaoImpl;
import com.mersens.applicationlock.main.R;
import com.mersens.applicationlock.utils.AppInfoDatas;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class UserApplicationFragment extends Fragment{
	private ListView listview;
	private ProgressBar progressBar;
	private List<AppInfo> userAppInfoList;
	private MyAdapter adapter;
	private AppLockDao dao;
	private TextView tv_app_count;
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v=inflater.inflate(R.layout.layout_user_app, null);
		dao=new AppLockDaoImpl(getActivity().getApplicationContext());
		initViews(v);
		initDates();
		return v;
	}

	private void initViews(View v) {
		tv_app_count=(TextView) v.findViewById(R.id.tv_app_count);
		listview=(ListView) v.findViewById(R.id.system_listView);
		progressBar=(ProgressBar) v.findViewById(R.id.progressBar);
		progressBar.setVisibility(View.VISIBLE);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				AppInfo info=userAppInfoList.get(position);
				Toast.makeText(getActivity(), info.getName(), Toast.LENGTH_SHORT).show();
				ImageView image_zt=(ImageView) view.findViewById(R.id.image_zt);
				if(dao.find(info.getPackName())){
					dao.delete(info.getPackName());
					image_zt.setImageResource(R.drawable.icon_js);
				}else{
					dao.add(info.getPackName());
					image_zt.setImageResource(R.drawable.icon_ys);
				}
			}
		});
	}
	private void initDates() {
		userAppInfoList=AppInfoDatas.getInstance(getActivity().getApplicationContext()).getUserAppInfo();
		adapter=new MyAdapter(userAppInfoList, getActivity().getApplicationContext());
		listview.setAdapter(adapter);
		tv_app_count.setText("用户程序"+userAppInfoList.size()+"个");
		tv_app_count.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);
	}
}
