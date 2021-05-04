package mmatula.bookingapp.config;

import com.twilio.Twilio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TwilioConfiguration.class)
public class TwilioInitializer {

    private TwilioConfiguration twilioConfiguration;
    private static final Logger logger = LoggerFactory.getLogger(TwilioInitializer.class);

    @Autowired
    public TwilioInitializer(TwilioConfiguration twilioConfiguration) {
        this.twilioConfiguration = twilioConfiguration;
        Twilio.init(twilioConfiguration.getAccountSid(), twilioConfiguration.getAuthToken());
        logger.info("Twilio initialized .. with account sid {} ", twilioConfiguration.getAccountSid());
    }
}
