# RedPackage
红包精灵

##效果图：
![这里写图片描述](https://github.com/hejunlin2013/RedPackage/blob/master/image/iamge5.png)
![这里写图片描述](https://github.com/hejunlin2013/RedPackage/blob/master/image/image6.png)

##关键Service代码：
![这里写图片描述](https://github.com/hejunlin2013/RedPackage/blob/master/image/image1.png)
![这里写图片描述](https://github.com/hejunlin2013/RedPackage/blob/master/image/image2.png)
![这里写图片描述](https://github.com/hejunlin2013/RedPackage/blob/master/image/image3.png)
![这里写图片描述](https://github.com/hejunlin2013/RedPackage/blob/master/image/image4.png)

##实现原理

1.1 状态说明
``` 
	private static final String WECHAT_OPEN_CH = "拆红包";
	private static final String WECHAT_VIEW_SELF_CH = "查看红包";
	private static final String WECHAT_VIEW_OTHERS_CH = "领取红包";
	private static final String WECHAT_NOTIFICATION_TIP = "[微信红包]";
``` 

1.2 根据阶段选择不同的入口

在每次窗体状态发生变化后，根据当前所在的阶段选择入口。
``` 
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
``` 

2. 屏幕内容检测和自动化点击的实现
``` 
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
``` 

和其他插件一样，这里使用的是Android API提供的AccessibilityService。这个类位于android.accessibilityservice包内，开启服务后就会自动做一些操作
像现在360的自动安装apk,也是开启了此服务。

AccessibilityService 服务在后台运行，等待系统在发生 AccessibilityEvent 事件时回调。这些事件指的是用户界面上发生的状态变化， 比如焦点变更、按钮按下等等。服务可以请求“查询当前窗口中内容”的能力。 开发辅助服务需要继承该类并实现其抽象方法。

2.1 配置AccessibilityService

在这个例子中，我们需要监听的事件是当红包来或者滑动屏幕时引起的屏幕内容变化，和点开红包时窗体状态的变化，因此我们只需要在配置XML的accessibility-service标签中加入一条

android:accessibilityEventTypes="typeWindowStateChanged|typeWindowContentChanged"
或在onAccessibilityEvent回调函数中对事件进行一次类型判断

final int eventType = event.getEventType();
if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
     || eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
         // ...
}
除此之外，由于我们只监听微信，还需要指定微信的包名

android:packageNames="com.tencent.mm"
为了获取窗口内容，我们还需要指定

android:canRetrieveWindowContent="true"
其他配置请看代码。

2.2 获取红包所在的节点

首先，我们要获取当前屏幕的根节点，下面两种方式效果是相同的：
``` 
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
``` 
2.3 红包标识符
``` 
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
``` 
##欢迎关注我的个人公众号，android干货，源码解析
![这里写图片描述](https://github.com/hejunlin2013/RedPackage/blob/master/image/image1.png)


