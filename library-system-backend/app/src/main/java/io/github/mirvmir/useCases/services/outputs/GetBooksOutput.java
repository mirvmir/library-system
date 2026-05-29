package io.github.mirvmir.useCases.services.outputs;

import java.util.List;

public record GetBooksOutput(List<GetBookOutput> books) {
}
