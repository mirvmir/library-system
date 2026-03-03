package com.myapp.app.useCases.services.outputs;

import java.util.List;

public record GetOrdersOutput(List<GetOrderOutput> orders) {
}
