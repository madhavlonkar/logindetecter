import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailLogs {

	public void sendEmailWithRetry(String filePath, int maxRetries, long retryDelayMillis) {
		int retryCount = 0;
		boolean emailSent = false;

		while (retryCount < maxRetries && !emailSent) {
			try {
				sendEmail(filePath); // Attempt to send email

				// If email sent successfully, set emailSent to true to exit the loop
				emailSent = true;
			} catch (Exception e) {
				// Handle the exception
				e.printStackTrace();

				// Increment retry count
				retryCount++;

				// Sleep before the next retry
				try {
					Thread.sleep(retryDelayMillis);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		}

		// Check if the email was not sent after maxRetries
		if (!emailSent) {
			System.out.println("Email could not be sent after " + maxRetries + " retries.");
		}
	}

	public void sendEmail(String filePath) throws Exception {
		Date currentDate = new Date();

		// Format the date and time for the filename
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String formattedDate = dateFormat.format(currentDate);

		// Create all the needed properties
		Properties connectionProperties = new Properties();
		// SMTP host
		connectionProperties.put("mail.smtp.host", "smtp-relay.sendinblue.com");
		// Is authentication enabled
		connectionProperties.put("mail.smtp.auth", "true");
		// SSL Port
		connectionProperties.put("mail.smtp.port", "587");

		// Create the session
		Session session = Session.getDefaultInstance(connectionProperties, new javax.mail.Authenticator() {
			// Define the authenticator
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("vayavi6626@yasiok.com", "ZDgPTAzQnXOcsESY");
			}
		});

		// Create and send the message
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress("java88pro@gmail.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("madhavlonkar2@gmail.com"));
			message.setSubject("Maddy Login Detected On Your Laptop");
			message.setText("This email contains log file and system details. Please find the attached log file.");

			// Attach computer details to the message
			String systemDetails = "Login Time: " + formattedDate;

			MimeBodyPart textBodyPart = new MimeBodyPart();
			textBodyPart.setText(systemDetails);

			MimeBodyPart fileBodyPart = new MimeBodyPart();
			fileBodyPart.attachFile(new File(filePath));

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(textBodyPart);
			multipart.addBodyPart(fileBodyPart);

			message.setContent(multipart);

			// Send the message
			Transport.send(message);
		} catch (Exception e) {
			throw new Exception("Failed to send email", e);
		}
	}

}
