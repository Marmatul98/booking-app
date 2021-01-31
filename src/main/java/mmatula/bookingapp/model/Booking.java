package mmatula.bookingapp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue()
    private Long bookingId;

    @ManyToOne
    private SportsField sportsField;

    private LocalDateTime bookedFrom;

    private LocalDateTime bookedTo;
}
