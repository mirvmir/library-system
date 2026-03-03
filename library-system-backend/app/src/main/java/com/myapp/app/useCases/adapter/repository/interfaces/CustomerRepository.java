package com.myapp.app.useCases.adapter.repository.interfaces;

import com.myapp.app.domain.entities.customer.Customer;

public interface CustomerRepository {
    Customer save(Customer customer);
    boolean existCustomer(Long customerId);
    Customer findById(Long customerId);
}
