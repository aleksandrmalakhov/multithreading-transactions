import lombok.Getter;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.*;

public class Bank {
    private final Map<String, Account> accounts;
    private final Random random = new Random();
    @Getter
    private final List<StringBuilder> transferList;
    @Getter
    private final List<StringBuilder> securityList;

    public Bank() {
        accounts = new HashMap<>();
        transferList = Collections.synchronizedList(new LinkedList<>());
        securityList = Collections.synchronizedList(new LinkedList<>());
    }

    public void addAccount(String accountName, Account account) {
        accounts.put(accountName, account);
    }

    public void fraudSecurity(String fromAccountNum, String toAccountNum, long amount)
            throws InterruptedException {
        Account accountFrom = getAccount(fromAccountNum);
        Account accountTo = getAccount(toAccountNum);

        synchronized (accountFrom.compareTo(accountTo) > 0 ? accountFrom : accountTo) {
            synchronized (accountFrom.compareTo(accountTo) > 0 ? accountTo : accountFrom) {
                Thread.sleep(1000);

                var result = random.nextBoolean();

                if (result) {
                    accountFrom.setStatusLock(true);
                    accountTo.setStatusLock(true);

                    StringBuilder string = new StringBuilder();

                    string.append(Thread.currentThread().getName()).append(",");
                    string.append(fromAccountNum).append(",");
                    string.append(toAccountNum).append(",");
                    string.append(amount).append(",");
                    string.append(LocalDateTime.now()).append("\n");

                    securityList.add(string);
                }
            }
        }
    }

    public void transfer(@NonNull String fromAccountNum, @NonNull String toAccountNum, long amount) throws InterruptedException {
        Account accountFrom = this.getAccount(fromAccountNum);
        Account accountTo = this.getAccount(toAccountNum);

        synchronized (accountFrom.compareTo(accountTo) > 0 ? accountFrom : accountTo) {
            synchronized (accountFrom.compareTo(accountTo) > 0 ? accountTo : accountFrom) {
                if (accountFrom.isStatusLock() || accountTo.isStatusLock()) {
                    return;
                }

                StringBuilder string = new StringBuilder();

                string.append(Thread.currentThread().getName()).append(",");
                string.append(fromAccountNum).append(" / ").append(getBalance(fromAccountNum)).append(",");
                string.append(toAccountNum).append(" / ").append(getBalance(toAccountNum)).append(",");
                string.append(amount).append(",");

                accountFrom.getMany(accountTo, amount);
                accountTo.addMoney(amount);

                string.append(fromAccountNum).append(" / ").append(getBalance(fromAccountNum)).append(",");
                string.append(toAccountNum).append(" / ").append(getBalance(toAccountNum)).append(",");
                string.append(LocalDateTime.now()).append("\n");

                transferList.add(string);
            }
        }

        if (amount > 50000) {
            fraudSecurity(fromAccountNum, toAccountNum, amount);
        }
    }

    public long getBalance(String accountNum) {
        for (Map.Entry<String, Account> account : accounts.entrySet()) {
            if (account.getValue().getAccNumber().equals(accountNum)) {
                return account.getValue().getBalance();
            }
        }
        return 0;
    }

    public Account getAccount(String accountNum) {
        for (Map.Entry<String, Account> account : accounts.entrySet()) {
            if (account.getValue().getAccNumber().equals(accountNum)) {
                return account.getValue();
            }
        }
        return null;
    }
}