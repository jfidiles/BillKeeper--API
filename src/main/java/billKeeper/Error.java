package billKeeper;

import java.util.*;

public class Error {
    String error;
    List<String> errors;
    public Error(){}

    public Error(String error){
        this.error = error;
    }

    public Error(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }

    public String getError() {
        return error;
    }
}
