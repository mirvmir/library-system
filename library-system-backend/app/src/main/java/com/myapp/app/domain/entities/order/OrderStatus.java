package com.myapp.app.domain.entities.order;

public enum OrderStatus {
    NEW {
        @Override public boolean canTransitionTo(OrderStatus t) {
            return COMPLETED == t || CANCELLED == t;
        }
    },
    COMPLETED {
        @Override public boolean canTransitionTo(OrderStatus t) {
            return false;
        }
    },
    CANCELLED {
        @Override public boolean canTransitionTo(OrderStatus t) {
            return false;
        }
    };

    public abstract boolean canTransitionTo(OrderStatus target);
}