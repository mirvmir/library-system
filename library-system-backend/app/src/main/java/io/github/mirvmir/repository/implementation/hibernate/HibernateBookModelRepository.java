package io.github.mirvmir.repository.implementation.hibernate;

import io.github.mirvmir.domain.entities.bookModel.BookModel;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BookModelRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HibernateBookModelRepository implements BookModelRepository {

    private final SessionFactory sessionFactory;

    public HibernateBookModelRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public BookModel save(BookModel bookModel) {
        Session session = sessionFactory.getCurrentSession();

        session.persist(bookModel);

        session.flush();

        return bookModel;
    }

    @Override
    public List<BookModel> findAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("FROM BookModel", BookModel.class).getResultList();
    }

    @Override
    public BookModel findByIsbn(String isbn) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(BookModel.class, isbn);
    }

    @Override
    public void update(BookModel bookModel) {
        Session session = sessionFactory.getCurrentSession();
        session.merge(bookModel);
    }

    @Override
    public Set<String> findUnavailableIsbns(List<String> isbns) {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery("""
                SELECT b.isbn
                FROM BookModel b
                WHERE b.isbn in :isbns
                    AND b.stockCount <= 0
                """, String.class)
                .setParameter("isbns", isbns)
                .getResultStream()
                .collect(Collectors.toSet());
    }
}
