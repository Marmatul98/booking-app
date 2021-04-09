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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private SportsField sportsField;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    private boolean requested = false;

    private Boolean confirmed = false;

    private LocalTime bookedFrom;

    private LocalTime bookedTo;

    private LocalDate date;
}
