package nz.co.noirland.noirstore;

import java.util.ArrayList;
import java.util.List;

public class ApproxPriceCalculator implements PriceCalculator {

    private ArrayList<PriceRange> prices = new ArrayList<PriceRange>();
    private PriceRange minPrice;
    private PriceRange maxPrice;


    public ApproxPriceCalculator(List<PriceRange> prices) {
        this.prices.addAll(prices);

        minPrice = prices.get(0);
        maxPrice = prices.get(0);

        for(PriceRange price : prices) {
            if(price.getMinAmount() < minPrice.getMinAmount()) {
                minPrice = price;
            }
            if(price.getMaxAmount() > maxPrice.getMaxAmount()) {
                maxPrice = price;
            }
        }
    }

    public double getPrice(int amount) {
        if(amount < minPrice.getMinAmount()) {
            return minPrice.calculatePrice(minPrice.getMinAmount());
        }
        if(amount > maxPrice.getMaxAmount()) {
            return maxPrice.calculatePrice(maxPrice.getMaxAmount());
        }

        for(PriceRange pRange : prices) {
            if(pRange.canCalculate(amount)) {
                return pRange.calculatePrice(amount);
            }
        }
        return 0;
    }

    public double getMinPrice() {
        return minPrice.calculatePrice(minPrice.getMinAmount());
    }

    public double getMaxPrice() {
        return maxPrice.calculatePrice(maxPrice.getMaxAmount());
    }
}
