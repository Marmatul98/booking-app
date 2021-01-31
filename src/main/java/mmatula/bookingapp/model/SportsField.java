package mmatula.bookingapp.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
public class SportsField {

    @Id
    @GeneratedValue
    private Integer sportFieldId;

    @OneToMany
    private Set<Booking> bookings;
}
