package nz.co.noirland.noirstore;

public class PriceRange {

    private int lower;
    private int upper;
    private double buy;
    private double sell;

    public PriceRange(int lower, int upper, double buy, double sell) {
        this.lower = lower;
        this.upper = upper;
        this.buy = buy;
        this.sell = sell;
    }

    public boolean isInRange(double val) {
        return val >= lower && val <= upper;
    }

    public double getBuyPrice() {
        return buy;
    }

    public double getSellPrice() {
        return sell;
    }

    public int getLower() {
        return lower;
    }

    public int getUpper() {
        return upper;
    }
}
