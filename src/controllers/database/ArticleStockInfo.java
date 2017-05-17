package controllers.database;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import models.Article;
import models.Size;

public class ArticleStockInfo {
    private Size size;
    private Article article;
    private IntegerProperty stock;

    public ArticleStockInfo(Size size, Article article, int stock) {
        this.size = size;
        this.article = article;
        this.stock = new SimpleIntegerProperty(stock);
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
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
