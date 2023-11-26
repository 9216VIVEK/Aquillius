package com.aquillius.portal.service.serviceImpl;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aquillius.portal.entity.User;
import com.aquillius.portal.entity.VerificationToken;
import com.aquillius.portal.service.EmailService;
import com.aquillius.portal.service.VerificationTokenService;

import jakarta.activation.DataHandler;
import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

	@Value("${smtp.host.name}")
	private String SMTP_HOST_NAME; // smtp URL

	@Value("${smtp.host.port}")
	private int SMTP_HOST_PORT; // port number

	@Value("${smtp.host.user}")
	private String SMTP_AUTH_USER; // email_id of sender

	@Value("${smtp.host.pwd}")
	private String SMTP_AUTH_PWD; // password of sender email_id

	private final VerificationTokenService tokenService;

	public void sendEmail(String toEmail, String subject, String content) {

		try {

			Session mailSession = getSMTPMailSession();

			Transport transport = mailSession.getTransport();

			MimeMessage message = new MimeMessage(mailSession);

			message.setSubject(subject);
			message.setContent(content, "text/html");
			Address[] from = InternetAddress.parse("maqs@aquillius.com");// Your domain email
			message.addFrom(from);
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail)); // Send email To
																							// (Type email ID
			transport.connect(SMTP_HOST_NAME, SMTP_HOST_PORT, SMTP_AUTH_USER, SMTP_AUTH_PWD);
			transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
			transport.close();
			log.info("mail sent to : " + toEmail);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendEmailWithAttachment(String toEmail, String subject, String content, String attachmentName,
			byte[] attachmentData) {

		log.info("===================inside sendEmailWithAttachment Service API ===================");

		try {
			Session mailSession = getSMTPMailSession();

			Transport transport = mailSession.getTransport();

			MimeMessage message = new MimeMessage(mailSession);

			message.setSubject(subject);
			message.setContent(content, "text/html");
			Address[] from = InternetAddress.parse("maqs@aquillius.com");
			message.addFrom(from);
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail)); // Send email To (Type email
																							// ID that you want to send)

			// Create a multipart message
			Multipart multipart = new MimeMultipart();

			// Add the message body
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(content, "text/html");
			multipart.addBodyPart(messageBodyPart);

			// Add the attachment
			MimeBodyPart attachmentPart = new MimeBodyPart();
			ByteArrayDataSource bds = new ByteArrayDataSource(attachmentData, "application/x-any");
			attachmentPart.setDataHandler(new DataHandler(bds));
			attachmentPart.setFileName(attachmentName);
			multipart.addBodyPart(attachmentPart);

			// Set the message content
			message.setContent(multipart);

			transport.connect(SMTP_HOST_NAME, SMTP_HOST_PORT, SMTP_AUTH_USER, SMTP_AUTH_PWD);
			transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
			transport.close();

		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	public void sendVerificationCode(User user) {

		log.info("===================inside sendVerificationCode Service API ===================");

		if (user == null) {
			return;
		}
		VerificationToken existingToken = tokenService.findTokenByUser(user);
		VerificationToken token;

		if (existingToken != null) {
			token = existingToken;
		} else {
			token = tokenService.createVerificationToken(user);
		}

		String emailContent = "Verification link : https://portal.aquillius.com/Email-Verification.php \n"
				+ "Verification Code :  " + token.getToken();
		sendEmail(user.getEmail(), "Account Verification", emailContent);
	}

	private Session getSMTPMailSession() {

		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.host", SMTP_HOST_NAME);

		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.debug", "true");
		props.put("mail.smtp.port", Integer.toString(SMTP_HOST_PORT));
		props.put("mail.smtp.socketFactory.port", Integer.toString(SMTP_HOST_PORT));
		props.put("mail.smtp.socketFactory.class", "javax.net.SocketFactory");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.ssl.enable", "false");
		props.put("mail.smtp.socketFactory.fallback", "true");
		props.setProperty("mail.smtp.quitwait", "false");

		Session mailSession = Session.getInstance(props, new jakarta.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(SMTP_AUTH_USER, SMTP_AUTH_PWD);
			}
		});

		mailSession.setDebug(true);

		return mailSession;
	}
}
