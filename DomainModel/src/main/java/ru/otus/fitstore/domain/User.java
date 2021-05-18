package ru.otus.fitstore.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
@ToString
public class User {
    @Id
    private String id;
    private String username;
    @JsonIgnore
    @ToString.Exclude
    private String password;
    @JsonIgnore
    @ToString.Exclude
    private String[] roles;

    private String firstName;
    private String lastName;
    private Short maxHeartRate;
}