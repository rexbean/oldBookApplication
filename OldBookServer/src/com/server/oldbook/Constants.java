package com.server.oldbook;

public class Constants
{
	public static final String SERVER_IP = "192.168.1.102";// 服务器ip
	public static final int PORT = 2222;// 服务器端口
	public static final int PORT_PIC = 2223;// 服务器端口
	public static final int REGISTER_FAIL = 0;//注册失败
	public static final String ACTION = "com.oldbook.android.message";//消息广播action
	public static final String MSGKEY = "message";//消息的key
	public static final String IP_PORT = "ipPort";//保存ip、port的xml文件名
	public static final String SAVE_USER = "saveUser";//保存用户信息的xml文件名
	public static final String BACKKEY_ACTION="com.way.backKey";//返回键发送广播的action
	public static final int NOTIFY_ID = 0x911;//通知ID
	public static final String DBNAME = "OldBook.db";//数据库名称
	
    public static int SCREEN_WIDTH;      //屏幕宽度
    public static int SCREEN_HEIGHT;     //屏幕高度
    public static float DENSITY;         //密度
    
    public static String USER_NAME;      //用户名
    public static String PET_NAME;       //昵称
    public static int AVATAR;            //头像       
    public static int ID;                //ID
}
