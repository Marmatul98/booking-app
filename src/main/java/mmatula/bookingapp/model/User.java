package mmatula.bookingapp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mmatula.bookingapp.enums.ERole;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Enumerated(EnumType.ORDINAL)
    private ERole role;

    private String firstName;
    private String lastName;
    private String email;
    private String password;

    private boolean guest;

    @OneToMany(mappedBy = "user")
    private Set<Booking> bookings = new HashSet<>();

    public User(String firstName, String lastName, String email, String password, boolean isGuest) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.guest = isGuest;
    }
}
