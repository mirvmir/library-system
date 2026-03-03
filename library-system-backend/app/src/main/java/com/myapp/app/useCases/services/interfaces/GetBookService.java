package com.myapp.app.useCases.services.interfaces;

import com.myapp.app.useCases.services.inputs.GetBookInput;
import com.myapp.app.useCases.services.outputs.GetBooksOutput;

public interface GetBookService {
    GetBooksOutput execute(GetBookInput params);
}
