package mmatula.bookingapp.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import mmatula.bookingapp.deserializer.BookingCreationRequestDeserializer;

import java.time.LocalDate;
import java.time.LocalTime;


@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = BookingCreationRequestDeserializer.class)
public class BookingCreationRequest {

    private int sportsFieldId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private int durationInMinutes;
}

