package nz.co.noirland.noirstore;

public class PriceRange {

    private int maxAmount;
    private int minAmount;
    private double gradient;
    private long basePrice;

    public PriceRange(int lowerAmount, int upperAmount, long lowerPrice, long upperPrice) {


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

    public long calculatePrice(int amount) {
        int relAmount = amount - minAmount;

        return (long) (gradient * relAmount) + basePrice;

    }


}
