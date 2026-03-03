package com.myapp.app.ui.console.menu;

import com.myapp.app.ui.console.menu.content.MenuContent;
import com.myapp.app.ui.console.menu.hook.MenuHook;

import java.util.List;

public class Menu {
    private final String name;
    private final List<MenuItem> menuItems;

    private MenuContent content;
    private MenuHook beforeRender;

    public Menu(String name, List<MenuItem> menuItems) {
        this.name = name;
        this.menuItems = menuItems;
    }

    public String getName() {
        return this.name;
    }

    public List<MenuItem> getMenuItems() {
        return this.menuItems;
    }

    public MenuContent getContent() {
        return content;
    }

    public Menu setContent(MenuContent content) {
        this.content = content;
        return this;
    }

    public MenuHook getBeforeRender() {
        return beforeRender;
    }

    public Menu setBeforeRender(MenuHook beforeRender) {
        this.beforeRender = beforeRender;
        return this;
    }
}
