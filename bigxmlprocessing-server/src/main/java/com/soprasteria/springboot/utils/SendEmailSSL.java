package com.soprasteria.springboot.utils;
	import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.soprasteria.springboot.constants.PropertyConstants;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SendEmailSSL {

	public void sendemail(String subject, String content) {
		Properties prop = new Properties();
		try {
			InputStream input = getClass().getClassLoader().getResourceAsStream(PropertyConstants.MAIL_PROPERTIES);
			if (input == null) {
				return;
			}
			// load a properties file from class path, inside static method
			prop.load(input);

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		final String username = prop.getProperty(PropertyConstants.USER_NAME);
		final String password = prop.getProperty(PropertyConstants.PASSWORD);

		Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(prop.getProperty(PropertyConstants.FEEDBACK_FROM)));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(prop.getProperty(PropertyConstants.FEEDBACK_TO)));
			message.setSubject(subject);
			message.setText(content);

			Transport.send(message);

		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

}