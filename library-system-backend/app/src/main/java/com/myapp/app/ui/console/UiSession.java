package com.myapp.app.ui.console;

import com.myapp.app.ui.console.consoleViewModels.consoleEntity.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UiSession {
    private CustomerViewModel currentCustomer;
    private List<BookViewModel> books = new ArrayList<>();
    private List<OrderViewModel> orders = new ArrayList<>();
    private List<BookRequestViewModel> requests = new ArrayList<>();

    private Boolean bookSort = false;
    private Boolean orderSort = false;
    private Boolean requestSort = false;

    public CustomerViewModel getCurrentCustomer() {
        return currentCustomer;
    }

    public void setCurrentCustomer(CustomerViewModel currentCustomer) {
        this.currentCustomer = currentCustomer;
    }

    public List<BookViewModel> getBooks() {
        return books;
    }

    public void setBooks(List<BookViewModel> books) {
        this.books = (null == books) ? new ArrayList<>() : books;
    }

    public List<OrderViewModel> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderViewModel> orders) {
        this.orders = orders;
    }

    public List<BookRequestViewModel> getRequests() {
        return requests;
    }

    public void setRequests(List<BookRequestViewModel> requests) {
        this.requests = requests;
    }

    public Boolean getBookSort() {
        return bookSort;
    }

    public void setBookSort(Boolean bookSort) {
        this.bookSort = bookSort;
    }

    public Boolean getOrderSort() {
        return orderSort;
    }

    public void setOrderSort(Boolean orderSort) {
        this.orderSort = orderSort;
    }

    public Boolean getRequestSort() {
        return requestSort;
    }

    public void setRequestSort(Boolean requestSort) {
        this.requestSort = requestSort;
    }
}
