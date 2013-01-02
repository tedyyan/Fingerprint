/**
 * 
 */
package com.wangyuan.phone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import com.hdsoft.fingerprint.LibFp;
import com.wangyuan.phone.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author teddy yan
 * 
 */
public class DemoHomeActivity extends Activity {
	private Button btZhifu, btSetting, btSuccess, btMatch, btEmpty, btCancel;
	private TextView tvInfo, txtYuE;
	EditText etMemony;
	private static final int MSG_CMD = 0;
	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAILED = 2;
	private static final int MSG_EXPIRED = 3;
	private static final int MSG_EXIT = 4;
	private boolean timeIsOpen = false;
	private boolean bContinue = true;
	private boolean fgIsOpen = false;

	private void sendInfo(Handler handler, int nMsg, int arg1) {
		Message msg = handler.obtainMessage();
		msg.what = nMsg;
		msg.arg1 = arg1;
		handler.sendMessage(msg);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.demohome);

		tvInfo = (TextView) findViewById(R.id.demohome_msg);
		txtYuE = (TextView) findViewById(R.id.txtYuE);
		btZhifu = (Button) findViewById(R.id.btZhiFu);
		btSetting = (Button) findViewById(R.id.btSetting);
		btSuccess = (Button) findViewById(R.id.btSuccess);

		etMemony = (EditText) findViewById(R.id.editMomey);


		final ActionsHandler actionsHandler = new ActionsHandler();
		
		btZhifu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				actionsHandler.onClickPay();
			}
		});

		btSetting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				actionsHandler.onClickSetting();
				// startActivityForResult(intent, REQUEST_CODE);
			}
		});
		btSuccess.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				actionsHandler.onClickPaySuccessfully();
			}
		});
	}

	@Override
	protected void onResume() {
		
		btSuccess.setEnabled(false);
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
		
		}
		else
		{
			btZhifu.setText(R.string.demohome_zhiwenlab);
			btZhifu.performClick();
		}
	}

	public void onPause() {
		bContinue = false;
		try {
			Thread.sleep(600);
		} catch (InterruptedException e) {
		}
		timeIsOpen = false;
		super.onPause();
		LibFp.FpClose();
	}

	protected boolean checkSD() {
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
					txtYuE.setText(readString);
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

		txtYuE.setText("0");
		btZhifu.setText("请插入网元电子钱包卡！再点击");
		return false;
	}

	class MessageHandler extends Handler {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:

				btZhifu.setText("指纹验证成功，请刷卡！");
				btSuccess.setEnabled(true);
				break;
			case MSG_FAILED:
				btZhifu.setText("指纹验证失败，点击重新验证!");
				btSuccess.setEnabled(false);
				break;
			case MSG_EXPIRED:
				if (timeIsOpen) {
					if (fgIsOpen) {
						LibFp.FpClose();
						fgIsOpen = false;
					}
					btZhifu.setText(R.string.demohome_zhiwenlab);
					btSuccess.setEnabled(false);
				}
				break;
			case MSG_CMD:
			case MSG_EXIT:
				tvInfo.setText(LibFp.GetError(msg.arg1));
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

			intent.setClass(DemoHomeActivity.this, LibFpDemo.class);

			intent.putExtra("str", "come from first activity");

			startActivity(intent);// 无返回值的调用,启动一个明确的activity

		}

		void onClickPaySuccessfully() {
			Intent intent = new Intent();

			intent.setClass(DemoHomeActivity.this, PaySuccess.class);

			intent.putExtra("str", "come from first activity");

			startActivity(intent);// 无返回值的调用,启动一个明确的activity
		}

		void onClickPay() {
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
					tvInfo.setText("打开设备成功！");
					fgIsOpen = true;
				} else {
					tvInfo.setText(LibFp.GetError(nRet));
					try {
						Thread.sleep(600);
					} catch (InterruptedException e) {
					}
					LibFp.FpClose();
				}
			}
			new Thread(new PayThread()).start();
		}
	}
}
