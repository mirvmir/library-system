package com.myapp.app.useCases.services.outputs;

import java.util.List;

public record GetBooksOutput(List<GetBookOutput> books) {
}
