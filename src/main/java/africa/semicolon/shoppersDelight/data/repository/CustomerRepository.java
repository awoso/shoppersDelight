package africa.semicolon.shoppersDelight.data.repository;

import africa.semicolon.shoppersDelight.data.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
        Optional<Customer> findByEmail(String email);

    }

