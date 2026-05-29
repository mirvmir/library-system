package io.github.mirvmir.repository.implementation.hibernate;

import io.github.mirvmir.domain.entities.bookModel.BookModel;
import io.github.mirvmir.domain.entities.bookUnit.BookUnit;
import io.github.mirvmir.frameworks.dataAccess.hibernate.persistence.entities.BookUnitEntity;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BookUnitRepository;
import io.github.mirvmir.useCases.services.dto.StaleUnitRow;
import jakarta.persistence.LockModeType;
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

        List<BookUnitEntity> booksEntity = session
                .createQuery("FROM BookUnitEntity", BookUnitEntity.class)
                .getResultList();
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
    public List<StaleUnitRow> findStaleUnitsByDeliveryDate(LocalDate staleDate,
                                                           String direction) {
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
    public List<StaleUnitRow> findStaleUnitsByPrice(LocalDate staleDate,
                                                    String direction) {
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
    public BookUnit findByIsbnForUpdate(String isbn) {
        Session session = sessionFactory.getCurrentSession();

        BookUnitEntity entity = session.createQuery("""
                SELECT bu
                FROM BookUnitEntity bu
                JOIN bu.bookModel bm
                WHERE bm.isbn = :isbn
                    AND bu.available = true
                ORDER BY bu.deliveryDate ASC, bu.id ASC
                """, BookUnitEntity.class)
                .setParameter("isbn", isbn)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .setMaxResults(1)
                .uniqueResult();

        return toDomain(entity);
    }

    @Override
    public void markSold(List<Long> bookUnitIds) {
        if (bookUnitIds == null || bookUnitIds.isEmpty()) {
            return;
        }

        Session session = sessionFactory.getCurrentSession();

        session.createQuery("""
                UPDATE BookUnitEntity bu
                SET bu.available = false
                WHERE bu.id IN (:ids)
                """)
                .setParameter("ids", bookUnitIds)
                .executeUpdate();
    }

    @Override
    public void releaseReservedUnits(List<Long> bookUnitIds) {
        if (bookUnitIds == null || bookUnitIds.isEmpty()) {
            return;
        }

        Session session = sessionFactory.getCurrentSession();

        session.createQuery("""
                UPDATE BookUnitEntity bu
                SET bu.available = true
                WHERE bu.id IN (:ids)
                """)
                .setParameter("ids", bookUnitIds)
                .executeUpdate();
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
