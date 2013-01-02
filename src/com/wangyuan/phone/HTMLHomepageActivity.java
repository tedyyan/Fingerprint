package com.wangyuan.phone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class HTMLHomepageActivity extends Activity {
	WebView mWebView;
	Handler mHandler;
	@Override
	 public void onCreate(Bundle savedInstanceState)  {
	        super.onCreate(savedInstanceState); 
	        
	        setContentView(R.layout.htmlhomepage);   	        
	        mWebView = (WebView) this.findViewById(R.id.webViewHome);
	        mHandler = new Handler();
	        WebSettings webSettings = mWebView.getSettings();
	        webSettings.setJavaScriptEnabled(true);
	        mWebView.addJavascriptInterface(new JavaScriptHandler(), "demo"); 
	        mWebView.loadUrl("file:///android_asset/html/index.htm");
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mWebView.canGoBack() && event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
        	mWebView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
	
	class JavaScriptHandler {
		public void clickOnAndroid(final String para) {
            mHandler.post(new Runnable() {
                public void run() {
                    mWebView.loadUrl("javascript:wave(\"fromAndroid"+para+"\")");// 调用脚本函数
                }
            });
        }
		public void clickForPacket(final String para) {
            mHandler.post(new Runnable() {
                public void run() {
                	Intent intent = new Intent();
					intent.setClass(HTMLHomepageActivity.this, DemoHomeActivity.class);
					intent.putExtra("str", "come from first activity");
					startActivity(intent);//无返回值的调用,启动一个明确的activity
                }
            });
        }
		public void clickForHTMLPacket(final String para) {
            mHandler.post(new Runnable() {
                public void run() {
                	Intent intent = new Intent();
					intent.setClass(HTMLHomepageActivity.this, HTMLPayActivity.class);
					intent.putExtra("str", "come from first activity");
					startActivity(intent);//无返回值的调用,启动一个明确的activity
                }
            });
        }
		public void clickForExit() {
            mHandler.post(new Runnable() {
                public void run() {

            		System.exit(0);
                }
            });
        }
    }
}
