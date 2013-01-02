package com.wangyuan.phone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class HTMLCheckSuccessful extends Activity {
	WebView mWebView;
	Handler mHandler;
	public static String innerpassword="1234";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.htmlchecksuccessful);
		mWebView = (WebView) this.findViewById(R.id.webViewSuccessful);
		mHandler = new Handler();
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		mWebView.addJavascriptInterface(new JavaScriptHandler(), "demo");
		mWebView.loadUrl("file:///android_asset/html/0302denglushang.htm");
	}

	class JavaScriptHandler {
		public void clickOnChangeFingerPrint() {
			mHandler.post(new Runnable() {
				public void run() {

					Intent intent = new Intent();
					intent.setClass(HTMLCheckSuccessful.this,
							HTMLFigureInputActivity.class);
					intent.putExtra("str", "come from first activity");
					startActivity(intent);// 无返回值的调用,启动一个明确的activity

				}
			});
		}
		public void SetPassword(final String password) {
			mHandler.post(new Runnable() {
				public void run() {
					HTMLCheckSuccessful.innerpassword = password;
				}
			});
		}
		public void GetPassword() {
			mHandler.post(new Runnable() {
				public void run() {
					mWebView.loadUrl("javascript:setPassword(\""+HTMLCheckSuccessful.innerpassword+"\")");
				}
			});
		}
	}
}
