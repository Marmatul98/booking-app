package mmatula.bookingapp.controller;

import mmatula.bookingapp.SmsRequest;
import mmatula.bookingapp.service.ExceptionLogService;
import mmatula.bookingapp.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class SmsController {

    private final SmsService smsService;
    private final ExceptionLogService exceptionLogService;

    @Autowired
    public SmsController(SmsService smsService, ExceptionLogService exceptionLogService) {
        this.smsService = smsService;
        this.exceptionLogService = exceptionLogService;
    }

    @PostMapping("/sms")
    public void sendSms() {
        try {
            SmsRequest smsRequest = new SmsRequest(
                    "+420704183294",
                    "Testuju aplikaci"
            );

            smsService.sendSms(smsRequest);
        } catch (Exception e) {
            this.exceptionLogService.addException(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
