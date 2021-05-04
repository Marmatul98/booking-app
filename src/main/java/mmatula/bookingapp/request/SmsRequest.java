package mmatula.bookingapp.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@AllArgsConstructor
@Getter
public class SmsRequest {

    private final String phoneNumber;
    private final String message;
}
