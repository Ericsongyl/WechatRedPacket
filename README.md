# WechatRedPacket
Android：实现监听微信红包，并自动抢红包[PS：微信消息需打开通知栏提示]。

原理：

利用AccessibilityService这个辅助服务类，监听通知栏微信消息，如果有微信红包，就模拟界面一些控件的点击事件。
可能大家会问：如何模拟界面上的控件点击事件呢？
问得好。

这里可以用到ADT里的uiautomatorviewer工具，可以清晰地看到每个界面每个控件的属性，如控件类型（是Button，还是ImageView，或者是TextView……）、
clickable的值是否可点击等；还会用到AccessibilityService类中getRootInActiveWindow这个方法，来获取这个界面。
了解详细讲解，请关注博客[http://blog.csdn.net/weiren1101/article/details/53112439](http://blog.csdn.net/weiren1101/article/details/53112439)
======================
微信红包插件，简单、安全。

注意：

Android系统4.1以上、微信版本6.3.28及以上。

此app安装后，请在手机“设置---辅助服务”（可能有些手机不是这样称呼），开启“微信红包插件”服务才能提醒有微信红包。


