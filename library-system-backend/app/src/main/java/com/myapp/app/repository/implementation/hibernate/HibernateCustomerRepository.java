package com.myapp.app.repository.implementation.hibernate;

import com.myapp.app.domain.entities.customer.Customer;
import com.myapp.app.useCases.adapter.repository.interfaces.CustomerRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class HibernateCustomerRepository implements CustomerRepository {

    private final SessionFactory sessionFactory;

    public HibernateCustomerRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Customer save(Customer customer) {
        Session session = sessionFactory.getCurrentSession();

        session.persist(customer);
        return customer;
    }

    @Override
    public boolean existCustomer(Long customerId) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(Customer.class, customerId) != null;
    }

    @Override
    public Customer findById(Long customerId) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(Customer.class, customerId);
    }
}
