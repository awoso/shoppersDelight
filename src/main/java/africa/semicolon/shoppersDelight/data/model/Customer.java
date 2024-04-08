package africa.semicolon.shoppersDelight.data.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
public class Customer {
        @Id
        @GeneratedValue(strategy = IDENTITY)
        private Long id;
        private String email;
        private String password;
        private String address;
        private String phoneNumber;
}

