package org.tuomilabs.heart.heart;

import java.util.Comparator;
import java.util.List;

public class AlgorithmQuality implements Comparable<AlgorithmQuality> {
    private List<Double> coefficients;
    private int score;

    public List<Double> getCoefficients() {
        return coefficients;
    }

    public void setCoefficients(List<Double> coefficients) {
        this.coefficients = coefficients;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public AlgorithmQuality(List<Double> coefficients, int score) {

        this.coefficients = coefficients;
        this.score = score;
    }

    @Override
    public int compareTo(AlgorithmQuality o) {
        return 0 - (o.score - this.score);
    }
}
