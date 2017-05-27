package models;


import javafx.beans.property.*;

public class SaleLine {
    private IntegerProperty id;
    private Article article;
    private Sale sale;
    private FloatProperty discount;
    private IntegerProperty quantity;
    private BooleanProperty checked;

    public SaleLine(int id, Article article, Sale sale, float discount, int quantity) {
        this.id = new SimpleIntegerProperty(id);
        this.article = article;
        this.sale = sale;
        this.discount = new SimpleFloatProperty(discount);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.checked = new SimpleBooleanProperty(false);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public float getDiscount() {
        return discount.get();
    }

    public FloatProperty discountProperty() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount.set(discount);
    }

    public int getQuantity() {
        return quantity.get();
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public boolean isChecked() {
        return checked.get();
    }

    public BooleanProperty checkedProperty() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked.set(checked);
    }
}
