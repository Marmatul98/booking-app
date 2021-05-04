package mmatula.bookingapp.request;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import mmatula.bookingapp.deserializer.BookingCreationRequestDeserializer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = BookingCreationRequestDeserializer.class)
public class BookingCreationRequest {

    private List<Integer> sportsFieldIds;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
}

