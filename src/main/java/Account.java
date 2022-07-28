import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
public class Account implements Comparable<Account> {
    private long balance;
    private final String accNumber;
    @Setter
    private boolean statusLock = false;

    public Account(long balance, String accNumber) {
        this.balance = balance;
        this.accNumber = accNumber;
    }

    public void addMoney(long money) {
        this.balance += money;
    }

    public void getMany(Account toAccount, long amount) {
        if (this.balance >= amount && amount > 0 && !this.accNumber.equals(toAccount.getAccNumber()))
            this.balance -= amount;
    }

    @Override
    public int compareTo(@NonNull Account acc) {
        return this.getAccNumber().compareTo(acc.getAccNumber());
    }
}