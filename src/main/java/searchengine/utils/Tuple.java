package searchengine.utils;

import lombok.Getter;

public class Tuple <V1, V2>{
    private final V1 obj1;
    private final V2 obj2;

    public Tuple(V1 obj1, V2 obj2) {
        this.obj1 = obj1;
        this.obj2 = obj2;
    }

    public V1 first() {
        return obj1;
    }

    public V2 second() {
        return obj2;
    }
}
