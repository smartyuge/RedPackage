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
import android.view.WindowManager;

import com.ant.liao.GifView;
import com.ant.liao.GifView.GifImageType;
/**
 * @author marsXTU(hejunlin2013@gmail.com)
 */
public class GuideActivity extends Activity {

	private GifView gf1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		@SuppressWarnings("deprecation")
		int width = wm.getDefaultDisplay().getWidth();
		@SuppressWarnings("deprecation")
		int height = wm.getDefaultDisplay().getHeight();
		
		gf1 = (GifView) findViewById(R.id.gif2);
		// 设置Gif图片源
		gf1.setGifImage(R.drawable.guide);
		// 设置显示的大小，拉伸或者压缩
		gf1.setShowDimension(width, height);
		// 设置加载方式：先加载后显示、边加载边显示、只显示第一帧再显示
		gf1.setGifImageType(GifImageType.COVER);
		
	}


}
