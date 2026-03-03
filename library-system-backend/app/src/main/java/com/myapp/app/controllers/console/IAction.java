package com.myapp.app.controllers.console;

import com.myapp.app.exception.AppException;

public interface IAction<Rq> {
    void execute(Rq request) throws AppException;
}
