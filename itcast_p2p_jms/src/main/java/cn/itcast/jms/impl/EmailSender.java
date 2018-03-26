package cn.itcast.jms.impl;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import cn.itcast.jms.MessageSender;

@Component("emailSender")
public class EmailSender implements MessageSender {

	@Autowired
	private JavaMailSender mailSender;

	@Value("${mail.default.from}")
	private String from; // from在properties文件中声明了 mail.default.from 怎样获取到?

	@Override
	public void send(Message message) {

		System.out.println("发送邮件");
	}

}
