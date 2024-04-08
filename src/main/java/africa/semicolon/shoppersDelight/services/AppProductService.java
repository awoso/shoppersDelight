package africa.semicolon.shoppersDelight.services;

import africa.semicolon.shoppersDelight.dtos.request.AddProductRequest;
import africa.semicolon.shoppersDelight.dtos.request.UpdateProductRequest;
import africa.semicolon.shoppersDelight.dtos.response.AddProductResponse;
import africa.semicolon.shoppersDelight.dtos.response.ProductResponse;
import africa.semicolon.shoppersDelight.dtos.response.UpdateProductResponse;
import africa.semicolon.shoppersDelight.exceptions.CustomerNotFoundException;
import africa.semicolon.shoppersDelight.exceptions.ProductNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchOperation;
import com.github.fge.jsonpatch.ReplaceOperation;
import africa.semicolon.shoppersDelight.data.model.Category;
import africa.semicolon.shoppersDelight.data.model.Product;
import africa.semicolon.shoppersDelight.data.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Store;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.math.BigInteger.ONE;
import static java.util.Arrays.stream;

@Service
@AllArgsConstructor
@Slf4j
public class AppProductService implements ProductService {
    private static final int DEFAULT_PAGE_NUMBER = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;

    private final ProductRepository productRepository;
    private final ModelMapper mapper = new ModelMapper();


    @Override
    public AddProductResponse addProduct(AddProductRequest request, Store store) throws Exception {
        Category category = categoryCheck(request.getCategory());
        ModelMapper mapper = new ModelMapper();
        Product product = mapper.map(request, Product.class);
        Product savedProduct = productRepository.save(product);

        return mapper.map(savedProduct, AddProductResponse.class);
    }

    @Override
    public AddProductResponse addProduct(AddProductRequest request) {
        return null;
    }

    @Override
    public ProductResponse getProductBy(Long id) {
        return mapper.map(findById(id),ProductResponse.class);
    }

    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(productRepository.findById(id).orElseThrow(
                () -> new ProductNotFoundException("product not found")
        ));
    }



    @Override
    public List<ProductResponse> getProduct(int page, int size) {
        Page<Product> productPage = productRepository.findAll(createPageRequest(page, size));
        log.info("page --->{}", productPage);
        return productPage.getContent()
                .stream()
                .map(product -> mapper.map(product, ProductResponse.class))
                .toList();
    }

    private static  Pageable createPageRequest(int page, int size) {
        if(page < DEFAULT_PAGE_NUMBER) page = DEFAULT_PAGE_NUMBER;
        if(size < ONE.intValue()) size = DEFAULT_PAGE_SIZE;
        page = page - ONE.intValue();
        return PageRequest.of(page, size);

    }
    private Category categoryCheck(String category) throws Exception{
        for (Category cate : Category.values()){
            if(cate.name().equalsIgnoreCase(category)){
                return cate;
            }
        }
        throw new Exception("Category Not Found");
    }
    @Override
    public UpdateProductResponse updateProduct(UpdateProductRequest updateProductRequest) throws Exception {
        Product product = findProductById(updateProductRequest.getProductId());

        List<JsonPatchOperation> jsonPatchOperations = new ArrayList<>();

        buildPatchOperations(updateProductRequest, jsonPatchOperations);
        product = applyPatch(jsonPatchOperations, product);
        UpdateProductResponse response = buidUpdateProductResponse();
        response.setId(product.getId());
        return response;
    }



    private UpdateProductResponse buidUpdateProductResponse(){
        UpdateProductResponse response = new UpdateProductResponse();
        response.setMessage("Product updated successfully");
        return response;
    }
    private void buildPatchOperations(UpdateProductRequest request, List<JsonPatchOperation> jsonPatchOperations) {
        Class<?> c = request.getClass();
        Field[]  fields = c.getDeclaredFields();
        stream(fields)
                .filter(field -> isValidUpdate(field, request))
                .forEach(field-> addOperation(request, field, jsonPatchOperations));
    }



    private boolean isValidUpdate(Field field, UpdateProductRequest request) {
        System.out.println(field);
        field.setAccessible(true);
        try{
            return field.get(request) != null &&
                    !(field.getName().equalsIgnoreCase("storeId")
                    || field.getName().equalsIgnoreCase("productId"));

        }catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }
    private void addOperation(UpdateProductRequest request, Field field, List<JsonPatchOperation> jsonPatchOperations) {
        try{
            JsonPointer path = new JsonPointer("/" + field.getName());
            JsonNode value = new TextNode(field.get(request).toString());
            ReplaceOperation replaceOperation = new ReplaceOperation(path, value);
            jsonPatchOperations.add(replaceOperation);

        }catch (Exception e){
            throw  new RuntimeException(e);
        }
    }
    private Product applyPatch(List<JsonPatchOperation> jsonPatchOperations, Product product) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonPatch jsonPatch = new JsonPatch(jsonPatchOperations);
            JsonNode customerNode = mapper.convertValue(product, JsonNode.class);
            JsonNode updateNode = jsonPatch.apply(customerNode);
            product = mapper.convertValue(updateNode, Product.class);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        return product;
    }
    private Product findProductById(Long id) throws CustomerNotFoundException {
        return productRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(
                        String.format("product with id %d not found", id)));
    }


}
