package pl.michal.tim_client.user;

import pl.michal.tim_client.domain.Customer;

public class User {
    private Long id;
    private String username;
    private String password;
    private boolean active;
    private Roles roles;
    private String token;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Roles getRoles() {
        return roles;
    }

    public void setRoles(Roles roles) {
        this.roles = roles;
    }

    public void setRoles(String roles) {
        if (roles.trim().toLowerCase().equals("customer"))
            this.roles = Roles.CUSTOMER;
        else if (roles.trim().toLowerCase().equals("coach"))
            this.roles = Roles.COACH;
    }

    public String getToken() {
        return token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public enum Roles {
        CUSTOMER("customer"),
        COACH("coach");

        private String friendlyName;

        Roles(String friendlyName) {
            this.friendlyName = friendlyName;
        }

        @Override
        public String toString() {
            return friendlyName;
        }
    }
}
