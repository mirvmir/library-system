package io.github.mirvmir.useCases.services.interfaces;

import io.github.mirvmir.useCases.services.inputs.CompleteOrderInput;
import io.github.mirvmir.useCases.services.outputs.CompleteOrderOutput;

public interface CompleteOrderService {
    CompleteOrderOutput execute(CompleteOrderInput params);
}
