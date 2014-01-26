package nz.co.noirland.noirstore;

public class PriceRange {

    private int maxAmount;
    private int minAmount;
    private double gradient;
    private double basePrice;

    public PriceRange(int lowerAmount, int upperAmount, double lowerPrice, double upperPrice) {


        basePrice = Math.max(lowerPrice, upperPrice);
        minAmount = lowerAmount;
        maxAmount = upperAmount;

        // 'rise over run'
        gradient = (upperPrice - lowerPrice) / (upperAmount - lowerAmount);

    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public boolean canCalculate(int amount) {
        return amount <= maxAmount && amount >= minAmount;
    }

    public double calculatePrice(int amount) {
        int relAmount = amount - minAmount;

        return (gradient * relAmount) + basePrice;

    }


}
