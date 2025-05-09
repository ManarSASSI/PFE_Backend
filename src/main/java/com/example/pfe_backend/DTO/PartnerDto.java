package com.example.pfe_backend.DTO;

public class PartnerDto {
    private Long id;
    private String username;
    private String phone;
    private String email;
    private String location;


    public PartnerDto(Long id, String username, String phone, String email, String location) {
        this.id = id;
        this.username = username;
        this.phone = phone;
        this.email = email;
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
