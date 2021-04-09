package mmatula.bookingapp.repository;

import mmatula.bookingapp.model.SportsField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SportsFieldRepository extends JpaRepository<SportsField, Integer> {
    SportsField findByName(String name);

    List<SportsField> findByIdIn(List<Integer> ids);
}
