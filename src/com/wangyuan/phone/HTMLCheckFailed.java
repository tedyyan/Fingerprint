package com.wangyuan.phone;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class HTMLCheckFailed extends Activity {
	WebView mWebView;
	Handler mHandler;
	@Override
	 public void onCreate(Bundle savedInstanceState)  {
	        super.onCreate(savedInstanceState); 
	        
	        setContentView(R.layout.htmlcheckfailed);
	        mWebView = (WebView) this.findViewById(R.id.webViewFailed);
	        mHandler = new Handler();
	        WebSettings webSettings = mWebView.getSettings();
	        webSettings.setJavaScriptEnabled(true);
	        mWebView.addJavascriptInterface(new JavaScriptHandler(), "demo"); 
	        mWebView.loadUrl("file:///android_asset/html/checkFailed.html");
	}
	
	class JavaScriptHandler {
		public void clickOnZhiFu() {
            mHandler.post(new Runnable() {
                public void run() {
                	//actionsHandler.onClickPay();
                }
            });
        }
    }
}
