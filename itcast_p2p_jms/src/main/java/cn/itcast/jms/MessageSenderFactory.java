package cn.itcast.jms;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MessageSenderFactory {

	@Autowired // 默认是bytype
	@Qualifier("emailSender")
	private MessageSender emailSender;
	// @Autowired
	// @Qualifier("msmSender")
	@Resource(name = "msmSender")
	private MessageSender msmSender;

	// 根据消息的类型来获取具体的消息处理类
	public MessageSender getMessageSender(String type) {
		if ("email".equals(type)) {
			return emailSender;
		} else if ("sms".equals(type)) {
			return msmSender;
		} else {
			return null;
		}
	}

}
