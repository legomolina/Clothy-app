package models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import models.Article;
import models.Size;

public class ArticleStockInfo {
    private Size size;
    private IntegerProperty stock;

    public ArticleStockInfo(Size size, int stock) {
        this.size = size;
        this.stock = new SimpleIntegerProperty(stock);
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public int getStock() {
        return stock.get();
    }

    public IntegerProperty stockProperty() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock.set(stock);
    }
}
