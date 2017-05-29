package models;


import javafx.beans.binding.Bindings;
import javafx.beans.property.*;

public class SaleLine {
    private IntegerProperty id;
    private ObjectProperty<Article> article;
    private Sale sale;
    private FloatProperty discount;
    private IntegerProperty quantity;
    private FloatProperty total;
    private BooleanProperty checked;

    public SaleLine(int id, Article article, Sale sale, float discount, int quantity) {
        this.id = new SimpleIntegerProperty(id);
        this.article = new SimpleObjectProperty<>(article);
        this.sale = sale;
        this.discount = new SimpleFloatProperty(discount);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.checked = new SimpleBooleanProperty(false);

        this.total = new SimpleFloatProperty();
        this.total.bind(Bindings.multiply(Bindings.subtract(Bindings.selectFloat(this.article, "price"), Bindings.multiply(Bindings.selectFloat(this.article, "price"), this.discount)), this.quantity));
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
        return article.get();
    }

    public ObjectProperty<Article> articleProperty() {
        return article;
    }

    public void setArticle(Article article) {
        this.article.set(article);
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

    public float getTotal() {
        return total.get();
    }

    public FloatProperty totalProperty() {
        return total;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof SaleLine && (this.id.get() == ((SaleLine) object).getId() && this.sale.getId() == ((SaleLine) object).getSale().getId());
    }
}
