package com.myapp.app.ui.console.consoleViewModels.itemViews;

import java.util.List;

public interface IItemView<T> {
    List<String[]> viewList(List<T> items);
    StringBuilder viewItem(T item);
}
