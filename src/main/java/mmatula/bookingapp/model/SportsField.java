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

    private boolean isGroup;

    private boolean isInGroup;

    @OneToMany(mappedBy = "sportsField", fetch = FetchType.EAGER)
    private Set<Booking> bookings = new HashSet<>();

    @OneToMany(mappedBy = "sportsFieldGroup", fetch = FetchType.EAGER)
    private Set<SportsField> groupedSportsFields = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    private SportsField sportsFieldGroup;

    public SportsField(String name) {
        this.name = name;
    }
}
