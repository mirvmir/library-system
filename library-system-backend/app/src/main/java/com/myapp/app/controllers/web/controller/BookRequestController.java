package com.myapp.app.controllers.web.controller;

import com.myapp.app.controllers.requests.GetBookRequestRq;
import com.myapp.app.useCases.services.inputs.GetBookRequestInput;
import com.myapp.app.useCases.services.interfaces.GetBookRequestService;
import com.myapp.app.useCases.services.outputs.GetBookRequestsOutput;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/requests")
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
