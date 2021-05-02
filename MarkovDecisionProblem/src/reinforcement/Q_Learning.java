package reinforcement;

import mdp.Action;
import mdp.Field;
import mdp.MarkovDecisionProblem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;;
import java.util.Random;

public class Q_Learning {
    private MarkovDecisionProblem mdp;
    private double actionPerform,actionSlide,actionBack;
    private Map<Pairs,Double> Q_Val;
    private Map<Integer,Double> Graph;

    private int MAX_STEPS = 100;
    private int alpha;
    public Q_Learning(MarkovDecisionProblem mdp){
        this.mdp = mdp;
        this.actionPerform = mdp.pPerform;
        this.actionSlide = mdp.pSidestep;
        this.actionBack = mdp.pBackstep;
        mdp.setShowProgress(false);
        Q_Val = new HashMap<>();
        Graph = new HashMap<>();
    }
    public Map<Pairs,Double> learn(double alpha,double epsilon, int epochs , double constant){
        initilize();
        int steps = MAX_STEPS;
        Random random = new Random(1);
        Integer numberOfSteps = 0;
        Double accumulatedReward = 0.0;
        for(int a = 0; a<epochs; a++){
            while (!mdp.isTerminated() && steps > 0){
                Action action = selectMove(mdp,epsilon, random.nextDouble(),Q_Val,random.nextInt(Action.values().length));
                Pairs old = new Pairs(mdp.getStateXPosition(),mdp.getStateYPostion(),action);
                mdp.performAction(action);
                numberOfSteps++;
                accumulatedReward+= mdp.getReward();
                double newQ = Q_Val.get(old) + alpha * (mdp.getReward() + constant *
                        (Double) max(Q_Val,mdp.getStateXPosition(),mdp.getStateYPostion()).getK() - Q_Val.get(old) );
                Q_Val.replace(old,newQ);
                Graph.put(numberOfSteps,accumulatedReward);
                steps--;
            }

            mdp.restart();
            steps = MAX_STEPS;
        }
        dump();
        ValueIteration.dump(policy(Q_Val));
        return Q_Val;
    }

    private Pair max(Map<Pairs, Double> q_val, int stateXPosition, int stateYPostion) {
        Double max = -100000.0;
        Action action = Action.UP;
            for(Pairs pair: q_val.keySet()){
                if(pair.getX() == stateXPosition && pair.getY() == stateYPostion){
                        if(q_val.get(pair) > max) {
                            max = q_val.get(pair);
                            action = pair.getAction();
                    }
                }
            }
    return new Pair(max,action);
    }

    private void dump() {
        for(Pairs pair: Q_Val.keySet()){
            Double found = Q_Val.get(pair);
            System.out.println(pair.toString() + " " + found);
        }
    }

    private Action selectMove(MarkovDecisionProblem mdp, double epsilon, double nextGaussian, Map<Pairs, Double> q_Val,int random) {
        Action [] actions = Action.values();
        int x = mdp.getStateXPosition();
        int y = mdp.getStateYPostion();
        if(epsilon > nextGaussian){
            Action best = (Action)max(q_Val,x,y).getT();
            return best;
        }
        else{
            Action rand = actions[random];
            return rand;
        }

    }


    private void initilize() {
        for(int a = 0; a<mdp.getWidth(); a++){
            for(int b = 0; b<mdp.getHeight(); b++){
                for(Action action: Action.values())
                       Q_Val.put(new Pairs(a,b,action),0.0);
            }
        }
    }
    private String[][] policy(Map<Pairs,Double> Qval){
        String [][] policy = new String[mdp.getWidth()][mdp.getHeight()];
        for(int a = 0; a<policy.length; a++) {
            for (int b = 0; b < policy[a].length; b++) {
                if (mdp.getField(a, b).equals(Field.OBSTACLE)) {
                    policy[a][b] = null;
                } else if (mdp.getField(a, b).equals(Field.REWARD)) {
                    policy[a][b] = String.valueOf(mdp.getPosReward());
                } else if (mdp.getField(a, b).equals(Field.NEGREWARD)) {
                    policy[a][b] = String.valueOf(mdp.getNegReward());
                } else {
                    Double maxVal = -10000.0;
                    Action action = null;
                    for (Pairs pairs : Qval.keySet()) {
                        if (pairs.getX() == a && pairs.getY() == b) {
                            if (Qval.get(pairs) > maxVal) {
                                maxVal = Qval.get(pairs);
                                action = pairs.getAction();
                            }

                        }
                    }
                    policy[a][b] = ValueIteration.write(action);

                }
            }
        }
        return policy;
    }

    public Map<Integer, Double> getGraph() {
        return Graph;
    }
}
