package cr.clearcorp.odoo.saleorderclient.models;

import java.util.ArrayList;

public class Product {

    protected Integer id;
    protected String name;
    protected String code;
    protected String imageMedium;
    protected UnitofMeasure uom;
    protected ArrayList<Integer> taxes;


    public Product(Integer id, String name, String code, String imageMedium, UnitofMeasure uom){
        this.id = id;
        this.name = name;
        this.code = code;
        this.imageMedium = imageMedium;
        this.uom = uom;
        this.taxes = new ArrayList<>();
    }

    @Override
    public String toString() {
        if (!this.code.isEmpty()) {
            return "[" + this.code + "] " + this.name;
        }
        else {
            return this.name;
        }
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

    public String getImageMedium() {
        return imageMedium;
    }

    public void setImageMedium(String imageMedium) {
        this.imageMedium = imageMedium;
    }

    public UnitofMeasure getUom() {
        return uom;
    }

    public void setUom(UnitofMeasure uom) {
        this.uom = uom;
    }

    public ArrayList<Integer> getTaxes() {
        return taxes;
    }

    public void setTaxes(ArrayList<Integer> taxes) {
        this.taxes = taxes;
    }

    public Double computePrice(Pricelist pricelist){
        return 0.0;
    }
}
