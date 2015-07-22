# NoirStore #

NoirStore allows for a dynamic and configurable economy for selling and buying items. The prices of items will adjust depending on the demand (ie stock) of the item.

This encourages players to sell items when stocks are low, and to buy when stocks are high.

* Completely configurable price scaling using exponential curves
* Signs are globally connected - all signs contain the same stock of their item
* Easy to use sign interface
* Links in with any VaultAPI economy plugin
* Supports item data such as wool colour (does not support meta data such as names or enchants)

## Creating NoirStore signs ##

1. Create a new sign with just `[NoirStore]` on the first line.
2. Take an item stack of the item you want to sell, including the stack size interval you want to sell at
3. Right-click sign with the item

The sign should update to show the buy/sell price of the item, colour the title

## Using signs ##

The signs layout is shown below:

```
[NoirStore] # Shows red when out of stock, and green when in stock
B: $10      # Price to buy one "unit" of the item
S: $7       # Price to sell one "unit" of the item
16 : 256    # The unit size, and the amount of items in stock
```

**Buying** - To buy, simply right click the sign, and you will be given a unit of the item, with funds automatically taken from your balance
**Selling** - To sell, left click the sign with the item in hand. It will consume one unit of the item with each click

## Configuration ##

### Item configurations ###

Each item that is to be sold must have a config file associated with it. While this is tedious to intially set up, it allows far more flexibility as to what should be sold, and the price curve.

Files must be located in `items` within the NoirStore config directory, and can be freely named.

Material types are defined by their bukkits names, available [here](//hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html).

Data types are either a integer value, or a datatype from bukkit all the "types" [here](https://hub.spigotmc.org/javadocs/bukkit/).

Curves are of the shape `y = (max - min) * e ^ (-1 / grad * amount) + min`, which is an exponential decay curve

```
cobblestone.yml:
material: COBBLESTONE # The material of the item
price:
  min: 0.01 # Minimum item price
  max: 0.035 # Maximum/initial item price
  gradient: 1250.0 # Exponential curve that the item follows, mostly arbitrary

wool-pink.yml:
data: PINK # damage/type of the item
material: WOOL
price:
  min: 0.2
  max: 1.8
  gradient: 1500.0

```

### Main config file ###

```
noirstore:
    sellpercent: 5 # percentage to be taken off sell prices
    tradedelay: 500 # How long to force a delay between sign uses, in milliseconds (1s = 1000ms); if -1 no delay will be applied
    mysql: # Database details
        username: foo
        password: bar
        host: localhost
        port: 3306
        database: noirstore
        prefix: store
```