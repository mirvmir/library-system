package com.myapp.app.ui.console.menu;

import com.myapp.app.controllers.console.IAction;
import com.myapp.app.ui.console.requestBuilders.RequestBuilder;
import com.myapp.app.ui.console.requestBuilders.context.SystemContext;

public class MenuItem<Rq> {
    private final String title;
    private final Menu nextMenu;
    private final boolean exitsProgram;
    private IAction<Rq> action;
    private RequestBuilder<Rq> requestBuilder;
    private SystemContext context;


    public MenuItem(String title,
                    IAction<Rq> action,
                    RequestBuilder<Rq> requestBuilder,
                    SystemContext context,
                    Menu nextMenu,
                    boolean exitsProgram) {
        this.title = title;
        this.action = action;
        this.requestBuilder = requestBuilder;
        this.context = context;
        this.nextMenu = nextMenu;
        this.exitsProgram = exitsProgram;
    }

    public MenuItem(String title,
                    IAction<Rq> action,
                    RequestBuilder<Rq> requestBuilder,
                    SystemContext context,
                    Menu nextMenu) {
        this(title, action, requestBuilder, context, nextMenu, false);
    }

    public String getTitle() {
        return this.title;
    }

    public IAction<Rq> getAction() {
        return action;
    }

    public Menu getNextMenu() {
        return this.nextMenu;
    }

    public boolean isExitsProgram() {
        return exitsProgram;
    }

    public RequestBuilder<Rq> getRequestBuilder() {
        return requestBuilder;
    }

    public SystemContext getContext() {
        return context;
    }
}
