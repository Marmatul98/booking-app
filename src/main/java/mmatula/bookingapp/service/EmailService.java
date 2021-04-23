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

    public void sendEmail(String recipient, String subject, String messageText) {
        if (recipient != null && !recipient.isBlank()
                && subject != null && !subject.isBlank()
                && messageText != null && !messageText.isBlank()) {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(recipient);
            message.setSubject(subject);
            message.setText(messageText);

            javaMailSender.send(message);
        } else throw new IllegalArgumentException("Recipient, subject and messageText are required");
    }

    public void sendConfirmationEmail(Booking booking) {
        this.sendEmail(
                booking.getUser().getEmail(),
                "Potvrzení rezervace",
                "Rezervace " + booking.getDate() + "potvrzena volejte na cislo"
        );
    }
}
