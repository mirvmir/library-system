package com.myapp.app.repository.implementation.hibernate;

import com.myapp.app.domain.entities.bookModel.BookModel;
import com.myapp.app.domain.entities.bookUnit.BookUnit;
import com.myapp.app.frameworks.dataAccess.hibernate.persistence.entities.BookUnitEntity;
import com.myapp.app.useCases.adapter.repository.interfaces.BookUnitRepository;
import com.myapp.app.useCases.services.dto.StaleUnitRow;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.time.LocalDate;
import java.util.*;

public class HibernateBookUnitRepository implements BookUnitRepository {

    private final SessionFactory sessionFactory;

    public HibernateBookUnitRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public BookUnit save(BookUnit bookUnit) {
        Session session = sessionFactory.getCurrentSession();

        BookUnitEntity entity = toEntity(bookUnit);
        session.persist(entity);
        return toDomain(entity);
    }

    @Override
    public BookUnit findByIsbn(BookModel bookModel) {
        Session session = sessionFactory.getCurrentSession();

        BookUnitEntity entity = session.createQuery("""
                SELECT b
                FROM BookUnitEntity b
                WHERE b.bookModel = :book
                    AND b.available = true
                """, BookUnitEntity.class)
                .setParameter("book", bookModel)
                .setMaxResults(1)
                .uniqueResult();
        return toDomain(entity);
    }

    @Override
    public List<BookUnit> findAll() {
        Session session = sessionFactory.getCurrentSession();

        List<BookUnitEntity> booksEntity = session.createQuery("FROM BookUnitEntity", BookUnitEntity.class).getResultList();
        return booksEntity.stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public BookUnit update(BookUnit bookUnit) {
        Session session = sessionFactory.getCurrentSession();

        BookUnitEntity entity = toEntity(bookUnit);
        session.merge(entity);
        return toDomain(entity);
    }

    @Override
    public List<StaleUnitRow> findStaleUnitsByDeliveryDate(LocalDate staleDate, String direction) {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery("""
                SELECT new com.senla.app.useCases.services.dto.StaleUnitRow(
                    bu.id,
                    bm.isbn,
                    bu.deliveryDate,
                    bm.price,
                    bm.title,
                    bm.author
                )
                FROM BookUnitEntity bu
                JOIN bu.bookModel bm
                WHERE bu.available = true
                    AND bu.deliveryDate < :staleDate
                ORDER BY bu.deliveryDate""" + " " + direction,
                        StaleUnitRow.class)
                .setParameter("staleDate", staleDate)
                .getResultList();
    }

    @Override
    public List<StaleUnitRow> findStaleUnitsByPrice(LocalDate staleDate, String direction) {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery("""
                SELECT new com.senla.app.useCases.services.dto.StaleUnitRow(
                    bu.id,
                    bm.isbn,
                    bu.deliveryDate,
                    bm.price,
                    bm.title,
                    bm.author
                )
                FROM BookUnitEntity bu
                JOIN bu.bookModel bm
                WHERE bu.available = true
                    AND bu.deliveryDate < :staleDate
                ORDER BY bm.price""" + " " + direction,
                        StaleUnitRow.class)
                .setParameter("staleDate", staleDate)
                .getResultList();
    }

    @Override
    public List<Long> reserveBookUnitId(List<String> isbns) {
        Session session = sessionFactory.getCurrentSession();

        List<Long> reserved = new ArrayList<>();

        for (String isbn : isbns) {
            Number n = (Number) session.createNativeQuery(
                            """
                            SELECT u.id
                            FROM book_unit u
                            JOIN book_model bm ON bm.isbn = u.isbn
                            WHERE bm.isbn = :isbn
                                AND u.available = true
                            FOR UPDATE SKIP LOCKED
                            LIMIT 1
                            """)
                    .setParameter("isbn", isbn)
                    .uniqueResult();

            if (null == n) {
                continue;
            }

            Long unitId = n.longValue();

            BookUnitEntity unit = session.get(BookUnitEntity.class, unitId);
            unit.setAvailable(false);
            reserved.add(unitId);
        }

        return reserved;
    }

    private BookUnit toDomain(BookUnitEntity entity) {
        BookUnit bookUnit = new BookUnit(
                entity.getBookModel().getIsbn(),
                entity.isAvailable(),
                entity.getDeliveryDate()
        );

        bookUnit.setId(entity.getId());
        return bookUnit;
    }

    private BookUnitEntity toEntity(BookUnit domain) {
        Session session = sessionFactory.getCurrentSession();

        BookUnitEntity entity = new BookUnitEntity();

        BookModel bookRef = session.getReference(BookModel.class, domain.getIsbn());

        entity.setBookModel(bookRef);
        entity.setDeliveryDate(domain.getDeliveryDate());
        entity.setAvailable(domain.isAvailable());
        return entity;
    }
}
