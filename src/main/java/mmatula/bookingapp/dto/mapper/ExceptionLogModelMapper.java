package mmatula.bookingapp.dto.mapper;

import mmatula.bookingapp.dto.ExceptionLogDTO;
import mmatula.bookingapp.model.ExceptionLog;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExceptionLogModelMapper {

    public ExceptionLogDTO entityToDTO(ExceptionLog exceptionLog) {
        return new ExceptionLogDTO(
                exceptionLog.getId(),
                exceptionLog.getMessage(),
                exceptionLog.getStackTrace(),
                exceptionLog.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE)
        );
    }

    public List<ExceptionLogDTO> entityListToDtoList(List<ExceptionLog> exceptionLogs) {
        return exceptionLogs
                .stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }
}
