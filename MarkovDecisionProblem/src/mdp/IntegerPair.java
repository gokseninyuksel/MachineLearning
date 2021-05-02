package mdp;

public class IntegerPair {
    private int first;
    private int second;
    public IntegerPair( int first, int second ){
        this.first = first;
        this.second = second;
    }

    public int getFirst() {
        return first;
    }
    public int getSecond(){
        return second;
    }

    @Override
    public String toString() {
        return "IntegerPair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
