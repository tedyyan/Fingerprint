package com.hdsoft.fingerprint;

public class LibFp {
	public static final int FP_OK          = 0x0000;
	public static final int FP_NO_FINGER   = 0x0002;
	public static final int FP_ERROR_OPEN  = 0x0102;
	public static final int FP_ERROR_DRIVE = 0x0104;
	
	static {
		System.loadLibrary("fp");
	}

	public static String GetError(int nCode)
	{
		String strText;
		switch (nCode)
		{  
		case 0:
			strText = "执行成功";
			break;
		case 1:
			strText = "数据包接收错误";
			break;
		case 2:
			strText = "传感器上没有手指";
			break;
		case 3:
			strText = "录入指纹图象失败";
			break;
		case 4:
			strText = "指纹太淡";
			break;
		case 5:
			strText = "指纹太糊";
			break;
		case 6:
			strText = "指纹太乱";
			break;
		case 7:
			strText = "指纹特征点太少";
			break;
		case 8:
			strText = "指纹不匹配";
			break;
		case 9:
			strText = "没搜索到指纹";
			break;
		case 10:
			strText = "特征合并失败";
			break;
		case 11:
			strText = "地址号超出指纹库范围";
			break;
		case 12:
			strText = "从指纹库读模板出错";
			break;
		case 13:
			strText = "上传特征失败";
			break;
		case 14:
			strText = "模块不能接收后续数据包";
			break;
		case 15:
			strText = "上传图象失败";
			break;
		case 16:
			strText = "删除模板失败";
			break;
		case 17:
			strText = "清空指纹库失败";
			break;
		case 18:
			strText = "不能进入休眠";
			break;
		case 19:
			strText = "口令不正确";
			break;
		case 20:
			strText = "系统复位失败";
			break;
		case 21:
			strText = "无效指纹图象";
			break;
		case 22:
			strText = "在线升级失败";
			break;
		case 23:
			strText = "残留或未移动";
			break;
		case 24:
			strText = "表示读写FLASH出错";
			break;
		case 25:
			strText = "未定义错误";
			break;
		case 26:
			strText = "无效寄存器号";
			break;
		case 27:
			strText = "寄存器设定内容错误号";
			break;
		case 28:
			strText = "记事本页码指定错误";
			break;
		case 29:
			strText = "端口操作失败";
			break;
		case 30:
			strText = "自动注册（enroll）失败";
			break;
		case 31:
			strText = "指纹库满";
			break;
		case 0xf0:
			strText = "有后续数据包的指令，正确接收后用0xf0应答";
			break;
		case 0xf1:
			strText = "有后续数据包的指令，命令包用0xf1应答";
			break;
		case 0xf2:
			strText = "表示烧写内部FLASH时，校验和错误";
			break;	
		case 0xf3:
			strText = "表示烧写内部FLASH时，包标识错误";
			break;
		case 0xf4:
			strText = "表示烧写内部FLASH时，包长度错误";
			break;
		case 0xf5:
			strText = "表示烧写内部FLASH时，代码长度太长";
			break;
		case 0xf6:
			strText = "表示烧写内部FLASH时，烧写FLASH失败";
			break;
		case 0x20:
			strText = "空操作";
			break;	
		case 0x101:
			strText = "驱动初始化失败";
			break;
		case 0x102:
			strText = "驱动打开失败";
			break;
		case 0x103:
			strText = "驱动打开失败";
			break;
		case 0x104:
			strText = "驱动未打开";
			break;
		case 0x105:
			strText = "连接内核驱动失败";
			break;
		case 0x106:
			strText = "获取驱动列表失败";
			break;
		case 0x107:
			strText = "获取驱动描叙失败";
			break;
		case 0x200:
			strText = "传输数据失败";
			break;
		case 0x301:
			strText = "应答包校验出错";
			break;
		case 0x302:
			strText = "命令编码失败";
			break;
		case 0x303:
			strText = "数据缓冲区太小";
			break;
		case 0x304:
			strText = "打开文件失败";
			break;
		default:
			strText = "未知错误";
			break;
		}
		return strText +"["+ nCode +"]";
	}
	/***********************************************************************************************************
	* 函数名称: void GetVersion();
	* 函数功能: 获取开发包版本号
	* 返回参数: 版本号
	***********************************************************************************************************/
	public native int GetVersion();
	 
	/***********************************************************************************************************
	* 函数名称: void GetRootRight();
	* 函数功能: 获取打开驱动的Root权限
	* 返回参数: 无
	***********************************************************************************************************/
	public static native void GetRootRight();
	
	/***********************************************************************************************************
	* 函数名称: int FpOpen();
	* 函数功能: 打开驱动
	* 返回参数: 执行结果
	***********************************************************************************************************/
	public static native int FpOpen();

	/***********************************************************************************************************
	* 函数名称: int  FpOpenEx(uint16_t idVendor, uint16_t idProduct);
	* 函数功能: 根据用户ID及产品ID打开驱动
	* 输入参数: 用户ID
	* 输入参数: 产品ID
	* 返回参数: 执行结果
	***********************************************************************************************************/
	public static native int  FpOpenEx(short idVendor, short idProduct);
	
	/***********************************************************************************************************
	* 函数名称: void FpClose();
	* 函数功能: 关闭驱动
	* 返回参数: 无
	***********************************************************************************************************/
	public static native void FpClose();

	/***********************************************************************************************************
	* 函数名称: int FpGetImage(uint32_t nAddr, uint32_t timeout)
	* 函数功能: 探测手指，探测到后录入指纹图像存于ImageBuffer
	* 输入参数: 芯片地址
	* 输入参数: 等候时间
	* 返回参数: 执行结果
	***********************************************************************************************************/
	public static native int FpGetImage(int nAddr, int timeout);

	/***********************************************************************************************************
	* 函数名称: int FpGenChar(uint32_t nAddr, uint8_t nBufferId, uint32_t timeout)
	* 函数功能: 将ImageBuffer 中的原始图像生成指纹特征文件存于CharBuffer1 或CharBuffer2
	* 输入参数: 芯片地址
	* 输入参数: 特征缓冲区号，要求为CharBuffer1=1或CharBuffer2=2
	* 输入参数: 等候时间
	* 返回参数: 执行结果
	***********************************************************************************************************/
	public static native int FpGenChar(int nAddr, byte nBufferId, int timeout);

	/***********************************************************************************************************
	* 函数名称: int FpMatch(uint32_t nAddr, uint16_t *pScore, uint32_t timeout)
	* 函数功能: 精确比对CharBuffer1 与CharBuffer2 中的特征文件
	* 输入参数: 芯片地址
	* 输入参数: 比对得分
	* 输入参数: 等候时间
	* 返回参数: 执行结果
	***********************************************************************************************************/
	public static native int FpMatch(int nAddr, short[] pScore, int timeout);

	/***********************************************************************************************************
	* 函数名称: int FpSearch(uint32_t nAddr, uint8_t nBufferId, uint16_t nStartPage, uint16_t nPageNum,
	*                        uint16_t *pPageId, uint16_t *pScore, uint32_t timeout)
	* 函数功能: 以CharBuffer1或CharBuffer2中的特征文件搜索指定指纹库
	* 输入参数: 芯片地址
	* 输入参数: 特征缓冲区号，要求为CharBuffer1=1或CharBuffer2=2
	* 输入参数: 搜索起始页
	* 输入参数: 搜索页数
	* 输入参数: 匹配页码
	* 输入参数: 比对得分
	* 输入参数: 等候时间
	* 返回参数: 执行结果
	***********************************************************************************************************/
	public static native int FpSearch(int nAddr, byte nBufferId, short nStartPage, short nPageNum, short[] pPageId, short[] pScore, int timeout);

	/***********************************************************************************************************
	* 函数名称: int FpRegModel(uint32_t nAddr, uint32_t timeout)
	* 函数功能: 将CharBuffer1与CharBuffer2中的特征文件合并生成模板，结果存于CharBuffer1与CharBuffer2
	* 输入参数: 芯片地址
	* 输入参数: 等候时间
	* 返回参数: 执行结果
	***********************************************************************************************************/
	public static native int FpRegModel(int nAddr, int timeout);

	/***********************************************************************************************************
	* 函数名称: int FpStoreChar(uint32_t nAddr, uint8_t nBufferId, uint16_t nPageId, uint32_t timeout)
	* 函数功能: 将CharBuffer1或CharBuffer2中的模板文件存到PageId号Flash 数据库位置
	* 输入参数: 芯片地址
	* 输入参数: 特征缓冲区号，要求为CharBuffer1=1或CharBuffer2=2
	* 输入参数: 指纹库位置号
	* 输入参数: 等候时间
	* 返回参数: 执行结果
	***********************************************************************************************************/
	public static native int FpStoreChar(int nAddr, byte nBufferId, short nPageId, int timeout);

	/***********************************************************************************************************
	* 函数名称: int FpLoadChar(uint32_t nAddr, uint8_t nBufferId, uint16_t nPageId, uint32_t timeout)
	* 函数功能: 将Flash数据库中指定ID号的指纹模板读入到模板缓冲区CharBuffer1或 CharBuffer2
	* 输入参数: 芯片地址
	* 输入参数: 特征缓冲区号，要求为CharBuffer1=1或CharBuffer2=2
	* 输入参数: 指纹库位置号
	* 输入参数: 等候时间
	* 返回参数: 执行结果
	***********************************************************************************************************/
	public static native int FpLoadChar(int nAddr, byte nBufferId, short nPageId, int timeout);

	/***********************************************************************************************************
	* 函数名称: int FpUpChar(uint32_t nAddr, uint8_t nBufferId, uint8_t *pBuf, int nBufSize, int *pBytesReturned, uint32_t timeout)
	* 函数功能: 将特征缓冲区中的特征文件上传给上位机
	* 输入参数: 芯片地址
	* 输入参数: 特征缓冲区号，要求为CharBuffer1=1或CharBuffer2=2
	* 输入参数: 缓冲区地址
	* 输入参数: 缓冲区大小
	* 输入参数: 实际大小
	* 输入参数: 等候时间
	* 返回参数: 执行结果
	***********************************************************************************************************/
	public static native int FpUpChar(int nAddr, byte nBufferId, byte[] pBuf, int nBufSize, int[] pBytesReturned, int timeout);

	/***********************************************************************************************************
	* 函数名称: int FpDownChar(uint32_t nAddr, uint8_t nBufferId, uint8_t *pBuf, int nBufSize, uint32_t timeout)
	* 函数功能: 上位机下载特征文件到模块的一个特征缓冲区
	* 输入参数: 芯片地址
	* 输入参数: 特征缓冲区号，要求为CharBuffer1=1或CharBuffer2=2
	* 输入参数: 缓冲区地址
	* 输入参数: 缓冲区大小
	* 输入参数: 等候时间
	* 返回参数: 执行结果
	***********************************************************************************************************/
	public static native int FpDownChar(int nAddr, byte nBufferId, byte[] pBuf, int nBufSize, int timeout);

	/***********************************************************************************************************
	* 函数名称: int FpUpImage(uint32_t nAddr, uint8_t *pBuf, int nBufSize, int *pBytesReturned, uint32_t timeout)
	* 函数功能: 将图像缓冲区中的数据上传给上位机
	* 输入参数: 芯片地址
	* 输入参数: 缓冲区地址
	* 输入参数: 缓冲区大小
	* 输入参数: 实际大小
	* 输入参数: 等候时间
	* 返回参数: 执行结果
	***********************************************************************************************************/
	public static native int FpUpImage(int nAddr, byte[] pBuf, int nBufSize, int[] pBytesReturned, int timeout);
	
	/***********************************************************************************************************
	* 函数名称: int FpUpBMP(uint32_t nAddr, uint8_t *pBuf, int nBufSize, int *pBytesReturned, uint32_t timeout)
	* 函数功能: 将图像缓冲区中的数据转换为BMP上传给上位机
	* 输入参数: 芯片地址
	* 输入参数: 缓冲区地址
	* 输入参数: 缓冲区大小
	* 输入参数: 实际大小
	* 输入参数: 等候时间
	* 返回参数: 执行结果
	***********************************************************************************************************/
	public static native int FpUpBMP(int nAddr, byte[] pBuf, int nBufSize, int[] pBytesReturned, int timeout);

	/***********************************************************************************************************
	* 函数名称: int FpDownImage(uint32_t nAddr, uint8_t *pBuf, int nBufSize, uint32_t timeout)
	* 函数功能: 将图像缓冲区中的数据上传给上位机
	* 输入参数: 芯片地址
	* 输入参数: 缓冲区地址
	* 输入参数: 缓冲区大小
	* 输入参数: 等候时间
	* 返回参数: 执行结果
	***********************************************************************************************************/
	public static native int FpDownImage(int nAddr, byte[] pBuf, int nBufSize, int timeout);

	/***********************************************************************************************************
	* 函数名称: int FpDeletChar(uint32_t nAddr, uint16_t nStartPage, uint16_t nPageNum, uint32_t timeout)
	* 函数功能: 删除Flash数据库中指定ID号开始的N个指纹模板
	* 输入参数: 芯片地址
	* 输入参数: 起始页
	* 输入参数: 页数
	* 输入参数: 等候时间
	* 返回参数: 执行结果
	***********************************************************************************************************/
	public static native int FpDeletChar(int nAddr, short nStartPage, short nPageNum, int timeout);

	/***********************************************************************************************************
	* 函数名称: int FpEmpty(uint32_t nAddr, uint32_t timeout)
	* 函数功能: 删除Flash 数据库中所有指纹模板
	* 输入参数: 芯片地址
	* 输入参数: 等候时间
	* 返回参数: 执行结果
	***********************************************************************************************************/
	public static native int FpEmpty(int nAddr, int timeout);

	/***********************************************************************************************************
	* 函数名称: int FpWriteReg(uint32_t nAddr, uint8_t nReg, uint8_t nValue, uint32_t timeout)
	* 函数功能: 写模块寄存器
	* 输入参数: 芯片地址
	* 输入参数: 寄存器地址
	* 输入参数: 寄存器数值
	* 输入参数: 等候时间
	* 返回参数: 执行结果
	***********************************************************************************************************/
	public static native int FpWriteReg(int nAddr, byte nReg, byte nValue, int timeout);

	/***********************************************************************************************************
	* 函数名称: int FpReadSysParam(uint32_t nAddr, uint8_t *pBuf, uint8_t nBufSize, uint8_t *pBytesReturned, uint32_t timeout)
	* 函数功能: 读取模块的基本参数,固定16字节
	* 输入参数: 芯片地址
	* 输入参数: 缓冲区地址
	* 输入参数: 缓冲区大小
	* 输入参数: 实际大小
	* 输入参数: 等候时间
	* 返回参数: 执行结果
	***********************************************************************************************************/
	public static native int FpReadSysParam(int nAddr, byte[] pBuf, int nBufSize, int[] pBytesReturned, int timeout);

	/***********************************************************************************************************
	* 函数名称: int FpEnroll(uint32_t nAddr, uint16_t *pPageId, uint32_t timeout)
	* 函数功能: 采集一次指纹注册模板，在指纹库中搜索空位并存储，返回存储ID
	* 输入参数: 芯片地址
	* 输入参数: 存储页码
	* 输入参数: 等候时间
	* 返回参数: 执行结果
	***********************************************************************************************************/
	public static native int FpEnroll(int nAddr, short[] pPageId, int timeout);

	/***********************************************************************************************************
	* 函数名称: int FpIdentify(uint32_t nAddr, uint16_t *pPageId, uint16_t *pScore, uint32_t timeout)
	* 函数功能: 自动采集指纹，在指纹库中搜索目标模板并返回搜索结果
	* 输入参数: 芯片地址
	* 输入参数: 存储页码
	* 输入参数: 比对得分
	* 输入参数: 等候时间
	* 返回参数: 执行结果
	***********************************************************************************************************/
	public static native int FpIdentify(int nAddr, short[] pPageId, short[] pScore, int timeout);

	/***********************************************************************************************************
	* 函数名称: int FpSetPwd(uint32_t nAddr, uint8_t *pBuf, int nBufSize, uint32_t timeout)
	* 函数功能: 设置模块握手口令
	* 输入参数: 芯片地址
	* 输入参数: 密码缓冲区地址
	* 输入参数: 密码缓冲区大小,要求为4
	* 输入参数: 等候时间
	* 返回参数: 执行结果
	***********************************************************************************************************/
	public static native int FpSetPwd(int nAddr, byte[] pBuf, int nBufSize, int timeout);

	/***********************************************************************************************************
	* 函数名称: int FpVfyPwd(uint32_t nAddr, uint8_t *pBuf, int nBufSize, uint32_t timeout)
	* 函数功能: 验证模块握手口令
	* 输入参数: 芯片地址
	* 输入参数: 密码缓冲区地址
	* 输入参数: 密码缓冲区大小,要求为4
	* 输入参数: 等候时间
	* 返回参数: 执行结果
	***********************************************************************************************************/
	public static native int FpVfyPwd(int nAddr, byte[] pBuf, int nBufSize, int timeout);

	/***********************************************************************************************************
	* 函数名称: int FpGetRandom(uint32_t nAddr, uint32_t *pRandom, uint32_t timeout)
	* 函数功能: 令芯片生成一个随机数并返回给上位机
	* 输入参数: 芯片地址
	* 输入参数: 随机数地址
	* 输入参数: 等候时间
	* 返回参数: 执行结果
	***********************************************************************************************************/
	public static native int FpGetRandom(int nAddr, int[] pRandom, int timeout);

	/***********************************************************************************************************
	* 函数名称: int FpSetChipAddr(uint32_t nAddr, uint32_t nChip, uint32_t timeout)
	* 函数功能: 设置芯片地址
	* 输入参数: 旧芯片地址
	* 输入参数: 新芯片地址
	* 输入参数: 等候时间
	* 返回参数: 执行结果
	***********************************************************************************************************/
	public static native int FpSetChipAddr(int nAddr, int nChip, int timeout);

	/***********************************************************************************************************
	* 函数名称: int FpReadInfPage(uint32_t nAddr, uint8_t *pBuf, int nBufSize, int *pBytesReturned, uint32_t timeout)
	* 函数功能: 写32字节数据到Flash用户区
	* 输入参数: 芯片地址
	* 输入参数: 缓冲区地址
	* 输入参数: 缓冲区大小
	* 输入参数: 实际大小
	* 输入参数: 等候时间
	* 返回参数: 执行结果
	***********************************************************************************************************/
	public static native int FpReadInfPage(int nAddr, byte[] pBuf, int nBufSize, int[] pBytesReturned, int timeout);
	
	/***********************************************************************************************************
	* 函数名称: int FpWriteNotepad(uint32_t nAddr, uint8_t nPageId, uint8_t *pBuf, int nBufSize, uint32_t timeout)
	* 函数功能: 写32字节数据到Flash用户区
	* 输入参数: 芯片地址
	* 输入参数: 用户区页码
	* 输入参数: 缓冲区地址
	* 输入参数: 缓冲区大小，要求不大于32
	* 输入参数: 等候时间
	* 返回参数: 执行结果
	***********************************************************************************************************/
	public static native int FpWriteNotepad(int nAddr, byte nPageId, byte[] pBuf, int nBufSize, int timeout);

	/***********************************************************************************************************
	* 函数名称: int FpReadNotepad(uint32_t nAddr, uint8_t nPageId, uint8_t *pBuf, int nBufSize, int *pBytesReturned, uint32_t timeout)
	* 函数功能: 从FLASH用户区读取32字节数据
	* 输入参数: 芯片地址
	* 输入参数: 用户区页码
	* 输入参数: 缓冲区地址
	* 输入参数: 缓冲区大小，要求等于32
	* 输入参数: 实际大小
	* 输入参数: 等候时间
	* 返回参数: 执行结果
	***********************************************************************************************************/
	public static native int FpReadNotepad(int nAddr, byte nPageId, byte[] pBuf, int nBufSize, int[] pBytesReturned, int timeout);

	/***********************************************************************************************************
	* 函数名称: int FpHighSpeedSearch(uint32_t nAddr, uint8_t nBufferId, uint16_t nStartPage, uint16_t nPageNum,
	*                        uint16_t *pPageId, uint16_t *pScore, uint32_t timeout)
	* 函数功能: 以CharBuffer1或CharBuffer2中的特征文件高速搜索指定指纹库
	* 输入参数: 芯片地址
	* 输入参数: 特征缓冲区号，要求为CharBuffer1=1或CharBuffer2=28
	* 输入参数: 搜索起始页
	* 输入参数: 搜索页数
	* 输入参数: 匹配页码
	* 输入参数: 比对得分
	* 输入参数: 等候时间
	* 返回参数: 执行结果
	***********************************************************************************************************/
	public static native int FpHighSpeedSearch(int nAddr, byte nBufferId, short nStartPage, short nPageNum, short[] pPageId, short[] pScore, int timeout);

	/***********************************************************************************************************
	* 函数名称: int FpGenBinImage(uint32_t nAddr, uint8_t nBinType, uint32_t timeout)
	* 函数功能: 对图像缓冲区中的指纹图像进行处理并生成细化指纹图像
	* 输入参数: 芯片地址
	* 输入参数: 细化类型
	* 输入参数: 等候时间
	* 返回参数: 执行结果
	***********************************************************************************************************/
	public static native int FpGenBinImage(int nAddr, byte nBinType, int timeout);

	/***********************************************************************************************************
	* 函数名称: int FpValidTempleteNum(uint32_t nAddr, uint16_t *pNum, uint32_t timeout)
	* 函数功能: 读有效模板个数
	* 输入参数: 芯片地址
	* 输入参数: 模板个数
	* 输入参数: 等候时间
	* 返回参数: 执行结果
	***********************************************************************************************************/
	public static native int FpValidTempleteNum(int nAddr, short[] pNum, int timeout);

	/***********************************************************************************************************
	* 函数名称: int FpMakeBMP(const uint8_t *pFile, uint8_t *pBuf, int nSize)
	* 函数功能: 根据图像数据创建BMP
	* 输入参数: 全路径的文件名称
	* 输入参数: 数据缓冲区地址
	* 输入参数: 数据缓冲区大小
	* 返回参数: 执行结果
	***********************************************************************************************************/
	public static native int FpMakeBMP(String pFile, byte[] pBuf, int nSize);
}
