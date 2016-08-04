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
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author marsXTU(hejunlin2013@gmail.com)
 */
public class AboutUsActivity extends Activity {

    private LinearLayout mUpgradeButton;
	private ImageButton mImageUpgrade;
	private TextView mCurrentVersion, mVersionTips, mUpgradeText, mUpgradeInfoView;
	private ImageView mLogo;
	private ImageView mLeftButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);
		initLayout();
	}
	
	private void initLayout() {
		mUpgradeInfoView = (TextView) findViewById(R.id.upgrade_version_info);
		mUpgradeButton = (LinearLayout) findViewById(R.id.upgrade_button_layout);
		mImageUpgrade = (ImageButton) findViewById(R.id.upgrade_imgbutton);
		mCurrentVersion = (TextView) findViewById(R.id.upgrade_current_version);
		mVersionTips = (TextView) findViewById(R.id.upgrade_version_tips);
		mUpgradeText = (TextView) findViewById(R.id.upgrade_text);
		mUpgradeButton.setEnabled(false);
		mUpgradeText.setEnabled(false);
		mCurrentVersion.setText(getString(R.string.upgrade_current_version, "1.2.1"));
        mLogo = (ImageView) findViewById(R.id.ic_logo);
        mLeftButton = (ImageView) findViewById(R.id.left_button);
        mLeftButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
                finish();				
			}
		});
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about_us, menu);
		return true;
	}

}
