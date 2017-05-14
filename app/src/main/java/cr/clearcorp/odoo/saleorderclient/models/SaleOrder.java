package cr.clearcorp.odoo.saleorderclient.models;

import java.util.ArrayList;

import cr.clearcorp.odoo.saleorderclient.controllers.PricelistController;

public class SaleOrder {

    protected Customer customer;
    protected Pricelist pricelist;
    protected Warehouse warehouse;
    protected ArrayList<SaleOrderLine> lines;

    public SaleOrder(){
        this.lines = new ArrayList<>();
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Pricelist getPricelist() {
        return pricelist;
    }

    public void setPricelist(Pricelist pricelist) {
        this.pricelist = pricelist;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public ArrayList<SaleOrderLine> getLines() {
        return lines;
    }

    public void setLines(ArrayList<SaleOrderLine> lines) {
        this.lines = lines;
    }

}
