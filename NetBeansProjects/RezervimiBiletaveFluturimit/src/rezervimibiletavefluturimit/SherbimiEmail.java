package rezervimibiletavefluturimit;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SherbimiEmail {

    private static final String EMAILI_DERGUES = "projektjava16@gmail.com";
    private static final String FJALEKALIMI_EMAILIT = "mloi ljpn zcpl zchv";

    //Funksion qe dergon email tek nje email i caktuar
    public static void dergoEmail(String emailiMarres, String tema, String trupi) {
        //vendosen konfigurimet
        System.setProperty("https.protocols", "TLSv1.2,TLSv1.3");
        Properties atributet = new Properties();
        atributet.put("mail.smtp.host", "smtp.gmail.com");
        atributet.put("mail.smtp.port", "587");
        atributet.put("mail.smtp.auth", "true");
        atributet.put("mail.smtp.ssl.protocols", "TLSv1.2");
        atributet.put("mail.smtp.starttls.enable", "true");
        atributet.put("mail.smtp.starttls.required", "true");
        atributet.put("mail.smtp.ssl.trust", "*");

        Session session = Session.getInstance(atributet, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAILI_DERGUES, FJALEKALIMI_EMAILIT);
            }
        });

        try {
            //krijohet emaili
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAILI_DERGUES));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailiMarres));
            message.setSubject(tema);
            message.setText(trupi);
            message.setContent(trupi, "text/html");
            Transport.send(message);

        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}
