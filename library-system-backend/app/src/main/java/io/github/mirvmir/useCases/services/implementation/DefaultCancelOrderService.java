package io.github.mirvmir.useCases.services.implementation;

import io.github.mirvmir.domain.entities.order.Order;
import io.github.mirvmir.domain.entities.payment.Payment;
import io.github.mirvmir.domain.entities.payment.PaymentStatus;
import io.github.mirvmir.exception.business.OrderNotFoundException;
import io.github.mirvmir.useCases.adapter.repository.interfaces.OrderRepository;
import io.github.mirvmir.useCases.adapter.repository.interfaces.PaymentRepository;
import io.github.mirvmir.useCases.services.interfaces.CancelOrderService;
import io.github.mirvmir.useCases.services.inputs.CancelOrderInput;
import io.github.mirvmir.useCases.services.outputs.CancelOrderOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefaultCancelOrderService implements CancelOrderService {

    private final OrderRepository orderRepo;
    private final PaymentRepository paymentRepo;

    public DefaultCancelOrderService(OrderRepository orderRepo,
                                     PaymentRepository paymentRepo) {
        this.orderRepo = orderRepo;
        this.paymentRepo = paymentRepo;
    }

    @Override
    @Transactional
    public CancelOrderOutput execute(CancelOrderInput input) {
        Order order = orderRepo.findByIdForUpdate(input.orderId());

        if (order == null) {
            throw new OrderNotFoundException(
                    "Order with ID " + input.orderId() + " not found."
            );
        }

        Payment payment = paymentRepo.findByOrderId(order.getId());

        if (order.isPayed()) {
            order.markRefundRequired();
            orderRepo.update(order);
            return new CancelOrderOutput();
        }

        if (payment != null && PaymentStatus.CREATED == payment.getStatus()) {
            payment.cancel();
            paymentRepo.save(payment);
        }

        order.cancel();
        orderRepo.update(order);

        return new CancelOrderOutput();
    }
}