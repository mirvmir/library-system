package com.myapp.app.useCases.adapter.infastructure.interfaces;

import java.util.function.Supplier;

public interface SessionManager {
    <T> T inTransaction(Supplier<T> work, String txName);
}
