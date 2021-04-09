package mmatula.bookingapp.params;

import mmatula.bookingapp.model.SportsField;
import mmatula.bookingapp.model.User;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class BookingCreationParams {

    private final List<SportsField> sportsFields;
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final Duration duration;

    private final boolean requested;
    private final boolean confirmed;

    private final User user;

    private BookingCreationParams(Builder builder) {
        this.sportsFields = builder.sportsFields;
        this.date = builder.date;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.duration = builder.duration;
        this.requested = builder.requested;
        this.confirmed = builder.confirmed;
        this.user = builder.user;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public boolean getRequested() {
        return requested;
    }

    public boolean getConfirmed() {
        return confirmed;
    }

    public User getUser() {
        return user;
    }

    public List<SportsField> getSportsFields() {
        return sportsFields;
    }

    public static class Builder {

        private List<SportsField> sportsFields;
        private LocalTime startTime;
        private LocalTime endTime;
        private Duration duration;

        private LocalDate date;

        private boolean requested;
        private boolean confirmed;

        private User user;

        public Builder sportsFields(List<SportsField> sportsFields) {
            this.sportsFields = sportsFields;
            return this;
        }

        public Builder startTime(LocalTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder endTime(LocalTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder duration(Duration duration) {
            this.duration = duration;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public Builder requested(boolean requested) {
            this.requested = requested;
            return this;
        }

        public Builder confirmed(boolean confirmed) {
            this.confirmed = confirmed;
            return this;
        }

        public BookingCreationParams build() {
            return new BookingCreationParams(this);
        }
    }
}
