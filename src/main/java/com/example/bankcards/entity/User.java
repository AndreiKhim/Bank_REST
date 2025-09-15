package com.example.bankcards.entity;

import jakarta.persistence.*;
import jakarta.persistence.OneToMany;
import com.example.bankcards.entity.enums.Role;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Card> cards;


    public User() {
    }

    public User(String lastName, String firstName, String email, String phoneNumber, String password, Role role) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    // Сеттер для id
    public void setId(Long id) {
        this.id = id;
    }

    // Геттер для lastName
    public String getLastName() {
        return lastName;
    }

    // Сеттер для lastName
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // Геттер для firstName
    public String getFirstName() {
        return firstName;
    }

    // Сеттер для firstName
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    // Геттер для email
    public String getEmail() {
        return email;
    }

    // Сеттер для email
    public void setEmail(String email) {
        this.email = email;
    }

    // Геттер для phoneNumber
    public String getPhoneNumber() {
        return phoneNumber;
    }

    // Сеттер для phoneNumber
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    // Геттер для role
    public Role getRole() {
        return role;
    }

    // Сеттер для role
    public void setRole(Role role) {
        this.role = role;
    }

    // Геттер для cards
    public List<Card> getCards() {
        return cards;
    }

    // Cеттер для cards
    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

}
