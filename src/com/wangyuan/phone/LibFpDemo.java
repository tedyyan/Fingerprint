package com.wangyuan.phone;

import com.hdsoft.fingerprint.LibFp;
import com.wangyuan.phone.R;
import com.wangyuan.phone.R.id;
import com.wangyuan.phone.R.layout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
//import android.util.Log;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class LibFpDemo extends Activity {
	private static final int MSG_CMD    = 0;
	private static final int MSG_EXIT   = 1;
	private static final int MSG_INFO   = 2;
	private static final int MSG_FIND   = 3;
	private static final int MSG_CANCEL = 4;
	
	private boolean bContinue = true;	
	
	private Button btOpen, btImage, btEnrol, btMatch, btEmpty, btCancel;
	private TextView tvInfo;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        
        setContentView(R.layout.activity_main);        
        
        tvInfo   = (TextView)findViewById(R.id.tvInfo); 
        btOpen   = (Button)findViewById(R.id.btOpen);
        btImage  = (Button)findViewById(R.id.btImage);
        btEnrol  = (Button)findViewById(R.id.btEnrol);
        btMatch  = (Button)findViewById(R.id.btMatch);
        btEmpty  = (Button)findViewById(R.id.btEmpty);
        btCancel = (Button)findViewById(R.id.btCancel);
        final ImageView iv = (ImageView)findViewById(R.id.imageView1);
        
        final Handler handler = new Handler() {   
	    	public void handleMessage(Message msg) { 
	    		switch (msg.what) {   
	    		case MSG_CMD:
	    			tvInfo.setText(LibFp.GetError(msg.arg1));
	    			if (msg.arg1 == LibFp.FP_OK) {
	    				optFinish();
	    			}
	    			else if (msg.arg1 == LibFp.FP_ERROR_DRIVE) {
	    				closeDrive();
	    			}
	    			break;
	    		case MSG_EXIT:
	    			tvInfo.setText(LibFp.GetError(msg.arg1));
	    			if (msg.arg1 == LibFp.FP_ERROR_DRIVE) {
	    				closeDrive();
	    			}
	    			else {
	    				optFinish();
	    			}
	    			break;
	    		case MSG_INFO:
	    			switch (msg.arg1) {
	    			case 0:
	    				tvInfo.setText("录入指纹, 请按手指...");
	    				break;
	    			case 1:
	    				tvInfo.setText("请移开手指...");
	    				break;
	    			case 2:
	    				tvInfo.setText("指纹成功入库, 地址编号 = " + msg.arg2 + ", 请按手指继续录入...");
	    				break;
	    			default:
	    				break;
	    			}
	    			break;
	    		case MSG_FIND:
	    			optFinish();
	    			tvInfo.setText("指纹匹配成功, 存放地址 = " + msg.arg1 + "比对得分  = " + msg.arg2);
	    			break;
	    		case MSG_CANCEL:
	    			bContinue = true;
	    			optFinish();
					switch (msg.arg1) {
	    			case 0:
	    				tvInfo.setText("操作已取消...");
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
	    
  
        btOpen.setOnClickListener(new View.OnClickListener() 
	    {
			@Override
			public void onClick(View v) 
			{	
				int nRet = LibFp.FpOpenEx((short)0x2109, (short)0x7638);
				if (nRet == LibFp.FP_ERROR_OPEN) {
					LibFp.GetRootRight();
					nRet = LibFp.FpOpenEx((short)0x2109, (short)0x7638);					
				}
				if (nRet == LibFp.FP_OK) {
					openDrive();
				}
				else {
					tvInfo.setText(LibFp.GetError(nRet));
				}				
			}
		});
        btImage.setOnClickListener(new View.OnClickListener() 
	    {
			@Override
			public void onClick(View v) 
			{
				optStart();
				bContinue = true;
				new Thread(new Runnable() {
			        public void run() {
			        	byte bmpData[] = new byte[256*288+1078];
			        	int  bmpSize[] = new int[1];
			        	while (bContinue) {	        			
			        		int nRet = LibFp.FpGetImage(0xffffffff, 10000);
			        		if (nRet == LibFp.FP_OK) {
			        			if ((nRet = LibFp.FpUpBMP(0xffffffff, bmpData, 256*288 + 1078, bmpSize, 10000)) == LibFp.FP_OK) {
			        				final Bitmap bm = BitmapFactory.decodeByteArray(bmpData, 0, 256*288 + 1078);
			        				iv.post(new Runnable() {
			        					public void run() {
			        						iv.setImageBitmap(bm);
			        					}
			        				});
			        			}
			        		}
			        		sendInfo(handler, MSG_CMD, nRet);
			        		if (nRet == LibFp.FP_NO_FINGER) {
			        			try {
			        				Thread.sleep(150);
				                } catch (InterruptedException e) {
				                }
			        		}
			        		else {
			        			return;
			        		}
			        	}
			        	sendInfo(handler, MSG_CANCEL, 0);
			        }
			    }).start();
			}
		});
        btEnrol.setOnClickListener(new View.OnClickListener() 
	    {
			@Override
			public void onClick(View v) 
			{
				optStart();
				bContinue = true;
				new Thread(new Runnable() {
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
						        			iv.post(new Runnable() {
						        				public void run() {
						        					iv.setImageBitmap(bm);
						        				}
						        			});						        		
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
			    }).start();
			}
		});
        btMatch.setOnClickListener(new View.OnClickListener() 
	    {
			@Override
			public void onClick(View v) 
			{
				optStart();
				bContinue = true;
				new Thread(new Runnable() {
			        public void run() {
			        	byte bmpData[] = new byte[256*288+1078];
			        	int  bmpSize[] = new int[1];
			        	short nNum[] = new short[1];
			        	short nAddr[] = new short[1];
						short nScore[] = new short[1];
			        	if (LibFp.FpValidTempleteNum(0xffffffff, nNum, 10000) != LibFp.FP_OK) return;
			        	while (bContinue) {
			        		int nRet = LibFp.FpGetImage(0xffffffff, 10000);
			        		if (nRet == LibFp.FP_OK) {
			        			if ((nRet = LibFp.FpGenChar(0xffffffff, (byte)1, 10000)) == LibFp.FP_OK) {			        				
			        				if ((nRet = LibFp.FpGetImage(0xffffffff, 10000)) == LibFp.FP_OK) {
			        					if ((nRet = LibFp.FpUpBMP(0xffffffff, bmpData, 256*288 + 1078, bmpSize, 10000)) == LibFp.FP_OK) {
						        			final Bitmap bm = BitmapFactory.decodeByteArray(bmpData, 0, 256*288 + 1078);
						        			iv.post(new Runnable() {
						        				public void run() {
						        					iv.setImageBitmap(bm);
						        				}
						        			});	
			        						if ((nRet = LibFp.FpSearch(0xffffffff, (byte)1, (short)0, nNum[0], nAddr, nScore, 10000)) == LibFp.FP_OK) {		    								
			        							sendInfo(handler, MSG_FIND, nAddr[0], nScore[0]);
			    							}
			        						else {
			        							sendInfo(handler, MSG_EXIT, nRet);
			        						}
			        						return;
			        					}
			        				}
			        			}		        				
			        		}
			        		if (nRet == LibFp.FP_NO_FINGER) {
			        			sendInfo(handler, MSG_CMD, nRet);
			        		}
			        		else if (nRet != LibFp.FP_OK) {
			        			sendInfo(handler, MSG_EXIT, nRet);
			        			return;
			        		}
			        	}
			        	sendInfo(handler, MSG_CANCEL, 0);
			        }
			    }).start();
			}
		});
        btEmpty.setOnClickListener(new View.OnClickListener() 
	    {
			@Override
			public void onClick(View v) 
			{
				int nRet = LibFp.FpEmpty(0xffffffff, 10000);
				tvInfo.setText(LibFp.GetError(nRet));
			}
		});
        btCancel.setOnClickListener(new View.OnClickListener() 
	    {
			@Override
			public void onClick(View v) 
			{
				bContinue = false;	
			}
		});
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onResume()
    {
        super.onResume();
    }
    @Override
    public void onPause()
    {
    	bContinue = false;
    	try {
			Thread.sleep(600);
        } catch (InterruptedException e) {
        }
        closeDrive();
    	super.onPause();
    	LibFp.FpClose();
    }
    
    private void sendInfo(Handler handler, int nMsg, int arg1){
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
    private void optStart() {
    	btImage.setEnabled(false);
		btEnrol.setEnabled(false);
		btMatch.setEnabled(false);
		btEmpty.setEnabled(false);
		btCancel.setEnabled(true);
    }
    private void optFinish() {
    	btImage.setEnabled(true);
		btEnrol.setEnabled(true);
		btMatch.setEnabled(true);
		btEmpty.setEnabled(true);
		btCancel.setEnabled(false);
    }
    private void openDrive() {
    	btOpen.setEnabled(false);
		btImage.setEnabled(true);
		btEnrol.setEnabled(true);
		btMatch.setEnabled(true);
		btEmpty.setEnabled(true);
		btCancel.setEnabled(false);
		tvInfo.setText("设备已打开...");
    }
    private void closeDrive() {
    	btOpen.setEnabled(true);
    	btImage.setEnabled(false);
		btEnrol.setEnabled(false);
		btMatch.setEnabled(false);
		btEmpty.setEnabled(false);
		btCancel.setEnabled(false);
		tvInfo.setText("已关闭设备，请重新打开...");
    }
  }
