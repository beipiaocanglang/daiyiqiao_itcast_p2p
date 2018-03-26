package cn.itcast.service.msm.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import cn.itcast.service.msm.IMsmService;
import cn.itcast.utils.MessageConstants;

@Service
public class MsmServiceImpl implements IMsmService {
	// 这个是用于发送短信操作
	@Autowired
	private JmsTemplate jmsTemplate;

	public void sendMsg(String phone, String content) {
		// 1.创建一个map来封装所有信息
		Map<String, Object> msg = new HashMap<String, Object>();
		msg.put(MessageConstants.MessageType, MessageConstants.SMSMessage);// 这是一个短信
		msg.put(MessageConstants.SMSNumbers, phone);
		msg.put(MessageConstants.MessageContent, content);
		// 发送
		jmsTemplate.convertAndSend(msg);

	}
}
