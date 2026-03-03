package com.myapp.app.useCases.services.implementation;

import com.myapp.app.domain.entities.customer.Customer;
import com.myapp.app.useCases.adapter.repository.interfaces.CustomerRepository;
import com.myapp.app.useCases.services.interfaces.CreateCustomerService;
import com.myapp.app.useCases.services.inputs.CreateCustomerInput;
import com.myapp.app.useCases.services.outputs.CreateCustomerOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefaultCreateCustomerService implements CreateCustomerService {

    private final CustomerRepository customerRepo;

    public DefaultCreateCustomerService(CustomerRepository customerRepo) {
        this.customerRepo = customerRepo;
    }

    @Override
    @Transactional
    public CreateCustomerOutput execute(CreateCustomerInput params) {
        Customer newCustomer = customerRepo.save(Customer.createNew());
        return new CreateCustomerOutput(newCustomer.getId());
    }
}
