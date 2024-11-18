// PrinterWorker.java
// D. Singletary
// 11/17/24
// printer simulation

package edu.fscj.cop3330c.printsim;

import java.util.List;
import java.util.Random;

public class PrinterWorker implements Runnable {
    private final Printer printer;
    private final DocumentRepository documentRepository;
    private final int maxIdleTime; // seconds
    private final Random random;

    public PrinterWorker(Printer printer,
                         DocumentRepository documentRepository,
                         int maxIdleTime) {
        this.printer = printer;
        this.documentRepository = documentRepository;
        this.maxIdleTime = maxIdleTime;
        this.random = new Random();
    }

    @Override
    public void run() {
        long lastAddedTime = System.currentTimeMillis();

        while (true) {

            System.out.println("Thread ID: " + Thread.currentThread().getId() +
                    " Obtaining the next documents...");

            String document =
                    documentRepository.getNextDocument();

            if (document != null) {
                lastAddedTime = System.currentTimeMillis(); // Reset the last added time
                printer.addDocument(document);
            } else {
                System.out.println("No more documents in the repository.");
            }

            System.out.println("Checking the queue...");
            printer.processQueue(); // Delegate behavior to the current state

            // Exit condition: more than maxIdleTime seconds with no new documents added
            if (System.currentTimeMillis() - lastAddedTime > maxIdleTime * 1000) {
                System.out.println("Idle time exceeded. Worker thread exiting...");
                break;
            }
        }
    }
}