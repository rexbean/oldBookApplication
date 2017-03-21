package com.oldbook.entity;

public enum MessageType
{
	SUCCESS,				//表明是否成功
	FAIL,				//表明失败
	INITIAl,            //初始化
	REGISTER,        	//注册
	LOGIN, 			//请求验证登陆
	MESSAGE,				//普通信息包
	GET_PERSONAL_INFOR,	//获取个人信息
	RET_PERSONAL_INFOR,  //返回个人信息
	GET_HOMEPAGE,		//获取主页
	RET_HOMEPAGE,		//返回主页
	GET_ONLINE_FRIENDS,	//获取在线好友
	RET_ONLINE_FRIENDS,//返回在线好友
	GET_MAIL,		//获取邮箱
	RET_MAIL,		//返回邮箱
	LOGOUT ,         //注销
	REFRESH,          //刷新
	LOGIN_ONLINE,      //好友上线
	NEW_BOOK,		//添加新书
	BORROW,			//借阅
	EXCHANGE,		//交换
	BOOKSURFACE,     //图书封面
	BOOK_LIST,		//图书列表
	SURFACE_SUCCESS,//封面上传成功
	AVATAR_SUCCESS,  //头像上传成功
	LEND,              //借出
	EVALUATION,         //评价
	RETURN,              //归还
	WAIT_EVALUATION,    //等待评价
	EVALUATION_OVER,     //已评价
	BEAT,                 //心跳包
	UPLOAD,               //上传
	DOWNLOAD              //下载
}

