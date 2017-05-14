package cr.clearcorp.odoo.saleorderclient.models;

public class Warehouse {

    protected Integer id;
    protected String name;

    public Warehouse(Integer id, String name){
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
