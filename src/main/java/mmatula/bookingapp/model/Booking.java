package mmatula.bookingapp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue()
    private Long id;

    @ManyToOne
    private SportsField sportsField;

    @ManyToOne
    private User user;

    private Boolean requested;

    private Boolean confirmed;

    private LocalTime bookedFrom;

    private LocalTime bookedTo;

    private LocalDate date;
}
