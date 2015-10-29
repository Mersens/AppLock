package com.mersens.applicationlock.adapter;

import java.util.List;

import com.mersens.applicationlock.bean.AppInfo;
import com.mersens.applicationlock.db.AppLockDao;
import com.mersens.applicationlock.db.AppLockDaoImpl;
import com.mersens.applicationlock.main.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {
	private List<AppInfo> list;
	private LayoutInflater inflater;
	private AppLockDao dao;
	public MyAdapter(List<AppInfo> list, Context context) {
		this.list = list;
		inflater = LayoutInflater.from(context);
		dao=new AppLockDaoImpl(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_item, null);
			holder.image_app = (ImageView) convertView
					.findViewById(R.id.image_app);
			holder.tv_package = (TextView) convertView
					.findViewById(R.id.tv_package);
			holder.tv_ly = (TextView) convertView.findViewById(R.id.tv_ly);
			holder.image_zt = (ImageView) convertView
					.findViewById(R.id.image_zt);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		AppInfo info=list.get(position);
		holder.image_app.setImageDrawable(info.getAppIcon());
		holder.tv_package.setText(info.getName());
		if(info.isInRom()){
			holder.tv_ly.setText(R.string.phone_save);
		}else{
			holder.tv_ly.setText(R.string.outside_save);
		}
		if(dao.find(info.getPackName())){
			holder.image_zt.setImageResource(R.drawable.icon_ys);
		}else{
			holder.image_zt.setImageResource(R.drawable.icon_js);
		}
		return convertView;
	}

	private class ViewHolder {
		private ImageView image_app;
		private TextView tv_package;
		private TextView tv_ly;
		private ImageView image_zt;
	}
}
