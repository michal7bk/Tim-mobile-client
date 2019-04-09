package pl.michal.tim_client.customer.model;

import pl.michal.tim_client.domain.Training;

import java.util.List;

public class Customer {

    public Long id;
    public String name;
    public String surname;
    public String email;
    public List<Training> trainings;

    public Customer(Long id, String name, String surname, String email, List<Training> trainings) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.trainings = trainings;
    }

    public Customer() {
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return name + " " + surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Training> getTrainings() {
        return trainings;
    }

    public void setTrainings(List<Training> trainings) {
        this.trainings = trainings;
    }
}
