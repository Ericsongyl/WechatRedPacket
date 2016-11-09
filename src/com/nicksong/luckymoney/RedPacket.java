package com.nicksong.luckymoney;

import java.io.IOException;
import java.util.List;
import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class RedPacket extends AccessibilityService {
	
	private MediaPlayer player;
	private boolean isFirst = false;

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		int eventType = event.getEventType();
		switch (eventType) {
		//第一步：监听通知栏消息
		case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
			List<CharSequence> texts = event.getText();
			if (!texts.isEmpty()) {
				for (CharSequence text : texts) {
					String content = text.toString();
					Log.i("demo", "text:"+content);
					if (content.contains("[微信红包]")) {
						isFirst = true;
						playSound4LuckyMoneyComing();
						//模拟打开通知栏消息
						if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
							Notification notification = (Notification) event.getParcelableData();
							PendingIntent pendingIntent = notification.contentIntent;
							try {
								pendingIntent.send();
							} catch (CanceledException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			break;
		//第二步：监听是否进入微信红包消息界面
		case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
			String className = event.getClassName().toString();
			if (className.equals("com.tencent.mm.ui.LauncherUI")) {
				Log.i("demo", "开始抢红包");
				//开始抢红包
				getPacket();
			} else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI")) {
				Log.i("demo", "开始拆红包");
				isFirst = false;
				//开始打开红包
				openPacket();
			}
//			else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI")) {
//				Log.i("demo", "在红包详情直接返回");
//				//模拟点击返回
//				goBackFromLmUi();
//			}
			break;
		}
	}
	
	/**
	 * 在红包详情界面模拟返回
	 */
	private void goBackFromLmUi() {
		isFirst = false;
		AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
		if (nodeInfo != null) {
			for (int i = 0; i < nodeInfo.getChildCount(); i++) {
				Log.i("demo", "goBackFromLmUi i = " + i);
				Log.i("demo", "goBackFromLmUi Click"+",isClickable:" + nodeInfo.getChild(i).isClickable());
				Log.i("demo", "goBackFromLmUi View type = " + nodeInfo.getChild(i).getClassName());
				if ("android.widget.LinearLayout".equals(nodeInfo.getChild(i).getClassName()) && nodeInfo.getChild(i).isClickable()) {
					nodeInfo.getChild(i).performAction(AccessibilityNodeInfo.ACTION_CLICK);
				}
			}
		}
	}

	/**
	 * 查找到
	 */
	@SuppressLint("NewApi")
	private void openPacket() {
		AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
		if (nodeInfo != null) {
			for (int i = 0; i < nodeInfo.getChildCount(); i++) {
				Log.i("demo", "openPacket i = " + i);
				Log.i("demo", "Click"+",isClickable:" + nodeInfo.getChild(i).isClickable());
				Log.i("demo", "View type = " + nodeInfo.getChild(i).getClassName());
				if ("android.widget.Button".equals(nodeInfo.getChild(i).getClassName()) && nodeInfo.getChild(i).isClickable()) {
					nodeInfo.getChild(i).performAction(AccessibilityNodeInfo.ACTION_CLICK);
				}
			}
		}

	}

	@SuppressLint("NewApi")
	private void getPacket() {
		AccessibilityNodeInfo rootNode = getRootInActiveWindow();
		recycle(rootNode);
	}
	
	/**
	 * 打印一个节点的结构
	 * @param info
	 */
	@SuppressLint("NewApi")
	public void recycle(AccessibilityNodeInfo info) {
        if (info.getChildCount() == 0) { 
        	if(info.getText() != null){
        		if("领取红包".equals(info.getText().toString())){
        			//这里有一个问题需要注意，就是需要找到一个可以点击的View
                	Log.i("demo", "Click"+",isClick:"+info.isClickable());
                	info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                	AccessibilityNodeInfo parent = info.getParent();
                	while(parent != null){
                		Log.i("demo", "parent isClick:"+parent.isClickable());
                		if(parent.isClickable() && isFirst){ 
                			parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                			break;
                		}
                		parent = parent.getParent();
                	}
                	
            	}
        	}
        	
        } else {  
            for (int i = 0; i < info.getChildCount(); i++) {  
                if(info.getChild(i)!=null){  
                    recycle(info.getChild(i));
                }  
            }  
        }  
    }  

	@Override
	public void onInterrupt() {
	}

	private void playSound4LuckyMoneyComing() {
		try {
			AssetManager assetManager = this.getAssets();
			AssetFileDescriptor aFileDescriptor = assetManager.openFd("luckymoney_sound.ogg");
			player = new MediaPlayer();
			player.setDataSource(aFileDescriptor.getFileDescriptor(), aFileDescriptor.getStartOffset(), aFileDescriptor.getLength());
			player.prepare();
			player.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
