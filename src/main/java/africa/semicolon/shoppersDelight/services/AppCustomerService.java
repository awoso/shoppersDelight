package africa.semicolon.shoppersDelight.services;

import africa.semicolon.shoppersDelight.dtos.request.CustomerRegistrationRequest;
import africa.semicolon.shoppersDelight.dtos.request.UpdateCustomerRequest;
import africa.semicolon.shoppersDelight.dtos.response.ApiResponse;
import africa.semicolon.shoppersDelight.dtos.response.CustomerRegistrationResponse;
import africa.semicolon.shoppersDelight.dtos.response.UpdateCustomerResponse;
import africa.semicolon.shoppersDelight.exceptions.CustomerNotFoundException;
//import com.fasterxml.jackson.core.JsonPointer;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchOperation;
import com.github.fge.jsonpatch.ReplaceOperation;
import africa.semicolon.shoppersDelight.data.model.Customer;
import africa.semicolon.shoppersDelight.data.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.stream;

@Service
@AllArgsConstructor
public class AppCustomerService implements CustomerService{

    private final CustomerRepository customerRepository;


    @Override
    public CustomerRegistrationResponse register(CustomerRegistrationRequest request) {
        Customer customer = new Customer();
        customer.setEmail(request.getEmail());
        customer.setPassword(request.getPassword());

        Customer savedCustomer = customerRepository.save(customer);

        CustomerRegistrationResponse responds = new CustomerRegistrationResponse();
        responds.setId(customer.getId());

        return responds;
    }

    @Override
    public ApiResponse<UpdateCustomerResponse> updateCustomer(Long id, UpdateCustomerRequest request)throws CustomerNotFoundException {

        Customer customer = findCustomerBy(id);

        List<JsonPatchOperation> jsonPatchOperation = new ArrayList<>();
        buildPatchOperation(request, jsonPatchOperation);
        customer = applyPatch(jsonPatchOperation, customer);
        customerRepository.save(customer);
        return new ApiResponse<>(buildUpdateCustomerResponse());
    }

    private static UpdateCustomerResponse buildUpdateCustomerResponse() {
        UpdateCustomerResponse response = new UpdateCustomerResponse();
        response.setMessage("Account updated successfully");
        return response;
    }
    private static void buildPatchOperation(UpdateCustomerRequest request,List<JsonPatchOperation> jsonPatchOperations) {
        Class<?> c = request.getClass();
        Field[] fields = c.getDeclaredFields();
        System.out.println(Arrays.toString(fields));
        stream(fields).filter(field -> isValidUpdate(field, request))
                .forEach(field -> addOperation(request, field, jsonPatchOperations));
    }




    private Customer applyPatch(List<JsonPatchOperation> jsonPatchOperation,Customer customer){
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonPatch jsonPatch = new JsonPatch(jsonPatchOperation);
            JsonNode customerNode = mapper.convertValue(customer, JsonNode.class);
            JsonNode updateNode = jsonPatch.apply(customerNode);
            customer = mapper.convertValue(updateNode, Customer.class);
        }catch(Exception exception){
            throw new RuntimeException(exception);
        }
        return customer;
    }
    private static boolean isValidUpdate(Field field, UpdateCustomerRequest request) {
        field.setAccessible(true);
        try{
            return field.get(request) != null;
        }catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }
    private static void addOperation(UpdateCustomerRequest request, Field field, List<JsonPatchOperation> jsonPatchOperations) {
        try{
            JsonPointer path = new JsonPointer("/"+ field.getName());
            JsonNode value = new TextNode(field.get(request).toString());
            ReplaceOperation replaceOperation = new ReplaceOperation(path, value);
            jsonPatchOperations.add(replaceOperation);
        }catch(Exception e){
            throw  new RuntimeException(e);
        }
    }
    private Customer findCustomerBy(Long id)throws CustomerNotFoundException{
        return customerRepository.findById(id)
                .orElseThrow(()-> new CustomerNotFoundException(String.format("Customer with id %d not found", id)));
    }
}

