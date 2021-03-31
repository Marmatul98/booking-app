package mmatula.bookingapp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class ExceptionLog {

    @Id
    @GeneratedValue
    private Long id;

    private String message;
    @Lob
    private String stackTrace;
    private LocalDate date;

    public ExceptionLog(String message, String stackTrace, LocalDate localDate) {
        this.message = message;
        this.stackTrace = stackTrace;
        this.date = localDate;
    }
}
