package pl.michal.tim_client.domain;

public class Training {
    public Long id;
    public Customer customer;
    public Coach coach;
    public String info;

    public String startTime;
    public String endTime;

    public Training(Customer customer, Coach coach, String startTime, String endTime) {
        this.customer = customer;
        this.coach = coach;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Training() {
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
