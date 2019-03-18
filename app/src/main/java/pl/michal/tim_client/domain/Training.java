package pl.michal.tim_client.domain;

import java.time.LocalDateTime;

public class Training {
    public  Long id;
    public Customer customer;
    public Coach coach;

    public LocalDateTime startTime;
    public LocalDateTime endTime;

    public Training(Customer customer, Coach coach, LocalDateTime startTime, LocalDateTime endTime) {
        this.customer = customer;
        this.coach = coach;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getId() {
        return id;
    }


    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Coach getCoach() {
        return coach;
    }

    public void setCoach(Coach coach) {
        this.coach = coach;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
