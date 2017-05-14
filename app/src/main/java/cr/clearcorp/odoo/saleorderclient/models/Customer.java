package cr.clearcorp.odoo.saleorderclient.models;

public class Customer {

    protected Integer id;
    protected String name;
    protected Integer paymentTermId;
    protected Pricelist pricelist;

    public Customer(Integer id, String name, Integer paymentTermId){
        this.id = id;
        this.name = name;
        this.pricelist = null;
        this.paymentTermId = paymentTermId;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPaymentTermId() {
        return paymentTermId;
    }

    public void setPaymentTermId(Integer paymentTermId) {
        this.paymentTermId = paymentTermId;
    }

    public Pricelist getPricelist() {
        return pricelist;
    }

    public void setPricelist(Pricelist pricelist) {
        this.pricelist = pricelist;
    }
}
