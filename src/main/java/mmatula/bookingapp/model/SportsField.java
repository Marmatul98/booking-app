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

    @OneToMany(mappedBy = "sportsField", fetch = FetchType.EAGER)
    private Set<Booking> bookings = new HashSet<>();

    public SportsField(String name) {
        this.name = name;
    }
}
