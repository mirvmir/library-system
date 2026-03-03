package com.myapp.app.ui.console.consoleViewModels.consoleEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderViewModel {
    private Long id;
    private Long customerId;
    private String status;
    private LocalDateTime completionDate;
    private BigDecimal totalPrice;
    private List<String> isbns;

    public OrderViewModel(Long id, Long customerId,
                          String status, LocalDateTime completionDate,
                          BigDecimal totalPrice, List<String> isbns) {
        this.id = id;
        this.customerId = customerId;
        this.status = status;
        this.completionDate = completionDate;
        this.totalPrice = totalPrice;
        this.isbns = isbns;
    }


    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCompletionDate() {
        return completionDate;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public List<String> getIsbns() {
        return isbns;
    }
}
