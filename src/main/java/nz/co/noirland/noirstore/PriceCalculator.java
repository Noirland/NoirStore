package nz.co.noirland.noirstore;

public interface PriceCalculator {

    public double getPrice(int amount);

    public double getMinPrice();

    public double getMaxPrice();

}
