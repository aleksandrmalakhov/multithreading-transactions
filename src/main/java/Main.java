import lombok.NonNull;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static StringBuilder heading;

    public static void main(String[] args) throws InterruptedException {
        Bank bank = new Bank();

        Account test1 = new Account(100_000_000, "Test1");
        Account test2 = new Account(100_000_000, "Test2");
        Account test3 = new Account(15_000_000, "Test3");
        Account test4 = new Account(0, "Test4");

        bank.addAccount("Test1", test1);
        bank.addAccount("Test2", test2);
        bank.addAccount("Test3", test3);
        bank.addAccount("Test4", test4);

        ExecutorService service = Executors.newCachedThreadPool();

        for (int i = 0; i < 3; i++) {
            service.execute(() -> {
                try {
                    for (int y = 0; y < 100; y++) {
                        bank.transfer("Test1", "Test2", 50_000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            service.execute(() -> {
                try {
                    for (int x = 0; x < 100; x++) {
                        bank.transfer("Test2", "Test1", 60_000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            service.execute(() -> {
                try {
                    for (int z = 0; z < 100; z++) {
                        bank.transfer("Test3", "Test4", 50_000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        service.shutdown();

        if (service.awaitTermination(60, TimeUnit.SECONDS)) {

            ExecutorService newService = Executors.newFixedThreadPool(2);

            newService.execute(() -> {
                try {
                    heading = new StringBuilder();

                    heading.append("Thread").append(",");
                    heading.append("From account number before transfer/Balance").append(",");
                    heading.append("To account number before transfer/Balance").append(",");
                    heading.append("Amount").append(",");
                    heading.append("From account number after transfer/Balance").append(",");
                    heading.append("To account number after transfer/Balance").append(",");
                    heading.append("Date/Time").append("\n");

                    writeFile(bank.getTransferList(), heading, "transfer");

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            newService.execute(() -> {
                try {
                    heading = new StringBuilder();

                    heading.append("Thread").append(",");
                    heading.append("From account number").append(",");
                    heading.append("To account number").append(",");
                    heading.append("Amount").append(",");
                    heading.append("Date/Time").append("\n");

                    writeFile(bank.getSecurityList(), heading, "security");

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            newService.shutdown();
            newService.awaitTermination(60, TimeUnit.SECONDS);
        }
    }

    private static void writeFile(@NonNull List<StringBuilder> list, @NonNull StringBuilder heading, String fileName)
            throws IOException {
        FileWriter writer = new FileWriter("src/main/resources/" + fileName + ".csv");
        writer.write(heading.toString());

        for (StringBuilder str : list) {
            writer.write(str.toString());
        }
        writer.close();
    }
}