/**
 * Created by maloletniy on 19/04/15.
 */
public class Element implements Comparable {

    private String make;
    private String model;
    private String category;

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Element{");
        sb.append("make='").append(make).append('\'');
        sb.append(", model='").append(model).append('\'');
        sb.append(", category='").append(category).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public Element(String make, String model, String category) {
        this.make = make;
        this.model = model;
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Element element = (Element) o;

        if (make != null ? !make.equals(element.make) : element.make != null) return false;
        if (model != null ? !model.equals(element.model) : element.model != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = make != null ? make.hashCode() : 0;
        result = 31 * result + (model != null ? model.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Element) {
            if (make.equals(((Element) o).getMake())) {
                return model.compareTo(((Element) o).getModel());
            } else {
                return make.compareTo(((Element) o).getMake());
            }
        } else {
            return 0;
        }
    }
}


