package edu.yu.parallel;

public class ITradingAccountImpl implements ITradingAccount 
{
    int cash;
    int shares;
    public ITradingAccountImpl() 
    {
        this.cash = 1000000;
        this.shares = 0;
    }

    @Override
    public synchronized double getCashBalance() 
    {
        return cash;
    }

    @Override
    public synchronized double getPositionBalance() 
    {
        return shares;
    }

    @Override
    public synchronized void Buy(double amount) 
    {
        cash -= amount;
        shares += amount;
    }

    @Override
    public synchronized void Sell(double amount) 
    {
        cash += amount;
        shares -= amount;
    }

    @Override
    public synchronized String getBalanceString()
    {
        return "Cash=" + cash + ",Positions=" + shares +",Total=" + (cash+shares);
    }
    
}