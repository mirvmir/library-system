package com.myapp.app.useCases.services.outputs;

import java.util.List;

public record GetBookRequestsOutput(List<GetBookRequestOutput> bookRequests) {
}
