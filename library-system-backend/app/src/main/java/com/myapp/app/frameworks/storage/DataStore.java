package com.myapp.app.frameworks.storage;

import com.myapp.app.domain.entities.bookModel.BookModel;
import com.myapp.app.domain.entities.bookUnit.BookUnit;
import com.myapp.app.domain.entities.customer.Customer;
import com.myapp.app.domain.entities.order.Order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class DataStore {

    private List<BookModel> models;
    private List<BookUnit> units;
    private List<Customer> customers;
    private List<Order> orders;
    private Map<String, Integer> requests;

    @JsonCreator
    public DataStore(
            @JsonProperty("models")
            List<BookModel> models,
            @JsonProperty("units")
            List<BookUnit> units,
            @JsonProperty("customers")
            List<Customer> customers,
            @JsonProperty("orders")
            List<Order> orders,
            @JsonProperty("requests")
            Map<String, Integer> requests
    ) {
        this.models = models;
        this.units = units;
        this.customers = customers;
        this.orders = orders;
        this.requests = requests;
    }

    public List<BookModel> getModels() {
        return models;
    }
    public List<BookUnit> getUnits() {
        return units;
    }
    public List<Customer> getCustomers() {
        return customers;
    }
    public List<Order> getOrders() {
        return orders;
    }
    public Map<String, Integer> getRequests() {
        return requests;
    }
}

