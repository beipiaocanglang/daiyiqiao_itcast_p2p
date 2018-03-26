package cn.itcast.jms;

import javax.jms.Message;

public interface MessageSender {
	
	//发送消息方法
	public void send(Message message);
}
