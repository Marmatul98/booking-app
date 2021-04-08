package mmatula.bookingapp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime valid;

    private UUID token;

    @OneToOne
    private User user;

    public PasswordResetToken(LocalDateTime valid, User user, UUID token) {
        this.valid = valid;
        this.user = user;
        this.token = token;
    }
}
