package pl.michal.tim_client.domain;

import java.time.LocalDateTime;

public class Training {
    public Long id;
    public Customer customer;
    public Coach coach;
    public String info;

    public LocalDateTime startTime;
    public LocalDateTime endTime;
    public boolean accepted;

    public Training(Customer customer, Coach coach, LocalDateTime startTime, LocalDateTime endTime, String info) {
        this.customer = customer;
        this.coach = coach;
        this.startTime = startTime;
        this.endTime = endTime;
        this.info = info;
    }
    public Training(Long id ,Customer customer, Coach coach, LocalDateTime startTime, LocalDateTime endTime, String info) {
        this.id = id;
        this.customer = customer;
        this.coach = coach;
        this.startTime = startTime;
        this.endTime = endTime;
        this.info = info;
    }

    public Training(Customer customer, Coach coach, LocalDateTime startTime, LocalDateTime endTime, String info, boolean accepted) {
        this.id = id;
        this.customer = customer;
        this.coach = coach;
        this.startTime = startTime;
        this.endTime = endTime;
        this.info = info;
        this.accepted =accepted;
    }

    public Training() {
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public Long getId() {
        return id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
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
