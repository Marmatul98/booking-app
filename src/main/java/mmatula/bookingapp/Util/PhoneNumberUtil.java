package mmatula.bookingapp.Util;

import org.springframework.stereotype.Component;

@Component
public class PhoneNumberUtil {

    public String validatePhoneNumber(String phoneNumber) {
        if (phoneNumber.length() < 13 || !phoneNumber.matches("\\+[0-9]{12}")) {
            throw new IllegalArgumentException(phoneNumber);
        } else {
            return phoneNumber;
        }
    }
}
