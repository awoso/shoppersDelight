package africa.semicolon.shoppersDelight.services;

import africa.semicolon.shoppersDelight.dtos.request.AddProductRequest;
import africa.semicolon.shoppersDelight.dtos.response.AddProductResponse;
import africa.semicolon.shoppersDelight.dtos.response.ProductResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Slf4j
public class ProductServiceTest {
    @Autowired
    private ProductService productService;

    @Test
    public void addProductTest(){
        AddProductRequest  request = new AddProductRequest();
        request.setName("strawberry");
        request.setPrice(BigDecimal.TEN);
        request.setDescription("sweeeeeet!");
        request.setQuantity(20);


        AddProductResponse response = productService.addProduct(request);
        log.info("product added :: {}", response);
        assertThat(response).isNotNull();
    }
    @Test
    @Sql(scripts = {"/scripts/insert.sql"})
    public void getproduct() {
        ProductResponse response = productService.getProductBy(200l);
        log.info("found product ->{}", response);
        assertThat(response).isNotNull();

    }
    @Test
    @Sql (scripts = {"/script/insert.sql"})
    public void getProduct(){
        List<ProductResponse> response = productService.getProduct(1,5);
        assertThat(response).isNotNull();
    }

}
