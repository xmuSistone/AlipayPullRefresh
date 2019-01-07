package com.stone.alipay.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stone.alipay.R;
import com.stone.alipay.library.AlipayContainerLayout;
import com.stone.alipay.library.AlipayScrollView;
import com.stone.alipay.utils.CustomizedToast;
import com.stone.alipay.utils.Utils;
import com.stone.alipay.widget.TopLinearLayout;

/**
 * Created by xmuSistone on 2018/12/29.
 */
public class MainActivity extends AppCompatActivity {

    private AlipayContainerLayout containerLayout;
    private AlipayScrollView scrollView;

    private View backToolbar, frontToolbar;
    private TopLinearLayout topLinearLayout;
    private View topBlueLayout;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adjustStatusBar();
        initView();
    }

    /**
     * 沉浸式状态栏
     */
    private void adjustStatusBar() {
        boolean immerse = Utils.immerseStatusBar(this);
        View statusBarView = findViewById(R.id.home_status_view);
        if (immerse) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) statusBarView.getLayoutParams();
            lp.height = Utils.getStatusBarHeight(this);
            statusBarView.setLayoutParams(lp);
        } else {
            statusBarView.setVisibility(View.GONE);
        }
    }

    /**
     * View初始化
     */
    private void initView() {
        backToolbar = findViewById(R.id.home_toolbar_back);
        frontToolbar = findViewById(R.id.home_toolbar_front);

        // 1. 绑定View页面内容
        final LayoutInflater inflater = LayoutInflater.from(this);
        containerLayout = findViewById(R.id.home_container_layout);
        containerLayout.setDecorator(new AlipayContainerLayout.Decorator() {
            @Override
            public View getContentView() {
                View contentView = initContentView(inflater);
                return contentView;
            }

            @Override
            public View getTopLayout() {
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

            @Override
            public void onFlingStop() {
                snapHeaderView();
            }
        });
        topLinearLayout.bindParallax(scrollView, topBlueLayout);
    }

    private void snapHeaderView() {
        if (scrollView.getScrollY() < topBlueLayout.getMeasuredHeight() / 2) {
            scrollView.snapTo(0);
        } else if (scrollView.getScrollY() < topBlueLayout.getMeasuredHeight()) {
            scrollView.snapTo(topBlueLayout.getMeasuredHeight());
        }
    }

    private void parallaxScroll(int scrollY) {
        // 1. toolbar透明度渐变
        int maxDistance = topBlueLayout.getMeasuredHeight();
        float progress = (float) scrollY / maxDistance;
        if (progress < 0) {
            progress = 0;
        } else if (progress > 1.0f) {
            progress = 1.0f;
        }
        frontToolbar.setAlpha(progress);
        backToolbar.setAlpha(1 - progress);


        float blueAlpha = 1 - progress;
        topBlueLayout.setAlpha(blueAlpha);


        // 2. topLayout蓝色区域视差
        topLinearLayout.syncScrollParallax();
    }

    private View initContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.home_content, null);
        TextView priceTv1 = view.findViewById(R.id.home_price_tv1);
        TextView priceTv2 = view.findViewById(R.id.home_price_tv2);
        TextView priceTv3 = view.findViewById(R.id.home_price_tv3);
        priceTv1.setText(Html.fromHtml("促销价<font color='#ff0000'>15.8元</font>"));
        priceTv2.setText(Html.fromHtml("促销价<font color='#ff0000'>99.9元</font>"));
        priceTv3.setText(Html.fromHtml("促销价<font color='#ff0000'>8元</font>"));
        return view;
    }

    private View initTopLayout(LayoutInflater inflater) {
        View topLinearLayout = inflater.inflate(R.layout.home_top_layout, null);
        topBlueLayout = topLinearLayout.findViewById(R.id.top_blue_layout);

        int[] buttonIds = {R.id.top_button1, R.id.top_button2, R.id.top_button3, R.id.top_button4,
                R.id.top_button5, R.id.top_button6, R.id.top_button7, R.id.top_button8,
                R.id.top_button9, R.id.top_button10, R.id.top_button11, R.id.top_button12,
                R.id.top_button13, R.id.top_button14, R.id.top_button15, R.id.top_button16};

        final String[] buttonNames = {"扫一扫", "付钱", "收钱", "卡包",
                "余额宝", "转账", "信用卡还款", "记账本",
                "淘宝", "生活缴费", "红包", "充值中心",
                "机票", "天猫宝", "国际汇款", "更多"};

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonName = v.getTag().toString();
                toast(buttonName);
            }
        };

        int len = buttonIds.length;
        for (int i = 0; i < len; i++) {
            View itemView = topLinearLayout.findViewById(buttonIds[i]);
            itemView.setTag(buttonNames[i]);
            itemView.setOnClickListener(onClickListener);
            Utils.setBorderlessBackground(itemView);
        }
        return topLinearLayout;
    }

    /**
     * 模拟网络访问、回调
     */
    private void requestNetwork() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.setRefreshing(false);
            }
        }, 1600);
    }

    private void toast(String message) {
        CustomizedToast.getInstance().showToast(this, message);
    }
}