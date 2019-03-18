package pl.michal.tim_client.domain;

import java.util.List;

public class Coach {

    public Long id;
    public String name;
    public String surname;
    public String email;
    public List<Training> acceptedTrainings;
    public List<Training> proposedTrainings;

    public Coach(Long id, String name, String surname, String email, List<Training> acceptedTrainings, List<Training> proposedTrainings) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.acceptedTrainings = acceptedTrainings;
        this.proposedTrainings = proposedTrainings;
    }

    public Long getId() {
        return id;
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

    public List<Training> getAcceptedTrainings() {
        return acceptedTrainings;
    }

    public void setAcceptedTrainings(List<Training> acceptedTrainings) {
        this.acceptedTrainings = acceptedTrainings;
    }

    public List<Training> getProposedTrainings() {
        return proposedTrainings;
    }

    public void setProposedTrainings(List<Training> proposedTrainings) {
        this.proposedTrainings = proposedTrainings;
    }
}
