package africa.semicolon.shoppersDelight.services;

import africa.semicolon.shoppersDelight.dtos.request.AddProductRequest;
import africa.semicolon.shoppersDelight.dtos.request.UpdateProductRequest;
import africa.semicolon.shoppersDelight.dtos.response.AddProductResponse;
import africa.semicolon.shoppersDelight.dtos.response.ProductResponse;
import africa.semicolon.shoppersDelight.dtos.response.UpdateProductResponse;
import africa.semicolon.shoppersDelight.data.model.Product;
import org.apache.catalina.Store;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    AddProductResponse addProduct(AddProductRequest request, Store store) throws Exception;

    AddProductResponse addProduct(AddProductRequest request);
    ProductResponse getProductBy(Long id);
    List<ProductResponse> getProduct(int page, int size);
    Optional<Product>findById(Long id);
    UpdateProductResponse updateProduct(UpdateProductRequest updateProductRequest) throws Exception;
}
