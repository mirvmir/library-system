package com.myapp.app.useCases.services.inputs;

import java.util.List;

public record CreateOrderInput(Long customerId, List<String> listIsbn) {
}
