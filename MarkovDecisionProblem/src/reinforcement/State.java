package reinforcement;

import mdp.Field;

public class State {
    private int x;
    private int y;
    private Field field;

    public State(int x, int y, Field field){
        this.x = x;
        this.y = y;
        this.field = field;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public Field getField(){
        return field;
    }
}
