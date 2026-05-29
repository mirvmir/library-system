package io.github.mirvmir.useCases.services.outputs;

import java.util.List;

public record GetBookRequestsOutput(List<GetBookRequestOutput> bookRequests) {
}
