package io.github.mirvmir.useCases.services.interfaces;

import io.github.mirvmir.useCases.services.inputs.GetOrderInput;
import io.github.mirvmir.useCases.services.outputs.GetOrdersOutput;

public interface GetOrderForAdminService {
    GetOrdersOutput execute(GetOrderInput input);
}
