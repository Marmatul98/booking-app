package mmatula.bookingapp.repository;

import mmatula.bookingapp.enums.ERole;
import mmatula.bookingapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    List<User> findUsersByRoleOrderByLastName(ERole role);
}
