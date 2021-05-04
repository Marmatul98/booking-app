package mmatula.bookingapp.service;

import mmatula.bookingapp.dto.BookingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

    public void sendConfirmationEmail(BookingDTO bookingDTO) {

        String message = getBaseMessage(bookingDTO) +
                " byla potvrzena administrátorem";

        this.sendEmail(
                bookingDTO.getUser().getEmail(),
                "Potvrzení rezervace",
                message
        );
    }

    public void sendRemovalEmail(BookingDTO bookingDTO) {

        String message = getBaseMessage(bookingDTO) +
                " byla zamítnutá administrátorem";

        this.sendEmail(
                bookingDTO.getUser().getEmail(),
                "Zamítnutí rezervace",
                message
        );
    }

    private String getBaseMessage(BookingDTO bookingDTO) {
        return "Rezervace hřiště " +
                bookingDTO.getSportsField().getName() +
                " dne " +
                LocalDate.parse(bookingDTO.getBookedDate(), DateTimeFormatter.ISO_LOCAL_DATE)
                        .format(DateTimeFormatter.ofPattern("dd. MM. yyyy")) +
                " v čase od " +
                bookingDTO.getStartTime() +
                " do " +
                bookingDTO.getEndTime();
    }
}
