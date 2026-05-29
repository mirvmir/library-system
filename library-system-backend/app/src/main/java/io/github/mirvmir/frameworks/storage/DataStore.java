package io.github.mirvmir.frameworks.storage;

import io.github.mirvmir.domain.entities.bookModel.BookModel;
import io.github.mirvmir.domain.entities.bookUnit.BookUnit;
import io.github.mirvmir.domain.entities.user.User;
import io.github.mirvmir.domain.entities.order.Order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class DataStore {

    private List<BookModel> models;
    private List<BookUnit> units;
    private List<User> users;
    private List<Order> orders;
    private Map<String, Integer> requests;

    @JsonCreator
    public DataStore(
            @JsonProperty("models")
            List<BookModel> models,
            @JsonProperty("units")
            List<BookUnit> units,
            @JsonProperty("customers")
            List<User> users,
            @JsonProperty("orders")
            List<Order> orders,
            @JsonProperty("requests")
            Map<String, Integer> requests
    ) {
        this.models = models;
        this.units = units;
        this.users = users;
        this.orders = orders;
        this.requests = requests;
    }

    public List<BookModel> getModels() {
        return models;
    }
    public List<BookUnit> getUnits() {
        return units;
    }
    public List<User> getCustomers() {
        return users;
    }
    public List<Order> getOrders() {
        return orders;
    }
    public Map<String, Integer> getRequests() {
        return requests;
    }
}

