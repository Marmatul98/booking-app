package mmatula.bookingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionLogDTO {

    private Long id;
    private String message;
    private String stackTrace;
    private String date;
}
