package cn.itcast.jms.impl;

import javax.jms.Message;

import org.springframework.stereotype.Component;

import cn.itcast.jms.MessageSender;

@Component("msmSender")
public class MsmSender implements MessageSender {

	@Override
	public void send(Message message) {
		System.out.println("发送短信");
	}

}
