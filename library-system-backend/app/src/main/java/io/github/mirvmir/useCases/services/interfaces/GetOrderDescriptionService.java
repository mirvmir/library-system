package io.github.mirvmir.useCases.services.interfaces;

import io.github.mirvmir.useCases.services.inputs.GetOrderDescriptionInput;
import io.github.mirvmir.useCases.services.outputs.OrderDescriptionOutput;

public interface GetOrderDescriptionService {
    OrderDescriptionOutput execute(GetOrderDescriptionInput input);
}
