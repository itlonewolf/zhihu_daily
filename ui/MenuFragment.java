package com.example.zhihudaily.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zhihudaily.R;
import com.example.zhihudaily.SettingsActivity;

public class MenuFragment extends Fragment implements OnClickListener {
	private TextView tvSettings;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.drawer_menu, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		tvSettings = (TextView) view.findViewById(R.id.menu_settings);
		tvSettings.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.menu_settings:
			getActivity().startActivity(
					new Intent(getActivity(), SettingsActivity.class));
			break;
			
		}
	}
}
