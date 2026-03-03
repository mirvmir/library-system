package com.myapp.app.domain.entities.customer;


import javax.persistence.*;

@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public int hashCode() {
        return Long.hashCode(id);
    }

    public static Customer createNew() {
        return new Customer();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (null == o || getClass() != o.getClass()) {
            return false;
        }

        Customer other = (Customer) o;
        return id != null && id.equals(other.id);
    }

    public Long getId() {
        return this.id;
    }
}
