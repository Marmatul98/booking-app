package mmatula.bookingapp.service;

import mmatula.bookingapp.model.ExceptionLog;
import mmatula.bookingapp.repository.ExceptionLogRepository;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExceptionLogService {

    private final ExceptionLogRepository exceptionLogRepository;

    @Autowired
    public ExceptionLogService(ExceptionLogRepository exceptionLogRepository) {
        this.exceptionLogRepository = exceptionLogRepository;
    }

    public void addException(Exception exception) {
        this.exceptionLogRepository.save(
                new ExceptionLog(
                        ExceptionUtils.getMessage(exception),
                        ExceptionUtils.getStackTrace(exception),
                        LocalDate.now()));
    }

    public List<ExceptionLog> getAllExceptionLogs() {
        return exceptionLogRepository.findAll();
    }

    public void deleteExceptionById(long id) {
        this.exceptionLogRepository.deleteById(id);
    }
}
