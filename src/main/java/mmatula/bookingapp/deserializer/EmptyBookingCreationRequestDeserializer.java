package mmatula.bookingapp.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import mmatula.bookingapp.request.EmptyBookingCreationRequest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class EmptyBookingCreationRequestDeserializer extends StdDeserializer<EmptyBookingCreationRequest> {

    public EmptyBookingCreationRequestDeserializer(Class<?> vc) {
        super(vc);
    }

    public EmptyBookingCreationRequestDeserializer() {
        super(EmptyBookingCreationRequest.class);
    }

    @Override
    public EmptyBookingCreationRequest deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        LocalDate startDate = parseToLocalDate(node.get("startDate").asText());
        LocalTime startTime = parseToLocalTime(node.get("startTime").asText());
        LocalDate endDate = parseToLocalDate(node.get("endDate").asText());
        LocalTime endTime = parseToLocalTime(node.get("endTime").asText());

        int sportsFieldId = node.get("sportsFieldId").asInt();
        int durationInMinutes = node.get("durationInMinutes").asInt();

        return new EmptyBookingCreationRequest(
                sportsFieldId,
                startDate,
                endDate,
                startTime,
                endTime,
                durationInMinutes
        );
    }

    private LocalDate parseToLocalDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("M/d/yyyy"));
    }

    private LocalTime parseToLocalTime(String time) {
        return LocalTime.parse(time, DateTimeFormatter.ofPattern("H:m"));
    }
}
