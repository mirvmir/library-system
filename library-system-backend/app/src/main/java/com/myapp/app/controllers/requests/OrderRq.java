package com.myapp.app.controllers.requests;

import java.util.List;

public record OrderRq(List<String> listIsbn, Long customerId) {
}
