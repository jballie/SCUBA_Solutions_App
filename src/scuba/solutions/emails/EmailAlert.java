/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scuba.solutions.emails;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import scuba.solutions.ui.customers.model.Customer;
import scuba.solutions.ui.dive_schedule.model.DiveTrip;
import scuba.solutions.ui.reservations.model.Reservation;
import scuba.solutions.util.AlertUtil;

/**
 *
 * @author Jon
 */
public class EmailAlert 
{
    
    public static void sendRequestEmail(int reservationId, Customer customer, DiveTrip selectedTrip) throws IOException, SQLException, InterruptedException {
          Properties props = new Properties();    
          props.put("mail.smtp.host", "smtp.gmail.com");    
          props.put("mail.smtp.socketFactory.port", "465");    
          props.put("mail.smtp.socketFactory.class",    
                    "javax.net.ssl.SSLSocketFactory");    
          props.put("mail.smtp.auth", "true");    
          props.put("mail.smtp.port", "465");    
          
          Session session = Session.getDefaultInstance(props,    
           new javax.mail.Authenticator() {
           @Override
           protected PasswordAuthentication getPasswordAuthentication() {    
           return new PasswordAuthentication("scubascubanow@gmail.com","capstone");  
           }    
          });   
          
          try {    
           MimeMessage message = new MimeMessage(session);    
           message.addRecipient(Message.RecipientType.TO,new InternetAddress("scubascubanow@gmail.com"));   //change 
           message.setSubject("Dive Reservation Request");
           
           StringBuilder msg = new StringBuilder();
           msg.append("Diver: ");
           msg.append(customer.getFullName());
           msg.append("\n\nReservation Number: ");
           msg.append(reservationId);
           msg.append("\n\nDive Date: ");
           msg.append(selectedTrip.getTripDate());
           msg.append("\n\nDepart Time: ");
           msg.append(selectedTrip.getDepartTime());
           msg.append("\n\n\n\nGreetings ");
           msg.append(customer.getFirstName());
           msg.append(",\n");
           
           Files.lines(Paths.get("RequestLetter.txt"), StandardCharsets.UTF_8).forEach(s -> msg.append(s).append("\n")); // works but doesn't like it
           
           BodyPart messageBodyPart1 = new MimeBodyPart();
           messageBodyPart1.setText(msg.toString());       
                 
           MimeBodyPart messageBodyPart2 = new MimeBodyPart();  
  
           String filename = "Waiver.docx";
           DataSource source = new FileDataSource(filename);  
           messageBodyPart2.setDataHandler(new DataHandler(source));  
           messageBodyPart2.setFileName(filename);  
         
           Multipart multipart = new MimeMultipart();  
           multipart.addBodyPart(messageBodyPart1);  
           multipart.addBodyPart(messageBodyPart2); 
           
           message.setContent(multipart);
           
           
           Transport.send(message);
           
          } 
          catch (MessagingException e) 
          {
              AlertUtil.showErrorAlert("Error with sending Reservation Request Email Message\n", e);
              throw new RuntimeException(e);
          
          }
    }
    
    public static void sendConfirmationEmail(Reservation selectedReservation) throws IOException{   
          Properties props = new Properties();    
          props.put("mail.smtp.host", "smtp.gmail.com");    
          props.put("mail.smtp.socketFactory.port", "465");    
          props.put("mail.smtp.socketFactory.class",    
                    "javax.net.ssl.SSLSocketFactory");    
          props.put("mail.smtp.auth", "true");    
          props.put("mail.smtp.port", "465");    
       
          Session session = Session.getDefaultInstance(props,    
           new javax.mail.Authenticator() {    
           @Override
           protected PasswordAuthentication getPasswordAuthentication() {    
           return new PasswordAuthentication("scubascubanow@gmail.com","capstone");  
           }    
          });    
          
          try {    
           MimeMessage message = new MimeMessage(session);    
           message.addRecipient(Message.RecipientType.TO,new InternetAddress("scubascubanow@gmail.com"));    
           message.setSubject("Diva Confirmation Email");
                   
           StringBuilder msg = new StringBuilder();
           msg.append("Diver: ");
           msg.append(selectedReservation.getCustomer().getFullName());
           msg.append("\n\nReservation Number: ");
           msg.append(selectedReservation.getReservationId());
           msg.append("\n\nDive Date: ");
           msg.append(selectedReservation.getDiveTrip().getTripDate());
           msg.append("\n\nDepart Time: ");
           msg.append(selectedReservation.getDiveTrip().getDepartTime());
           msg.append("\n\n\n\nGreetings ");
           msg.append(selectedReservation.getCustomer().getFirstName());
           msg.append(",\n\n");
           
           Files.lines(Paths.get("ConfirmationLetter.txt"), StandardCharsets.UTF_8).forEach(s -> msg.append(s).append("\n"));
           
           message.setText(msg.toString());
           
           Transport.send(message); 
          } 
          catch (MessagingException e) 
          {
              AlertUtil.showErrorAlert("Error with sending Reservation Confirmation Email Message\n", e);
              throw new RuntimeException(e);
          }  
    }
    
    
    
    
}
