package cn.itcast.jms;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.springframework.beans.factory.annotation.Autowired;

public class MessageReceiver implements MessageListener {

	@Autowired
	private MessageSenderFactory factory;

	@Override
	public void onMessage(Message m) {
		// 1.将message转型
		MapMessage mm = (MapMessage) m;

		try {
			// 2.根据mm的类型，获取具体的消息处理，完成消息发送
			processMessage(mm);
			// 3.设置应答机制
			mm.acknowledge();
		} catch (JMSException e) {
			e.printStackTrace();
		}

	}

	private void processMessage(MapMessage mm) throws JMSException {
		String type = mm.getString("type");
		MessageSender messageSender = factory.getMessageSender(type);
		messageSender.send(mm);
	}

}
