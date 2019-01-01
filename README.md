# AlipayHome
Alipay Pull Refresh

模仿支付宝首页下拉刷新效果<br/><br/>
支付宝首页的下拉刷新效果不走寻常路，跟我们理解的很不一样。它在下拉刷新时，分成上下两段，loading动画处于中间的位置。在拖拽的时候，就像是从中间撕裂的样子。<br/><br/>
真要细细琢磨起来，这样的下拉刷新确实挺难搞的。在页面中的任何一处都能上下拖动，确实很考验细心&耐心。

### 截图
效果图如下：<br/>
<a href="http://xmusistone.github.io/capture/alipay1.html" target="_blank">
  <img src="capture1.png" width="460"/>
</a>

点击图片可查看[动态的截屏视频](http://xmusistone.github.io/capture/alipay1.html)

### 使用方法
1. layout布局文件
```xml
<com.stone.alipay.library.AlipayContainerLayout
        android:id="@+id/home_container_layout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        alipay:progressColor="@color/statusBarColor"
        alipay:progressCenterOffset="3dp"
        alipay:progressHeight="@dimen/alipay_progress_height" />
```
2. java代码使用
```java
        containerLayout = findViewById(R.id.home_container_layout);
        containerLayout.setDecorator(new AlipayContainerLayout.Decorator() {
            @Override
            public View getContentView() {
                // 内部滑动的scrollView content
                View contentView = initContentView(inflater);
                return contentView;
            }

            @Override
            public View getTopLayout() {
                // 顶部悬浮的topLayout
                topLinearLayout = (TopLinearLayout) initTopLayout(inflater);
                return topLinearLayout;
            }
        });

        // 2. 下拉刷新
        scrollView = containerLayout.getScrollView();
        scrollView.setOnRefreshListener(new AlipayScrollView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 下拉刷新回调，请求网络数据
                requestNetwork();
            }
        });

        // 3. 顶部视差效果绑定
        scrollView.setScrollChangeListener(new AlipayScrollView.ScrollChangeListener() {
            @Override
            public void onScrollChange(int scrollY) {
                parallaxScroll(scrollY);
            }
        });
        topLinearLayout.bindParallax(scrollView, topBlueLayout);
```

