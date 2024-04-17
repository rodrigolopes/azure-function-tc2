package com.example.azurefunctiontc;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CustomerAzureFunction {

    private final CustomerRepository customerRepository;

    public CustomerAzureFunction(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @FunctionName("users")
    public HttpResponseMessage getUsers(
            @HttpTrigger(name = "req",
                    methods = { HttpMethod.GET },
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            ExecutionContext context) {
        return request
                .createResponseBuilder(HttpStatus.OK)
                .body(customerRepository.findAll())
                .header("Content-Type", "application/json")
                .build();
    }
}
