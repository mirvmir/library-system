package com.myapp.app.controllers.requests;

import java.math.BigDecimal;

public record BookModelRq(String isbn, String title, String author, BigDecimal price) {
}
