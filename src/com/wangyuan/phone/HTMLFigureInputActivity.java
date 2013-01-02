package com.wangyuan.phone;

import com.hdsoft.fingerprint.LibFp;
import com.wangyuan.phone.HTMLCheckSuccessful.JavaScriptHandler;
import com.wangyuan.phone.HTMLPayActivity.ExpiredThread;
import com.wangyuan.phone.HTMLPayActivity.HMTLProxy;
import com.wangyuan.phone.HTMLPayActivity.MessageHandler;
import com.wangyuan.phone.HTMLPayActivity.PayThread;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class HTMLFigureInputActivity extends Activity {
	WebView mWebView;
	Handler mHandler;
	private static final int MSG_CMD    = 0;
	private static final int MSG_EXIT   = 1;
	private static final int MSG_INFO   = 2;
	private static final int MSG_FIND   = 3;
	private static final int MSG_CANCEL = 4;
	private boolean bContinue = true;
	private boolean fgIsOpen = false;

	HMTLProxy htmlProxy = new HMTLProxy();
	class HMTLProxy
	{
		
		void putHintText(String hintText)
		{
			mWebView.loadUrl("javascript:setHintText(\""+hintText+"\")");
		}
	}
	@Override
	 public void onCreate(Bundle savedInstanceState)  {
	        super.onCreate(savedInstanceState); 
	        
	        setContentView(R.layout.htmlfigureinput);   	        
	        mWebView = (WebView) this.findViewById(R.id.webViewFigureInput);
	        mHandler = new Handler();
	        WebSettings webSettings = mWebView.getSettings();
	        webSettings.setJavaScriptEnabled(true);
	        mWebView.addJavascriptInterface(new JavaScriptHandler(), "input"); 
	        mWebView.loadUrl("file:///android_asset/html/inputFinger.html");
	        mWebView.requestFocus();
	}
	
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mWebView.canGoBack() && event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
        	mWebView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

	@Override
	protected void onResume() {
		
		super.onResume();
		if (fgIsOpen) {
			try {
				Thread.sleep(600);
			} catch (InterruptedException e) {
			}
			LibFp.FpClose();
			fgIsOpen = false;
		}
		bContinue = true;
		if (checkSD()==false)
		{
			htmlProxy.putHintText("请插入网元电子钱包卡！");
		}
		else
		{
			htmlProxy.putHintText("录入指纹");
		}
		mWebView.requestFocus();
	}
	void onInputFingerPrint() {
		if (!checkSD()) {
			Log.d("1","SD Card check is failed!");
			return;
		}
		if (!fgIsOpen) {
			int nRet = LibFp.FpOpenEx((short) 0x2109, (short) 0x7638);
			if (nRet == LibFp.FP_ERROR_OPEN) {
				LibFp.GetRootRight();
				nRet = LibFp.FpOpenEx((short) 0x2109, (short) 0x7638);
			}

			if (nRet == LibFp.FP_OK) {
				Log.d("fingerprint", "Successfully open fingerprint device for enroll.");
				htmlProxy.putHintText("打开设备成功！");
				fgIsOpen = true;

				new Thread(new InputFingerPrintThread()).start();
			} else {
				htmlProxy.putHintText(LibFp.GetError(nRet));
				Log.d("fingerprint", "Can't open fingerprint device for enroll.");
				try {
					Thread.sleep(600);
				} catch (InterruptedException e) {
				}
				//LibFp.FpClose();
			}
		}
	}
	private void sendInfo(Handler handler, int nMsg, int arg1) {
		Message msg = handler.obtainMessage();
		msg.what = nMsg;
		msg.arg1 = arg1;
		handler.sendMessage(msg);
	}
	private void sendInfo(Handler handler, int nMsg, int arg1, int arg2){
    	Message msg = handler.obtainMessage();
		msg.what = nMsg;
		msg.arg1 = arg1;
		msg.arg2 = arg2;
		handler.sendMessage(msg);
    }
	class MessageHandler extends Handler{   
    	public void handleMessage(Message msg) { 
    		switch (msg.what) {   
    		case MSG_CMD:
    			htmlProxy.putHintText(LibFp.GetError(msg.arg1));
    			
    			break;
    		case MSG_EXIT:
    			htmlProxy.putHintText(LibFp.GetError(msg.arg1));
    			
    			break;
    		case MSG_INFO:
    			switch (msg.arg1) {
    			case 0:
    				htmlProxy.putHintText("录入指纹, 请按手指...");
    				break;
    			case 1:
    				htmlProxy.putHintText("请移开手指...");
    				break;
    			case 2:
    				htmlProxy.putHintText("指纹成功入库, 地址编号 = " + msg.arg2 + ", 请按手指继续录入...");
    				break;
    			default:
    				break;
    			}
    			break;
    		case MSG_FIND:
    			
    			htmlProxy.putHintText("指纹匹配成功, 存放地址 = " + msg.arg1 + "比对得分  = " + msg.arg2);
    			break;
    		case MSG_CANCEL:
    			bContinue = true;
    			
				switch (msg.arg1) {
    			case 0:
    				htmlProxy.putHintText("操作已取消...");
    				break;
    			default:
    				break;
    			}
    			break;
    		default: 
    			break;   
    		}   
    		super.handleMessage(msg);   
    	}
    };
	final Handler handler = new MessageHandler();
	class InputFingerPrintThread implements Runnable {
		public void run() {

			byte nBufferId = 1;
        	byte bmpData[] = new byte[256*288+1078];
        	int  bmpSize[] = new int[1];
        	short nPageId[] = new short[1];
        	if (LibFp.FpValidTempleteNum(0xffffffff, nPageId, 10000) != LibFp.FP_OK) return;
        	sendInfo(handler, MSG_INFO, 0);
        	while (bContinue) {
        		int nRet = LibFp.FpGetImage(0xffffffff, 10000);
        		if (nRet == LibFp.FP_OK) {
        			if ((nRet = LibFp.FpGenChar(0xffffffff, nBufferId, 10000)) == LibFp.FP_OK) {			        				
        				if ((nRet = LibFp.FpGetImage(0xffffffff, 10000)) == LibFp.FP_OK) {
        					if ((nRet = LibFp.FpUpBMP(0xffffffff, bmpData, 256*288 + 1078, bmpSize, 10000)) == LibFp.FP_OK) {
			        			final Bitmap bm = BitmapFactory.decodeByteArray(bmpData, 0, 256*288 + 1078);
			        							        		
        						sendInfo(handler, MSG_INFO, 1);			        						
        						while ((nRet = LibFp.FpGetImage(0xffffffff, 10000)) != LibFp.FP_NO_FINGER) {
        							if (nRet != LibFp.FP_OK) {
        								sendInfo(handler, MSG_EXIT, nRet);
		        						return;
        							}
        							if (!bContinue) {
        								sendInfo(handler, MSG_CANCEL, 0);
        								return;
        							}
        							try {
				        				Thread.sleep(150);
					                } catch (InterruptedException e) {
					                }
        						}
        					}
        				}
        				if (nBufferId == 2) {
        					if ((nRet = LibFp.FpRegModel(0xffffffff, 10000))== LibFp.FP_OK) {			        						
        						if ((nRet = LibFp.FpStoreChar(0xffffffff, nBufferId, nPageId[0], 10000)) == LibFp.FP_OK) {
        							sendInfo(handler, MSG_INFO, 2, nPageId[0]);
        							nBufferId = 1;
        							nPageId[0] += 1;
        						}
        					}
        					if (nRet != LibFp.FP_OK) {		
        						sendInfo(handler, MSG_EXIT, nRet);
        						return;
        					}			        					
        				}
        				else {
        					nBufferId = 2;
        					sendInfo(handler, MSG_INFO, 0);
        				}			        				
        			}
        		}
        		if (nRet != LibFp.FP_OK && nRet != LibFp.FP_NO_FINGER) {
        			sendInfo(handler, MSG_EXIT, nRet);
        			return;
        		}
        	}
        	sendInfo(handler, MSG_CANCEL, 0);
	        
	    
		}
	}
	
	public void onPause() {
		bContinue = false;
		try {
			Thread.sleep(600);
		} catch (InterruptedException e) {
		}
		if (fgIsOpen)
		{
			LibFp.FpClose();
			fgIsOpen = false;
		}
		super.onPause();
	}

	protected boolean checkSD() {
		return true;
	}
	
	class JavaScriptHandler {
		public void clickOnAndroid(final String para) {
            mHandler.post(new Runnable() {
                public void run() {
                    mWebView.loadUrl("javascript:wave(\"fromAndroid"+para+"\")");// 调用脚本函数
                }
            });
        }
		public void onload() {
            mHandler.post(new Runnable() {
                public void run() {
                    onInputFingerPrint();
                }
            });
        }
		public void back(final String para) {
            mHandler.post(new Runnable() {
                public void run() {
                	Intent intent = new Intent();
					intent.setClass(HTMLFigureInputActivity.this, HTMLCheckSuccessful.class);
					intent.putExtra("str", "come from first activity");
					startActivity(intent);//无返回值的调用,启动一个明确的activity
                }
            });
        }
	}
}
