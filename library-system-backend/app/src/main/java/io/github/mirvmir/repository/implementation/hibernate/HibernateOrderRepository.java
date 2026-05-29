package io.github.mirvmir.repository.implementation.hibernate;

import io.github.mirvmir.domain.entities.bookModel.BookModel;
import io.github.mirvmir.domain.entities.user.User;
import io.github.mirvmir.domain.entities.order.Order;
import io.github.mirvmir.domain.entities.order.OrderItem;
import io.github.mirvmir.domain.entities.order.OrderStatus;
import io.github.mirvmir.frameworks.dataAccess.hibernate.persistence.entities.OrderEntity;
import io.github.mirvmir.frameworks.dataAccess.hibernate.persistence.entities.OrderItemEntity;
import io.github.mirvmir.useCases.adapter.repository.interfaces.OrderRepository;
import jakarta.persistence.LockModeType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class HibernateOrderRepository implements OrderRepository {

    private final SessionFactory sessionFactory;

    public HibernateOrderRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Order save(Order order) {
        Session session = sessionFactory.getCurrentSession();

        BigDecimal totalPrice = session.createQuery("""
                SELECT SUM(b.price)
                FROM BookModel b
                WHERE b.isbn in :isbns
                """, BigDecimal.class)
                .setParameter("isbns", order.getItems().stream().map(OrderItem::getBookIsbn).toList())
                .uniqueResult();
        order.setTotalPrice(totalPrice);

        OrderEntity entity = toEntity(order);
        session.persist(entity);
        return toDomain(entity);
    }

    @Override
    public void saveAll(List<Order> orders) {
        Session session = sessionFactory.getCurrentSession();

        List<String> allIsbns = orders.stream()
                .flatMap(o -> o.getItems().stream())
                .map(OrderItem::getBookIsbn)
                .toList();

        Map<String, BigDecimal> priceByIsbn = new HashMap<>();
        List<Object[]> rows = session.createQuery("""
                SELECT b.isbn, b.price
                FROM BookModel b
                WHERE b.isbn IN :isbns
                """, Object[].class)
                .setParameter("isbns", allIsbns)
                .getResultList();
        for (Object[] r : rows) {
            priceByIsbn.put((String) r[0], new BigDecimal(r[1].toString()));
        }

        for (Order order : orders) {
            BigDecimal total = new BigDecimal("0");

            for (OrderItem item : order.getItems()) {
                if (null == priceByIsbn.get(item.getBookIsbn())) {
                    throw new IllegalStateException("Книга с isbn=" + item.getBookIsbn() + " не существует.");
                }
                BigDecimal price = priceByIsbn.get(item.getBookIsbn());
                total = total.add(price);
            }

            order.setTotalPrice(total);

            OrderEntity entity = toEntity(order);
            session.persist(entity);
        }
    }

    @Override
    public Order findById(Long orderId) {
        Session session = sessionFactory.getCurrentSession();

        OrderEntity entity = session.get(OrderEntity.class, orderId);
        return null == entity
                ? null
                : toDomain(entity);
    }

    @Override
    public Order findByIdForUpdate(Long orderId) {
        OrderEntity entity = sessionFactory.getCurrentSession()
                .find(
                        OrderEntity.class,
                        orderId,
                        LockModeType.PESSIMISTIC_WRITE
                );

        return toDomain(entity);
    }

    @Override
    public List<Order> findExpiredForUpdate(LocalDateTime now) {
        return sessionFactory.getCurrentSession()
                .createQuery("""
                        select distinct o
                        from OrderEntity o
                        left join fetch o.items
                        where o.expiresAt <= :now
                          and o.status in (:statuses)
                        order by o.expiresAt asc
                        """, OrderEntity.class)
                .setParameter("now", now)
                .setParameter("statuses", List.of(
                        OrderStatus.CREATED,
                        OrderStatus.PAYMENT_PROCESSING
                ))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getResultList()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Order> findAll() {
        Session session = sessionFactory.getCurrentSession();

        List<OrderEntity> ordersEntity = session.createQuery(("FROM OrderEntity"), OrderEntity.class).getResultList();

        return ordersEntity.stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Order> findAllByUserId(Long userId) {
        Session session = sessionFactory.getCurrentSession();

        List<OrderEntity> ordersEntity = session.createQuery("""
                SELECT DISTINCT o
                FROM OrderEntity o
                LEFT JOIN FETCH o.items i
                LEFT JOIN FETCH i.bookModel
                WHERE o.user.id = :userId
                """, OrderEntity.class)
                .setParameter("userId", userId)
                .getResultList();

        return ordersEntity.stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Order update(Order order) {
        Session session = sessionFactory.getCurrentSession();

        OrderEntity entity = toEntity(order);
        session.merge(entity);
        return toDomain(entity);
    }

    @Override
    public BigDecimal calculateTotalEarningsByPeriod(LocalDateTime from, LocalDateTime to) {
        Session session = sessionFactory.getCurrentSession();

        BigDecimal result = session.createQuery("""
                SELECT SUM(o.totalPrice)
                FROM OrderEntity o
                WHERE o.completionDate >= :from
                    AND o.completionDate <= :to
                    AND o.status = :status
                """, BigDecimal.class)
                .setParameter("from", from)
                .setParameter("to", to)
                .setParameter("status", OrderStatus.COMPLETED)
                .uniqueResult();

        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public long countCompletedByPeriod(LocalDateTime from, LocalDateTime to) {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery("""
                SELECT COUNT(o)
                FROM OrderEntity o
                WHERE o.completionDate >= :from
                    AND o.completionDate <= :to
                    AND o.status = :status
                """, Long.class)
                .setParameter("from", from)
                .setParameter("to", to)
                .setParameter("status", OrderStatus.COMPLETED)
                .uniqueResult();
    }

    private Order toDomain(OrderEntity entity) {
        List<OrderItem> items = entity.getItems().stream()
                .map(this::toDomainItem)
                .toList();

        Order order = new Order(
                entity.getCustomer().getId(),
                items,
                entity.getStatus(),
                entity.getTotalPrice(),
                entity.getCreatedAt(),
                entity.getCompletionAt(),
                entity.getExpiresAt()
        );
        order.setId(entity.getId());
        return order;
    }

    private OrderItem toDomainItem(OrderItemEntity itemEntity) {
        OrderItem item = new OrderItem(
                itemEntity.getBookId(),
                itemEntity.getBookModel().getIsbn()
        );

        item.setId(itemEntity.getId());

        return item;
    }

    private OrderEntity toEntity(Order domain) {
        Session session = sessionFactory.getCurrentSession();

        OrderEntity entity = new OrderEntity();

        User userRef = session.getReference(User.class, domain.getCustomerId());
        entity.setCustomer(userRef);

        entity.setId(domain.getId());
        entity.setCompletionAt(domain.getCompletionAt());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setExpiresAt(domain.getExpiresAt());
        entity.setStatus(domain.getStatus());
        entity.setTotalPrice(domain.getTotalPrice());

        List<OrderItemEntity> itemsEntity = domain.getItems().stream()
                .map(item -> toEntityItem(item, session))
                .toList();

        entity.setItems(itemsEntity);

        return entity;
    }

    private OrderItemEntity toEntityItem(OrderItem item, Session session) {
        BookModel bookRef = session.getReference(BookModel.class, item.getBookIsbn());

        return new OrderItemEntity(item.getBookId(), bookRef);
    }
}
