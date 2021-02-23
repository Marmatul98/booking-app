package mmatula.bookingapp.repository;

import mmatula.bookingapp.model.SportsField;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SportsFieldRepository extends JpaRepository<SportsField, Integer> {
    SportsField findByName(String name);
}
