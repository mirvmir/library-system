package com.myapp.app.useCases.services.interfaces;

import com.myapp.app.useCases.services.inputs.WriteOffBookInput;
import com.myapp.app.useCases.services.outputs.WriteOffBookOutput;

public interface WriteOffBookService {
    WriteOffBookOutput execute(WriteOffBookInput params);
}
