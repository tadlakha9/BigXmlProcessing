package com.soprasteria.springboot.utils;
	import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

	public class SendEmailSSL {

	    public void sendemail(String subject,String content) {
	    	Properties prop = new Properties();
	    	try{
	    	InputStream input =getClass().getClassLoader().getResourceAsStream("mail.properties");
	    	   if (input == null) {
	               System.out.println("Sorry, unable to find config.properties");
	               return;
	           }
	    	  //load a properties file from class path, inside static method
	         prop.load(input);

	     } catch (IOException ex) {
	         ex.printStackTrace();
	     }
	    	
	        final String username = prop.getProperty("username");
	        final String password =prop.getProperty("password");

	        Session session = Session.getInstance(prop,
	                new javax.mail.Authenticator() {
	                    protected PasswordAuthentication getPasswordAuthentication() {
	                        return new PasswordAuthentication(username, password);
	                    }
	                });

	        try {

	            Message message = new MimeMessage(session);
	            message.setFrom(new InternetAddress(prop.getProperty("from")));
	            message.setRecipients(
	                    Message.RecipientType.TO,
	                    InternetAddress.parse(prop.getProperty("to"))
	            );
	            message.setSubject(subject);
	            message.setText(content);

	            Transport.send(message);

	            System.out.println("Done");

	        } catch (MessagingException e) {
	            e.printStackTrace();
	        }
	    }

	}