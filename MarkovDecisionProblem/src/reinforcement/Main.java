package reinforcement;

import mdp.Field;
import mdp.MarkovDecisionProblem;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {

        MarkovDecisionProblem mp = new MarkovDecisionProblem();
        mp.setProbsStep(0.8,0.2,0,0);
        mp.setNoReward(-0.04);
        ValueIteration val = new ValueIteration(mp);
        val.valueIteration(1,1000);
        Q_Learning qLearning = new Q_Learning(mp);
        qLearning.learn(0.2,0.35,100,1);

    }
}
