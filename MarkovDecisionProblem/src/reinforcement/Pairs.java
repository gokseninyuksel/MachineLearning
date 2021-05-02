package reinforcement;

import mdp.Action;
import mdp.Field;

import java.util.Objects;

public class Pairs {
    private int x;
    private int y;
    private Action action;

    public Pairs(int x, int y, Action action){
        this.x = x;
        this.y = y;
        this.action = action;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "(" + x + ")" + " (" + y + ")" + " " + action.toString();
    }

    public int getX() {
        return x;
    }


    public Action getAction() {
        return action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pairs pairs = (Pairs) o;
        return x == pairs.x && y == pairs.y  && action == pairs.action;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, action);
    }
}
