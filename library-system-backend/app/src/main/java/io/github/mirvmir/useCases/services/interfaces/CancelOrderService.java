package io.github.mirvmir.useCases.services.interfaces;

import io.github.mirvmir.useCases.services.inputs.CancelOrderInput;
import io.github.mirvmir.useCases.services.outputs.CancelOrderOutput;

public interface CancelOrderService {
    CancelOrderOutput execute(CancelOrderInput params);
}
