package com.myapp.app.useCases.services.implementation;

import com.myapp.app.domain.entities.order.Order;
import com.myapp.app.domain.entities.request.BookRequest;
import com.myapp.app.useCases.adapter.repository.interfaces.BookModelRepository;
import com.myapp.app.useCases.adapter.repository.interfaces.BookRequestRepository;
import com.myapp.app.useCases.adapter.repository.interfaces.OrderRepository;
import com.myapp.app.useCases.services.interfaces.CreateOrderService;
import com.myapp.app.useCases.services.inputs.CreateOrderInput;
import com.myapp.app.useCases.services.outputs.CreateOrderOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class DefaultCreateOrderService implements CreateOrderService {

    private final OrderRepository orderRepo;
    private final BookModelRepository modelRepo;
    private final BookRequestRepository requestRepo;

    public DefaultCreateOrderService(OrderRepository orderRepo,
                                     BookModelRepository modelRepo,
                                     BookRequestRepository requestRepo) {
        this.orderRepo = orderRepo;
        this.modelRepo = modelRepo;
        this.requestRepo = requestRepo;
    }

    @Override
    @Transactional
    public CreateOrderOutput execute(CreateOrderInput input) {
        Order newOrder = orderRepo.save(
                Order.createNew(
                        input.customerId(),
                        input.listIsbn()
                )
        );

        Set<String> unavailable = modelRepo.findUnavailableIsbns(input.listIsbn());

        List<BookRequest> requests = input.listIsbn().stream()
                .filter(unavailable::contains)
                .map(isbn -> BookRequest.createNew(isbn, input.customerId()))
                .toList();

        requestRepo.saveAll(requests);

        return new CreateOrderOutput(newOrder.getId());
    }
}
