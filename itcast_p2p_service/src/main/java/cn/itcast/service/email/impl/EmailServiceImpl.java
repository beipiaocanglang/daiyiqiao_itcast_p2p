package cn.itcast.service.email.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import cn.itcast.service.email.IEmailService;
import cn.itcast.utils.MessageConstants;

@Service
public class EmailServiceImpl implements IEmailService {

	@Autowired
	private JmsTemplate jmsTemplate;
	// private JavaMailSender mailSender;

	// 发送邮件操作
	// 参数 email 要发送的邮箱地址
	// 参数 content 邮件内容
	@Override
	public void sendEmail(String email, String content) {
		// 1.创建一个map来封装所有信息
		Map<String, Object> msg = new HashMap<String, Object>();
		// 2.调用convertAndSender来发送消息到activemq,注意：这个方法在执行时，会将map通过内部消息转换器进行处理。

		msg.put(MessageConstants.MessageType, MessageConstants.EmailMessage); // 当前邮件
		msg.put(MessageConstants.EmailMessageTo, email); // 封装接收邮件的地址
		msg.put(MessageConstants.MessageContent, content); // 封装邮件内容

		jmsTemplate.convertAndSend(msg);

		// 将邮件信息封装成一个Message,发送到activemq上。

		// // 1.创建一个MimeMessage
		// MimeMessage mm = mailSender.createMimeMessage();
		//
		// // 3.设置相关属性
		// try {
		// // 2.得到一个设置邮件相关信息的对象
		// MimeMessageHelper helper = new MimeMessageHelper(mm, true);
		// helper.setFrom("itcast1234567@sina.com"); // 从哪发送邮件
		// helper.setTo(email); // 发送到哪
		// helper.setSubject("P2P邮件激活"); // 标题
		// helper.setText(content, true);// 内容
		// } catch (MessagingException e) {
		// e.printStackTrace();
		// }
		//
		// // 发送邮件
		// mailSender.send(mm);
	}

}
