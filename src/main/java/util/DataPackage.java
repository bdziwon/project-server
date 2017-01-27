package util;

import java.io.Serializable;

public class DataPackage implements Serializable {
    private String details;
    private Object object;

    public DataPackage(String details, Object object) {
        this.details = details;
        this.object  = object;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
