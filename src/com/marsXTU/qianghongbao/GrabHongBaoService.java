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

import java.util.ArrayList;
import java.util.List;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

/**
 * @author marsXTU(hejunlin2013@gmail.com)
 */
@SuppressLint("NewApi")
public class GrabHongBaoService extends AccessibilityService {
	
	private static final String WECHAT_DETAILS_EN = "Details";
	private static final String WECHAT_DETAILS_CH = "红包详情";
	private static final String WECHAT_BETTER_LUCK_EN = "Better luck next time!";
	private static final String WECHAT_BETTER_LUCK_CH = "手慢了";
	private static final String WECHAT_OPEN_EN = "Open";
	private static final String WECHAT_OPENED_EN = "You've opened";
	private static final String WECHAT_OPEN_CH = "拆红包";
	private static final String WECHAT_VIEW_SELF_CH = "查看红包";
	private static final String WECHAT_VIEW_OTHERS_CH = "领取红包";
	private static final String WECHAT_NOTIFICATION_TIP = "[微信红包]";
	
    private List<AccessibilityNodeInfo> mReceiveNodeList; 
    private List<AccessibilityNodeInfo> mUnpackNodeList;
    private static final int MAX_CACHE_TOLERANCE = 5000;
	private static final String TAG = "GrabHongBaoService";
    private AccessibilityNodeInfo rootNodeInfo;
    
    private boolean mLuckyMoneyPicked;
    private boolean mLuckyMoneyReceived;
    private boolean mNeedUnpack;
    private boolean mNeedBack;
    private boolean mCycle = false;
    private boolean isClicked = false;
    private String lastFetchedHongbaoId = null;
    private long lastFetchedTime = 0;

    /**
     * AccessibilityEvent的回调方法
     *
     * @param event 事件
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    	 Log.d(TAG, "事件---->onAccessibilityEvent " + event);
        /* 检测通知消息 */
        if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED && !mCycle) {
        	 List<CharSequence> texts = event.getText();
             if(!texts.isEmpty()) {
                 for(CharSequence t : texts) {
                     String text = String.valueOf(t);
                     Log.d(TAG, "事件----> notifychanged " + text);
                     if(text.contains(WECHAT_NOTIFICATION_TIP)) {
                     	isClicked = false;
                         openNotify(event);
                         break;
                     }
                 }
             }
//            return;
        } else if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && !mCycle) {
        	Log.i(TAG, "startGrapHongBao() ");
            startGrapHongBao(event);
        }
    }
    
    private void startGrapHongBao(AccessibilityEvent event) {
    	Log.d(TAG, "事件----> start startGrapHongBao " + event);
    	this.rootNodeInfo = event.getSource();
        if (rootNodeInfo == null) return;
        mReceiveNodeList = null;
        mUnpackNodeList = null;
        checkNodeInfo();

        /* 如果已经接收到红包并且还没有戳开 */
        if (mLuckyMoneyReceived && !mLuckyMoneyPicked && (mReceiveNodeList != null)) {
            int size = mReceiveNodeList.size();
            Log.d(TAG, "事件----> performAction 节点数目 " + mReceiveNodeList.size());
            if (size > 0) {
            	Log.d(TAG, "事件----> start 已经接收到红包并且还没有戳开 ");
                String id = getHongbaoText(mReceiveNodeList.get(size - 1));
                long now = System.currentTimeMillis();
                if (this.shouldReturn(id, now - lastFetchedTime))
                    return;

                mCycle = true;
                lastFetchedHongbaoId = id;
                lastFetchedTime = now;
                AccessibilityNodeInfo cellNode = mReceiveNodeList.get(size - 1);
                cellNode.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                Log.d(TAG, "事件----> end 已经接收到红包并且还没有戳开 click");
                mLuckyMoneyReceived = false;
                mLuckyMoneyPicked = true;
            }
        }
        
        /* 如果戳开但还未领取 */
        if (mNeedUnpack && (mUnpackNodeList != null)) {
        	Log.d(TAG, "事件----> start performAction 戳开但还未领取");
            int size = mUnpackNodeList.size();
            if (size > 0) {
                AccessibilityNodeInfo cellNode = mUnpackNodeList.get(size - 1);
                cellNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                mNeedUnpack = false;
            }
            Log.d(TAG, "事件----> end performAction 戳开但还未领取");
        }

        if (mNeedBack) {
        	Log.d(TAG, "事件----> start performAction back");
            performGlobalAction(GLOBAL_ACTION_BACK);
            mCycle = false;
            mNeedBack = false;
            Log.d(TAG, "事件----> end performAction back");
        }
//        new Handler().postDelayed(new Runnable() {
//			@Override
//			public void run() {
//                playSounds();						
//			}
//		}, 0);
        Log.d(TAG, "事件----> end startGrapHongBao " + event);
	}
    
    private void playSounds() {
		MediaPlayer player;
		player = MediaPlayer.create(this, R.raw.shake_match);
		player.setLooping(false);
		player.start();
	}

	/** 打开通知栏消息*/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void openNotify(AccessibilityEvent event) {
        if(event.getParcelableData() == null || !(event.getParcelableData() instanceof Notification)) {
            return;
        }
        //将微信的通知栏消息打开
        Notification notification = (Notification) event.getParcelableData();
        PendingIntent pendingIntent = notification.contentIntent;
        Log.d(TAG, "事件----> 打开通知栏消息 " + event);
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInterrupt() {

    }

    /**
     * 检查节点信息
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void checkNodeInfo() {
    	Log.d(TAG, "事件----> start checkNodeInfo()!");
        if (this.rootNodeInfo == null) return;

        /* 聊天会话窗口，遍历节点匹配“领取红包”和"查看红包" */
        List<AccessibilityNodeInfo> nodes1 = this.findAccessibilityNodeInfosByTexts(
        		this.rootNodeInfo, 
        		new String[]{
                WECHAT_VIEW_OTHERS_CH, WECHAT_VIEW_SELF_CH });

        if (!nodes1.isEmpty()) {
            String nodeId = Integer.toHexString(System.identityHashCode(this.rootNodeInfo));
            if (!nodeId.equals(lastFetchedHongbaoId)) {
                mLuckyMoneyReceived = true;
                mReceiveNodeList = nodes1;
            }
            return;
        }

        /* 戳开红包，红包还没抢完，遍历节点匹配“拆红包” */
        List<AccessibilityNodeInfo> nodes2 = this.findAccessibilityNodeInfosByTexts(
        		this.rootNodeInfo, new String[]{
                WECHAT_OPEN_CH, WECHAT_OPEN_EN });
        if (!nodes2.isEmpty()) {
            mUnpackNodeList = nodes2;
            mNeedUnpack = true;
            return;
        }

        /* 戳开红包，红包已被抢完，遍历节点匹配“红包详情”和“手慢了” */
        if (mLuckyMoneyPicked) {
            List<AccessibilityNodeInfo> nodes3 = this.findAccessibilityNodeInfosByTexts(
            		this.rootNodeInfo, new String[]{
                    WECHAT_BETTER_LUCK_CH, WECHAT_DETAILS_CH,
                    WECHAT_BETTER_LUCK_EN, WECHAT_DETAILS_EN });
            if (!nodes3.isEmpty()) {
                mNeedBack = true;
                mLuckyMoneyPicked = false;
            }
        }
        Log.d(TAG, "事件----> end checkNodeInfo()!");
    }

    /**
     * 将节点对象的id和红包上的内容合并
     * 用于表示一个唯一的红包
     *
     * @param node 任意对象
     * @return 红包标识字符串
     */
    private String getHongbaoText(AccessibilityNodeInfo node) {
        /* 获取红包上的文本 */
        String content;
        try {
            AccessibilityNodeInfo i = node.getParent().getChild(0);
            content = i.getText().toString();
        } catch (NullPointerException npe) {
            return null;
        }
        Log.d(TAG, "事件----> start getHongbaoText() " + content);
        return content;
    }

    /**
     * 批量化执行AccessibilityNodeInfo.findAccessibilityNodeInfosByText(text).
     * 由于这个操作影响性能,将所有需要匹配的文字一起处理,尽早返回
     *
     * @param nodeInfo 窗口根节点
     * @param texts    需要匹配的字符串们
     * @return 匹配到的节点数组
     */
    private List<AccessibilityNodeInfo> findAccessibilityNodeInfosByTexts(AccessibilityNodeInfo nodeInfo, String[] texts) {
    	Log.d(TAG, "事件----> start findAccessibilityNodeInfosByTexts() " + texts);
    	for (String text : texts) {
            if (text == null) continue;

            List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByText(text);

            if (!nodes.isEmpty()) {
                if (text.equals(WECHAT_OPEN_EN) && !nodeInfo.findAccessibilityNodeInfosByText(WECHAT_OPENED_EN).isEmpty()) {
                    continue;
                }
                return nodes;
            }
        }
    	Log.d(TAG, "事件----> end findAccessibilityNodeInfosByTexts() " + texts);
        return new ArrayList<AccessibilityNodeInfo>();
    }

    /**
     * 判断是否返回,减少点击次数
     * 现在的策略是当红包文本和缓存不一致时,戳
     * 文本一致且间隔大于MAX_CACHE_TOLERANCE时,戳
     *
     * @param id       红包id
     * @param duration 红包到达与缓存的间隔
     * @return 是否应该返回
     */
    private boolean shouldReturn(String id, long duration) {
        // ID为空
        if (id == null) return true;

        // 名称和缓存不一致
        if (duration < MAX_CACHE_TOLERANCE && id.equals(lastFetchedHongbaoId)) {
            return true;
        }
        return false;
    }
    

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        HomeActivity.mIsStarted = true;
        Toast.makeText(this, "连接红包精灵服务", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public boolean onUnbind(Intent intent) {
    	Toast.makeText(this, "中断红包精灵服务", Toast.LENGTH_SHORT).show();
    	HomeActivity.mIsStarted = false;
    	return super.onUnbind(intent);
    }

    
}
