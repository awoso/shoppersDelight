package africa.semicolon.shoppersDelight.data.repository;

import africa.semicolon.shoppersDelight.data.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
//    Optional<List<Product>> findDistinctByProductCategory(Category category);

}
