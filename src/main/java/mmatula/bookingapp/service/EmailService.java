package mmatula.bookingapp.service;

import mmatula.bookingapp.model.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(String to, String subject, String messageText) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(messageText);

        javaMailSender.send(message);
    }

    public void sendConfirmationEmail(Booking booking) {
        this.sendEmail(
                booking.getUser().getEmail(),
                "Potvrzení rezervace",
                "Rezervace " + booking.getDate() + "potvrzena volejte na cislo"
        );
    }
}
