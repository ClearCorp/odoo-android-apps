package cr.clearcorp.odoo.saleorderclient.models;

public class SaleOrderLine {

    protected Double quantity;
    protected Product product;
    protected UnitofMeasure uom;
    protected Double price;

    public SaleOrderLine(Double quantity, Product product, UnitofMeasure uom, Double price) {
        this.quantity = quantity;
        this.product = product;
        this.uom = uom;
        this.price = price;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public UnitofMeasure getUom() {
        return uom;
    }

    public void setUom(UnitofMeasure uom) {
        this.uom = uom;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

}
