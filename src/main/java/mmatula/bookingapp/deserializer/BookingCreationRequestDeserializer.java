package mmatula.bookingapp.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import mmatula.bookingapp.request.BookingCreationRequest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BookingCreationRequestDeserializer extends StdDeserializer<BookingCreationRequest> {

    public BookingCreationRequestDeserializer(Class<?> vc) {
        super(vc);
    }

    public BookingCreationRequestDeserializer() {
        super(BookingCreationRequest.class);
    }

    @Override
    public BookingCreationRequest deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        LocalDate startDate = parseToLocalDate(node.get("startDate").asText());
        LocalTime startTime = parseToLocalTime(node.get("startTime").asText());
        LocalDate endDate = parseToLocalDate(node.get("endDate").asText());
        LocalTime endTime = parseToLocalTime(node.get("endTime").asText());

        List<Integer> sportsFieldIds = new ArrayList<>();
        node.get("sportsFieldIds")
                .elements()
                .forEachRemaining(jsonNode -> sportsFieldIds.add(jsonNode.asInt()));
        return new BookingCreationRequest(
                sportsFieldIds,
                startDate,
                endDate,
                startTime,
                endTime
        );
    }

    private LocalDate parseToLocalDate(String date) {
        return LocalDate.parse(date,DateTimeFormatter.ofPattern("d. M. yyyy"));
    }

    private LocalTime parseToLocalTime(String time) {
        return LocalTime.parse(time, DateTimeFormatter.ofPattern("H:m"));
    }
}
