/**
 * Created by maloletniy on 18/02/15.
 */
class Item {
    String make;
    String model;
    String year;
    String url;
    String category;
    String engine;

    public Item(String make, String model, String year, String url) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.url = url;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    @Override
    public String toString() {
        return "Item{" +
                "make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", year='" + year + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
