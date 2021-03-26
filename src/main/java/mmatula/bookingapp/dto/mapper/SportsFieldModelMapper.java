package mmatula.bookingapp.dto.mapper;

import mmatula.bookingapp.dto.SportsFieldDTO;
import mmatula.bookingapp.model.SportsField;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SportsFieldModelMapper {

    public SportsFieldDTO entityToDto(SportsField sportsField) {
        return new SportsFieldDTO(String.valueOf(sportsField.getId()), sportsField.getName());
    }

    public List<SportsFieldDTO> entityListToDtoList(List<SportsField> sportsFields) {
        return sportsFields.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }
}
