package com.hzw.nested.example;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hzw.nested.NestedWebViewRecyclerViewGroup;

public class NestedFragmentActivity extends AppCompatActivity implements View.OnClickListener {

    private NestedWebViewRecyclerViewGroup parent;
    private CommentFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nested_fragment);
        parent = findViewById(R.id.nest_parent);
        TextView tvComment = findViewById(R.id.tv_comment);
        TextView addItem = findViewById(R.id.addItem);
        TextView deleteItem = findViewById(R.id.deleteItem);
        tvComment.setOnClickListener(this);
        addItem.setOnClickListener(this);
        deleteItem.setOnClickListener(this);
        findViewById(R.id.hide_rv).setOnClickListener(this);
        initWebView();

        //评论是一个以Fragment的方式存在
        FragmentManager manager = getSupportFragmentManager();
        fragment = (CommentFragment) manager.findFragmentByTag(CommentFragment.class.getName());
        FragmentTransaction transaction = manager.beginTransaction();
        if (fragment == null) {
            fragment = new CommentFragment();
            transaction.add(R.id.fragment_container, fragment, CommentFragment.class.getName());
        } else {
            transaction.attach(fragment);
        }
        transaction.commit();


        //当你的评论的 RecyclerView 在一个Fragment中时，有两种方式可以将 RecyclerView
        //和 NestedWebViewRecyclerViewGroup 关联起来

        //1. 在 NestedWebViewRecyclerViewGroup 的内部如果在解析布局文件时，如果没有找到 RecyclerView ，
        //那么在界面显示时会尝试重新获取 RecyclerView ，这种情况不需要你再做额外的事情

        //2. 如果你还有更特殊的用法，当 NestedWebViewRecyclerViewGroup 在界面显示时都无法获取到 RecyclerView
        //那么可以调用 NestedWebViewRecyclerViewGroup 的 setRecyclerView 方法，将两者关联
        //parent.setRecyclerView(your recyclerView);

        parent.post(new Runnable() {
            @Override
            public void run() {
                Log.i("xxx", "run: parent");
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        int scrollY = parent.getCurrentScrollY();
        ReadUtil.saveRead(this, scrollY);
    }

    private void initWebView() {
        //手动添加WebView的方式，便于WebView的复用以及其他
        WebView webView = new CustomerWebView(this);
        FrameLayout container = findViewById(R.id.webView_container);
        container.addView(webView);
        webView.loadUrl("https://github.com/HzwSunshine/NestedWebViewRecyclerViewGroup");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_comment:
                parent.switchView(0);
                break;
            case R.id.addItem:
                fragment.addItem();
                break;
            case R.id.deleteItem:
                fragment.deleteItem();
                break;
            case R.id.hide_rv:
                View view = findViewById(R.id.fragment_container);
                if (view.getVisibility() == View.VISIBLE) {
                    view.setVisibility(View.GONE);
                } else {
                    view.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
}
