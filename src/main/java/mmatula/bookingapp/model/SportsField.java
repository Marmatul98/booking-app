package mmatula.bookingapp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class SportsField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @OneToMany
    private Set<Booking> bookings = new HashSet<>();

    public SportsField(String name) {
        this.name = name;
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public void addBookings(Set<Booking> bookingsToAdd){
        this.bookings.addAll(bookingsToAdd);
    }
}
