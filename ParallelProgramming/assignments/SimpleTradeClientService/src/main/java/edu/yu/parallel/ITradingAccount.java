package edu.yu.parallel;

public interface ITradingAccount 
{
    double getCashBalance();
    double getPositionBalance();
    void Buy(double amount);
    void Sell(double amount);
    /**
     * @return a string containing the cash and position balances
     * in the following format:
     * "Cash=%.2f,Position=%.2f,Total=%.2f"
     */
    String getBalanceString();
}
