package nz.co.noirland.noirstore;

public class ExpPriceCalculator implements PriceCalculator {

    private double a;
    private double b;
    private double c;

    private double min;
    private double max;

    public ExpPriceCalculator(double max, double min, double grad) {
        this.a = max - min;
        this.b = -1.0 / grad;
        this.c = min;

        this.max = max;
        this.min = min;
    }

    public double getPrice(int amount) {
        return a * Math.exp(b * amount) + c; // y = ae^(bx) + c
    }

    public double getMinPrice() {
        return min;
    }

    public double getMaxPrice() {
        return max;
    }
}
