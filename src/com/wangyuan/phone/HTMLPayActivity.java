package com.wangyuan.phone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import com.hdsoft.fingerprint.LibFp;

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

public class HTMLPayActivity extends Activity {
	WebView mWebView;
	Handler mHandler;
	
	@Override
	 public void onCreate(Bundle savedInstanceState)  {
		 super.onCreate(savedInstanceState); 

	     setContentView(R.layout.htmlpay);   	        
	     mWebView = (WebView) this.findViewById(R.id.webViewPay);
	     mHandler = new Handler();
	     WebSettings webSettings = mWebView.getSettings();
	     webSettings.setJavaScriptEnabled(true);
	     mWebView.addJavascriptInterface(new JavaScriptHandler(), "pay"); 
	     mWebView.loadUrl("file:///android_asset/html/0301denglu.htm");
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
	
	class HMTLProxy
	{
		void putZhiFuText(String zhifuText)
		{
			mWebView.loadUrl("javascript:setZhiFuButtonText(\""+zhifuText+"\")");
		}
		void putYuEText(String yuEText)
		{
			mWebView.loadUrl("javascript:setYuEText(\""+yuEText+"\")");
		}
		void enableSuccessfulButton()
		{
			mWebView.loadUrl("javascript:enableSuccessfulButton()");
		}
		void disableSuccessfulButton()
		{
			mWebView.loadUrl("javascript:disableSuccessfulButton()");
		}
		void putHintText(String hintText)
		{
			mWebView.loadUrl("javascript:setHintText(\""+hintText+"\")");
		}
	}
	
	class JavaScriptHandler {
		public void clickOnZhiFu() {
            mHandler.post(new Runnable() {
                public void run() {
                	actionsHandler.onClickPay();
                }
            });
        }
		public void clickOnSetting() {
            mHandler.post(new Runnable() {
                public void run() {
                	actionsHandler.onClickSetting();
                }
            });
        }
		public void clickOnCheckSuccessfully() {
            mHandler.post(new Runnable() {
                public void run() {
                	actionsHandler.onClickPaySuccessfully();
                }
            });
        }
		public void clickOnCheckFailed() {
            mHandler.post(new Runnable() {
                public void run() {
                	actionsHandler.onClickPayFailed();
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
		public void onload() {
			mHandler.post(new Runnable() {
				public void run() {
					actionsHandler.onClickPay();
				}
			});
		}
    }
	
	private static final int MSG_CMD = 0;
	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILED = 2;
	private static final int MSG_EXPIRED = 3;
	private static final int MSG_EXIT = 4;
	private boolean timeIsOpen = false;
	private boolean bContinue = true;
	private boolean fgIsOpen = false;
	HMTLProxy htmlProxy = new HMTLProxy();
	final ActionsHandler actionsHandler = new ActionsHandler();
	private void sendInfo(Handler handler, int nMsg, int arg1) {
		Message msg = handler.obtainMessage();
		msg.what = nMsg;
		msg.arg1 = arg1;
		handler.sendMessage(msg);
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
			htmlProxy.putZhiFuText("请插入网元电子钱包卡！");
		}
		else
		{
			htmlProxy.putZhiFuText("点击后，录入指纹");
			Log.d("fingerprint","fingerprint input!");
		}
		mWebView.requestFocus();
	}

	public void onPause() {
		bContinue = false;
		try {
			Thread.sleep(600);
		} catch (InterruptedException e) {
		}
		timeIsOpen = false;
		if (fgIsOpen)
		{
			LibFp.FpClose();
			fgIsOpen = false;
		}
		super.onPause();
	}

	protected boolean checkSD() {
		return true;
		/*
		File file = android.os.Environment.getExternalStorageDirectory();
		if (file != null) {
			String path = file.getAbsolutePath();
			try {
				FileInputStream fileIS = new FileInputStream(path
						+ "/wangyuan.txt");
				BufferedReader buf = new BufferedReader(new InputStreamReader(
						fileIS));
				String readString = new String();
				// just reading each line and pass it on the debugger
				// while ((readString = buf.readLine()) != null) {
				readString = buf.readLine();
				if (readString != null) {
					Log.d("line: ", readString);
					htmlProxy.putYuEText(readString);
					fileIS.close();
					return true;
				}
				fileIS.close();
			} catch (FileNotFoundException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		
		htmlProxy.putYuEText("0");
		htmlProxy.putZhiFuText("请插入网元电子钱包卡！再点击");
		return false;
		*/
	}

	class MessageHandler extends Handler {
		public void handleMessage(Message msg) {
			Intent intent;
			switch (msg.what) {
			case MSG_SUCCESS:

				htmlProxy.putZhiFuText("指纹验证成功，请刷卡！");
				//htmlProxy.enableSuccessfulButton();
				intent = new Intent();
				intent.setClass(HTMLPayActivity.this, HTMLCheckSuccessful.class);
				intent.putExtra("str", "come from first activity");
				startActivity(intent);//无返回值的调用,启动一个明确的activity
				break;
			case MSG_FAILED:
				htmlProxy.putZhiFuText("指纹验证失败，点击重新验证!");
				//htmlProxy.disableSuccessfulButton();
				//mWebView.loadUrl("file:///android_asset/html/checkFailed.html");
				intent = new Intent();
				intent.setClass(HTMLPayActivity.this, HTMLCheckFailed.class);
				intent.putExtra("str", "come from first activity");
				startActivity(intent);//无返回值的调用,启动一个明确的activity
				break;
			case MSG_EXPIRED:
				if (timeIsOpen) {
					if (fgIsOpen) {
						LibFp.FpClose();
						fgIsOpen = false;
					}
					htmlProxy.putZhiFuText("点击后，录入指纹");
					htmlProxy.disableSuccessfulButton();
				}
				break;
			case MSG_CMD:
			case MSG_EXIT:
				htmlProxy.putHintText(LibFp.GetError(msg.arg1));
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	}

	final Handler handler = new MessageHandler();

	class PayThread implements Runnable {
		public void run() {
			byte bmpData[] = new byte[256 * 288 + 1078];
			int bmpSize[] = new int[1];
			short nNum[] = new short[1];
			short nAddr[] = new short[1];
			short nScore[] = new short[1];
			if (LibFp.FpValidTempleteNum(0xffffffff, nNum, 10000) != LibFp.FP_OK) {

				sendInfo(handler, MSG_EXIT, 0x102);
				return;
			}
			while (bContinue) {
				int nRet = LibFp.FpGetImage(0xffffffff, 10000);
				if (nRet == LibFp.FP_OK) {
					if ((nRet = LibFp.FpGenChar(0xffffffff, (byte) 1, 10000)) == LibFp.FP_OK) {
						if ((nRet = LibFp.FpGetImage(0xffffffff, 10000)) == LibFp.FP_OK) {
							if ((nRet = LibFp.FpUpBMP(0xffffffff, bmpData,
									256 * 288 + 1078, bmpSize, 10000)) == LibFp.FP_OK) {
								final Bitmap bm = BitmapFactory
										.decodeByteArray(bmpData, 0,
												256 * 288 + 1078);
								nRet = LibFp.FpSearch(0xffffffff,(byte) 1, (short) 0, nNum[0], nAddr,
										nScore, 10000);
								if ( nRet == LibFp.FP_OK) {
									sendInfo(handler, MSG_SUCCESS, nRet);
									new Thread(new ExpiredThread()).start();
									timeIsOpen = true;
								} else {
									sendInfo(handler, MSG_FAILED, nRet);
								}
								return;
							}
						}
					}
				}

				if (nRet == LibFp.FP_NO_FINGER) {
					sendInfo(handler, MSG_CMD, nRet);
				} else if (nRet != LibFp.FP_OK) {
					sendInfo(handler, MSG_EXIT, nRet);
					return;
				}

			}
		}
	}

	final class ExpiredThread implements Runnable {
		@Override
		public void run() {
			int i = 0;
			// TODO Auto-generated method stub
			while (timeIsOpen) {
				try {

					Thread.sleep(5000);// 线程暂停10秒，单位毫秒
					Message message = new Message();
					message.what = 1;
					if (i > 60) {
						sendInfo(handler, MSG_EXPIRED, 0);// 发送消息
						return;
					}
					i = i + 1;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	class ActionsHandler {
		void onClickSetting() {
			Intent intent = new Intent();
			intent.setClass(HTMLPayActivity.this, LibFpDemo.class);
			intent.putExtra("str", "come from first activity");
			startActivity(intent);// 无返回值的调用,启动一个明确的activity
		}

		void onClickPaySuccessfully() {
			Intent intent = new Intent();
			intent.setClass(HTMLPayActivity.this, HTMLCheckSuccessful.class);
			intent.putExtra("str", "come from first activity");
			startActivity(intent);// 无返回值的调用,启动一个明确的activity
		}
		void onClickPayFailed() {
			Intent intent = new Intent();
			intent.setClass(HTMLPayActivity.this, HTMLCheckFailed.class);
			intent.putExtra("str", "come from first activity");
			startActivity(intent);// 无返回值的调用,启动一个明确的activity
		}
		void onClickPay() {
			if (!checkSD()) {
				Log.d("fingerprint","SD Card check is failed!");
				return;
			}
			if (!fgIsOpen) {
				int nRet = LibFp.FpOpenEx((short) 0x2109, (short) 0x7638);
				if (nRet == LibFp.FP_ERROR_OPEN) {
					LibFp.GetRootRight();
					nRet = LibFp.FpOpenEx((short) 0x2109, (short) 0x7638);
				}

				if (nRet == LibFp.FP_OK) {
					Log.d("fingerprint","fingerprint device open successfully!");
					htmlProxy.putHintText("打开设备成功！");
					fgIsOpen = true;
					new Thread(new PayThread()).start();
				} else {
					Log.d("fingerprint","fingerprint device open failed!");
					htmlProxy.putHintText(LibFp.GetError(nRet));
					try {
						Thread.sleep(600);
					} catch (InterruptedException e) {
					}
					//LibFp.FpClose();
				}
			}
		}
	}
}
