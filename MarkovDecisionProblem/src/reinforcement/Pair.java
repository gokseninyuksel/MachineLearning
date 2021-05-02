package reinforcement;

import javax.swing.*;

public class Pair<K,T>{
    K k;
    T t;
    public Pair(K k, T t){
        this.k = k;
        this.t = t;
    }
    public K getK(){
        return k;
    }

    public T getT() {
        return t;
    }
}
