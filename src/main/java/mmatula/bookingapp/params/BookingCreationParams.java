package mmatula.bookingapp.params;

import mmatula.bookingapp.model.SportsField;
import mmatula.bookingapp.model.User;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class BookingCreationParams {

    private final SportsField sportsField;
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final Duration duration;

    private final Boolean requested;
    private final Boolean confirmed;

    private final User user;

    public SportsField getSportsField() {
        return sportsField;
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

    public Boolean getRequested() {
        return requested;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public User getUser() {
        return user;
    }

    private BookingCreationParams(Builder builder) {
        this.sportsField = builder.sportsField;
        this.date = builder.date;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.duration = builder.duration;
        this.requested = builder.requested;
        this.confirmed = builder.confirmed;
        this.user = builder.user;
    }

    public static class Builder {

        private SportsField sportsField;
        private LocalTime startTime;
        private LocalTime endTime;
        private Duration duration;

        private LocalDate date;

        private Boolean requested;
        private Boolean confirmed;

        private User user;

        public Builder sportsField(SportsField sportsField) {
            this.sportsField = sportsField;
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

        public Builder requested(Boolean requested) {
            this.requested = requested;
            return this;
        }

        public Builder confirmed(Boolean confirmed) {
            this.confirmed = confirmed;
            return this;
        }

        public BookingCreationParams build() {
            return new BookingCreationParams(this);
        }
    }
}
