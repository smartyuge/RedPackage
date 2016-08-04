/*
**        RedPackage Project
**
** Copyright(c) 2016 marsXTU <hejunlin2013@gmail.com>
**
** This file is part of RedPackage.
**
** RedPackage is free software: you can redistribute it and/or
** modify it under the terms of the GNU Lesser General Public
** License as published by the Free Software Foundation, either
** version 3 of the License, or (at your option) any later version.
**
** RedPackage is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
** Lesser General Public License for more details.
**
** You should have received a copy of the GNU Lesser General Public
** License along with RedPackage.  If not, see <http://www.gnu.org/licenses/lgpl.txt>
**
**/
package com.marsXTU.qianghongbao;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import cn.domob.android.ads.AdEventListener;
import cn.domob.android.ads.AdManager.ErrorCode;
import cn.domob.android.ads.AdView;
/**
 * @author marsXTU(hejunlin2013@gmail.com)
 */
public class BannerAdActivity extends Activity {
	RelativeLayout mAdContainer;
	AdView mAdview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.banner);
		mAdContainer = (RelativeLayout) findViewById(R.id.adcontainer);
		// Create ad view
		mAdview = new AdView(this, HomeActivity.PUBLISHER_ID, HomeActivity.InlinePPID);
		mAdview.setKeyword("game");
		mAdview.setUserGender("male");
		mAdview.setUserBirthdayStr("2000-08-08");
		mAdview.setUserPostcode("123456");
		mAdview.setAdEventListener(new AdEventListener() {
			@Override
			public void onAdOverlayPresented(AdView adView) {
				Log.i("DomobSDKDemo", "overlayPresented");
			}
			@Override
			public void onAdOverlayDismissed(AdView adView) {
				Log.i("DomobSDKDemo", "Overrided be dismissed");
			}
			@Override
			public void onAdClicked(AdView arg0) {
				Log.i("DomobSDKDemo", "onDomobAdClicked");
			}
			@Override
			public void onLeaveApplication(AdView arg0) {
				Log.i("DomobSDKDemo", "onDomobLeaveApplication");
			}
			@Override
			public Context onAdRequiresCurrentContext() {
				return BannerAdActivity.this;
			}
			@Override
			public void onAdFailed(AdView arg0, ErrorCode arg1) {
				Log.i("DomobSDKDemo", "onDomobAdFailed");
			}
			@Override
			public void onEventAdReturned(AdView arg0) {
				Log.i("DomobSDKDemo", "onDomobAdReturned");
			}
		});
		RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		layout.addRule(RelativeLayout.CENTER_HORIZONTAL);
		mAdview.setLayoutParams(layout);
		mAdContainer.addView(mAdview);
	}
}
