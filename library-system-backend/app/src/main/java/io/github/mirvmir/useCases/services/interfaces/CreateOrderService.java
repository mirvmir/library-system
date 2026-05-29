package io.github.mirvmir.useCases.services.interfaces;

import io.github.mirvmir.useCases.services.inputs.CreateOrderInput;
import io.github.mirvmir.useCases.services.outputs.CreateOrderOutput;

public interface CreateOrderService {
    CreateOrderOutput execute(CreateOrderInput input);
}
