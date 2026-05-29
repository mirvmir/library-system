package io.github.mirvmir.useCases.services.interfaces;

import io.github.mirvmir.useCases.services.inputs.WriteOffBookInput;
import io.github.mirvmir.useCases.services.outputs.WriteOffBookOutput;

public interface WriteOffBookService {
    WriteOffBookOutput execute(WriteOffBookInput input);
}
