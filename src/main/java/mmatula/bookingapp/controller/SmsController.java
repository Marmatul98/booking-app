package mmatula.bookingapp.controller;

import mmatula.bookingapp.SmsRequest;
import mmatula.bookingapp.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SmsController {

    private final SmsService smsService;

    @Autowired
    public SmsController(SmsService smsService) {
        this.smsService = smsService;
    }

    @PostMapping("/sms")
    public void sendSms(){
        SmsRequest smsRequest = new SmsRequest(
                "+420704183294",
                "Testuju aplikaci"
        );

        smsService.sendSms(smsRequest);
    }
}
