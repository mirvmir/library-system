package com.myapp.app.repository.implementation.hibernate;

import com.myapp.app.domain.entities.bookModel.BookModel;
import com.myapp.app.domain.entities.customer.Customer;
import com.myapp.app.domain.entities.request.BookRequest;
import com.myapp.app.frameworks.dataAccess.hibernate.persistence.entities.BookRequestEntity;
import com.myapp.app.useCases.adapter.repository.interfaces.BookRequestRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

public class HibernateBookRequestRepository implements BookRequestRepository {

    private final SessionFactory sessionFactory;

    public HibernateBookRequestRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void deleteByIsbn(String isbn) {
        Session session = sessionFactory.getCurrentSession();

        session.createQuery("""
                 DELETE FROM BookRequestEventEntity e
                 WHERE e.isbn = :isbn
                 """)
                .setParameter("isbn", isbn)
                .executeUpdate();
    }

    @Override
    public BookRequest save(BookRequest bookRequest) {
        Session session = sessionFactory.getCurrentSession();

        BookRequestEntity entity = toEntity(bookRequest);
        session.persist(entity);
        return toDomain(entity);
    }

    @Override
    public void saveAll(List<BookRequest> requests) {
        Session session = sessionFactory.getCurrentSession();

        for (BookRequest request : requests) {
            session.persist(toEntity(request));
        }
    }

    private BookRequest toDomain(BookRequestEntity entity) {
        if (null == entity) return null;

        BookRequest request = new BookRequest(
                entity.getBookModel().getIsbn(),
                entity.getCustomer().getId()
        );

        request.setId(entity.getId());
        return request;
    }

    private BookRequestEntity toEntity(BookRequest domain) {
        Session session = sessionFactory.getCurrentSession();

        BookRequestEntity entity = new BookRequestEntity();

        BookModel bookRef = session.getReference(BookModel.class, domain.getIsbn());
        Customer customerRef = session.getReference(Customer.class, domain.getCustomerId());

        entity.setBookModel(bookRef);
        entity.setCustomer(customerRef);
        return entity;
    }
}
