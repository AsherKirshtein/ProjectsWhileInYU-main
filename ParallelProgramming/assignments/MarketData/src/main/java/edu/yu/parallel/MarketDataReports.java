package edu.yu.parallel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.print.attribute.standard.MediaSize.NA;


public class MarketDataReports {

    private final SymbolCache symbolCache;

    public MarketDataReports(SymbolCache symbolCache) {
        this.symbolCache = symbolCache;
    }


    /***
     * Generate a report for all the NASDAQ symbols for which data has been provided in
     * the resources/data directory.
     *
     * The report should be in CSV format and contain two columns:
     * 1) Symbol - (string) the symbol
     * 2) Count - (number) the number days on which the closing price for the given symbol
     *            was greater than the midpoint of the high and low for that day
     *
     *  The report should have a header row with columns names: symbol,count
     *
     *  The report should be ordered according the count in descending order. If two symbols
     *  have the same count, then they should be ordered alphabetically according to the symbol.
     *
     *  e.g.
     * Symbol,Count
     * MSFT,844
     * VRSK,843
     * ADBE,841
     * INTU,840
     * ...
     *
     * @param outputStream the OutputStream to save the report to
     */
    public void generateCloseAboveMidPriceReport(OutputStream outputStream)
    {
        try
        {
            outputStream.write("Symbol,Count\n".getBytes());
            SymbolReader reader = new SymbolReader("nasdaq");
            SymbolCache cache = new SymbolCache(reader);
            Stream<SymbolData> NASDAQ_100Data = cache.stream();
            List<File> files = new ArrayList<File>();
            
            NASDAQ_100Data.forEach(symbol -> 
            {
               File file = new File("src/main/resources/data/" + symbol.symbol() + ".csv");
               if(!file.exists())
               {
                  System.out.println("File does not exist: " + file.getName());
               }
               else
               {
                files.add(file);
               }
            });
            List<CSV> list = new ArrayList<>();
            files.parallelStream()
            .forEach(file -> 
            {
                String name = file.getName();
                dataReader reader2 = new dataReader(name);
                dataCache cache2 = new dataCache(reader2);
                Stream<otherData> data = cache2.stream();
                name = name.replace(".csv", "");
                CSV csv = new CSV(name);
                data.forEach(symbol -> 
                {
                    if(symbol.close() > (symbol.high() + symbol.low()) / 2)
                    {
                        csv.increment();
                    } 
                });
                list.add(csv);
            });
            Collections.sort(list);
            for(CSV csv2 : list)
            {
                outputStream.write(csv2.toString().getBytes());
            }
        } 
        catch (IOException e)
        {
            
            e.printStackTrace();
        }        
    }

    /***
     * Generate a market data report in the same format as the market data files that you are using
     * as input.  The report should be a composite of all the symbols that have been tagged as being
     * part of the NASDAQ 100 index -- i.e. those symbols that have a participation weights greater
     * than 0.
     *
     * When aggregating your numbers, you must apply the participation weights to scale the values
     * for each symbol. Apply the weight to all price and volume fields.
     *
     * e.g.
     * Date,High,Low,Open,Close,Volume,Adj Close
     * 2017-01-03,7624.927863,7474.788626,7547.201970,7566.424213,3475185756.440001,7156.133505
     * 2017-01-04,7657.954486,7541.022483,7578.069394,7608.906997,3034456813.144000,7198.486098
     * 2017-01-05,7690.347497,7566.646364,7606.050444,7640.882928,3152182093.596000,7230.710732

     * ...
     * 2022-11-29,19910.155576,19482.617342,19776.978908,19660.611746,2435208268.800000,19660.611746
     * 2022-11-30,20614.196690,19543.308255,19704.378422,20550.018541,3976500312.100000,20550.018541
     *
     * @param outputStream the OutputStream to save the report to
     */
    public void generateNASDAQ100CompositeReport(OutputStream outputStream)
    {
        try
        {
            outputStream.write("Date,High,Low,Open,Close,Volume,Adj Close\n".getBytes());
           
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        List<NASDAQ_100> nas = new ArrayList<>();  
        this.symbolCache.stream().filter(symbol -> symbol.nsdq100Weight() > 0).forEach(symbol -> 
        {
            File file = new File("src/main/resources/data/" + symbol.symbol() + ".csv");
            if(!file.exists())
            {
                System.out.println("File does not exist: " + file.getName());
            }
            else
            {
                dataReader reader = new dataReader(file.getName());
                dataCache cache = new dataCache(reader);
                cache.stream().forEach(data -> 
                {
                    NASDAQ_100 n = new NASDAQ_100(data.date(), data.high() * symbol.nsdq100Weight(), data.low() * symbol.nsdq100Weight(), data.open() * symbol.nsdq100Weight(), data.close() * symbol.nsdq100Weight(), data.volume() * symbol.nsdq100Weight(), data.adjClose() * symbol.nsdq100Weight());
                    nas.add(n);
                });
            }
        });
        Map<String, NASDAQ_100> map = new HashMap<>();
        nas.parallelStream().forEach(n ->
        {
                if(map.get(n.getDate()) == null)
                {
                    map.put(n.date, n);
                }
                else
                {
                    NASDAQ_100 n2 = map.get(n.date);
                    if(n2 != null)
                    {
                        n2.high += n.high;
                        n2.low += n.low;
                        n2.open += n.open;
                        n2.close += n.close;
                        n2.volume += n.volume;
                        n2.adjClose += n.adjClose;
                    }
                }
        });
        List<NASDAQ_100> list = map.values().stream().collect(Collectors.toList());
        Collections.sort(list);
        list.stream().forEach(n ->
        {
            try
            {
                outputStream.write(n.toString().getBytes());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        });

    }

    private class NASDAQ_100 implements Comparable<NASDAQ_100>
    {
        String date;
        short year;
        short month;
        short day;
        double high;
        double low;
        double open;
        double close;
        double volume;
        double adjClose;

        NASDAQ_100(String date, double high, double low, double open, double close, double volume, double adjClose)
        {
            this.date = date;
            String[] d = date.split("-");
            this.year = Short.parseShort(d[0]);
            this.month = Short.parseShort(d[1]);
            this.day = Short.parseShort(d[2]);
            this.high = high;
            this.low = low;
            this.open = open;
            this.close = close;
            this.volume = volume;
            this.adjClose = adjClose;
        }

        public String getDate() {
            return date;
        }

       public double getAdjClose() {
           return adjClose;
       }

       public double getClose() {
           return close;
       }

         public double getHigh() {
              return high;}

         public double getLow() {
             return low;
         }

            public double getOpen() {
                return open;
            }
              
        @Override
        public int compareTo(NASDAQ_100 arg0)
        {
            if(this.year > arg0.year)
            {
                return 1;
            }
            else if(this.year < arg0.year)
            {
                return -1;
            }
            else
            {
                if(this.month > arg0.month)
                {
                    return 1;
                }
                else if(this.month < arg0.month)
                {
                    return -1;
                }
                else
                {
                    if(this.day > arg0.day)
                    {
                        return 1;
                    }
                    else if(this.day < arg0.day)
                    {
                        return -1;
                    }
                    else
                    {
                        return 0;
                    }
                }
            }
        }

        @Override
        public String toString() {
            // TODO Auto-generated method stub
            return "[" + this.date + "," + this.high + "," + this.low + "," + this.open + "," + this.close + "," + this.volume + "," + this.adjClose + "]\n";
        }
    }

    private class CSV implements Comparable<CSV>
    {
        private String symbol;
        private int count;

        CSV(String symbol)
        {
            this.symbol = symbol;
            this.count = 0;
        }

        public void increment()
        {
            this.count++;
        }

        public String getSymbol()
        {
            return symbol;
        }

        public int getCount()
        {
            return count;
        }

        @Override
        public String toString()
        {
            return this.symbol + "," + this.count + "\n";
        }

        @Override
        public int compareTo(CSV o)
        {
            if (this.count > o.count)
            {
                return -1;
            }
            else if (this.count < o.count)
            {
                return 1;
            }
            else if(this.count == o.count)
            {
                if(this.symbol.compareTo(o.symbol) > 0)
                {
                    return 1;
                }
                else if(this.symbol.compareTo(o.symbol) < 0)
                {
                    return -1;
                }
                else
                {
                    return 0;
                }
            }
            else
            {
                return this.symbol.compareTo(o.symbol);
            }
        }
    }
}
