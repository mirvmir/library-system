package io.github.mirvmir.controllers.web.controller;

import io.github.mirvmir.controllers.web.requests.GetBookRequestRq;
import io.github.mirvmir.useCases.services.inputs.GetBookRequestInput;
import io.github.mirvmir.useCases.services.interfaces.GetBookRequestService;
import io.github.mirvmir.useCases.services.outputs.GetBookRequestsOutput;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/requests")
@PreAuthorize("hasRole('ADMIN')")
public class BookRequestController {

    private final GetBookRequestService getBookRequestService;

    public BookRequestController(GetBookRequestService getBookRequestService) {
        this.getBookRequestService = getBookRequestService;
    }

    @GetMapping
    public GetBookRequestsOutput getAll(GetBookRequestRq rq) {
        GetBookRequestInput getRqDto = new GetBookRequestInput(
                rq.type().toString(),
                rq.direction().toString(),
                rq.field().toString()
        );

        return getBookRequestService.execute(getRqDto);
    }
}
