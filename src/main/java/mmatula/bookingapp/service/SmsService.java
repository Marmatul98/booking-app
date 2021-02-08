package mmatula.bookingapp.service;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import mmatula.bookingapp.SmsRequest;
import mmatula.bookingapp.TwilioConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    private final TwilioConfiguration twilioConfiguration;

    @Autowired
    public SmsService(TwilioConfiguration twilioConfiguration) {
        this.twilioConfiguration = twilioConfiguration;
    }

    public void sendSms(SmsRequest smsRequest) {
        MessageCreator creator = Message.creator(
                new PhoneNumber(smsRequest.getPhoneNumber()),
                new PhoneNumber(twilioConfiguration.getTrialNumber()),
                smsRequest.getMessage()
        );

        creator.create();
    }
}
