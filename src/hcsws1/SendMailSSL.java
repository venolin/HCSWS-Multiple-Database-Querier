/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hcsws1;

/**
 *
 * @author Venolin
 */
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.*;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMailSSL extends JDialog {
   
    JTextArea messagebodyArea;
    JScrollPane sp;
    JPanel messagebodyPanel;
    JPanel bottomPanel;
    JButton sendButton;
    
    
    public SendMailSSL() {
        initComponents();
    }
    
    private void initComponents() {
        messagebodyArea = new JTextArea();
        sp = new JScrollPane(messagebodyArea);
        messagebodyPanel = new JPanel();
        messagebodyPanel.setLayout(new BorderLayout()); //Panel's have the default Layout of 'FlowLayout', unlike the Layout of JFrame which had the default Layout of 'BorderLayout', so this has gotta be changed manually to fill in the panel automatically.
        
        messagebodyPanel.setPreferredSize(new Dimension(500, 450));
        
        bottomPanel = new JPanel();
        sendButton = new JButton("Send Email");
        
        this.setTitle("Request a feature"); //keyword 'this' may be used here as initComponents is eventualy used in the main class SendMailSSL which extends the JFrame object anyway.
        
        this.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
        
        //this.setResizable(false);
        
        this.setSize(500, 450);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        //this.setVisible(true); //This will make the dialog run as soon as the class is called. We don't want this. For better understanding.
        this.setLocationRelativeTo(null); //On load, the application is placed in the center of the screen
        
        messagebodyPanel.add(sp);
        
        bottomPanel.add(sendButton);
        this.add(messagebodyPanel);
        this.add(bottomPanel, BorderLayout.PAGE_END);
        
        sendButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
             
             if (sendEmail(messagebodyArea.getText()) == true) {
                 messagebodyArea.setText("Email sent successfully!");
             } else {
                 messagebodyArea.setText("Email unsuccessful :(");
             }
            
                                  
            //(new Main.BackgroundProcess()).execute();   
         }
        });
        
    }
    
	public static void main(String[] args) {
            
           
        //SendMailSSL ven = new SendMailSSL();
            
		
               
                
	}

        
        public Boolean sendEmail(String messageBody) { //This needs to be placed in a background worker as it stalls the interface process while it attempts to send
          Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getDefaultInstance(props,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("hcswsven@gmail.com","531WaSdo");
				}
			});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("venolin1@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse("venolin@outlook.com"));
			message.setSubject("Request a Feature " + new Date());
			message.setText(messageBody); //\n escapes carriage return even in the email received.

			Transport.send(message);

			System.out.println("Done");
                        return true;
                        
		} catch (MessagingException e) {
			System.out.println("Message Fail");
                        return false;
                        //throw new RuntimeException(e);
                        
		}          
            
        }
        
}