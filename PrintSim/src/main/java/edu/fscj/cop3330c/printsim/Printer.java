// Printer.java
// D. Singletary
// 11/17/24
// printer simulation
// Bailey Lester
// 8/9/2026
// Added ReentrantLock for thread-safe printer access
// Changed for use of three worker threads

package edu.fscj.cop3330c.printsim;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class IdleState implements PrinterState {
    @Override
    public void processQueue(Printer printer) {
        if (printer.hasPendingJobs()) {
            System.out.println("Printer is transitioning to Printing state...");
            printer.setState(new PrintingState());
            printer.getState().processQueue(printer); // Delegate to PrintingState
        } else {
            System.out.println("Printer is idle. No jobs in the queue.");
        }
    }
}

class PrintingState implements PrinterState {
    @Override
    public void processQueue(Printer printer) {
        if (printer.hasPendingJobs()) {
            PrintJob job = printer.pollJob();
            System.out.println("Submitting Job #" + job.getJobNumber());
            printer.print(job);

            // Check if more jobs are pending
            if (printer.hasPendingJobs()) {
                //System.out.println("Continuing to print next job...");
            } else {
                System.out.println(
                        "All jobs are printed. Transitioning to Idle state...");
                printer.setState(new IdleState());
            }
        } else {
            System.out.println(
                    "No jobs to print. Transitioning to Idle state...");
            printer.setState(new IdleState());
        }
    }
}

// Printer Class
public class Printer {
    private final Queue<PrintJob> printQueue;
    private static int jobCounter = 0;
    private PrinterState state;
    private final Lock lock;

    public Printer() {
        printQueue = new LinkedList<>();
        state = new IdleState();// Start in IdleState
        lock = new ReentrantLock();
    }

    public void setState(PrinterState state) {
        lock.lock();
        try {
            this.state = state;
        } finally {
            lock.unlock();
        }
    }

    public PrinterState getState() {
        lock.lock();
        try {
            return state;
        } finally {
            lock.unlock();
        }
    }

    public boolean hasPendingJobs() {
        lock.lock();
        try {
            return !printQueue.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    public PrintJob pollJob() {
        lock.lock();
        try {
            return printQueue.poll();
        } finally {
            lock.unlock();
        }
    }

    public void addDocument(String document) {
        lock.lock();
        try {
            PrintJob job = new PrintJob(document, ++jobCounter);
            printQueue.offer(job);
            System.out.println("Document added to the queue: " +
                    document + " (Job #" + job.getJobNumber() + ")");
        } finally {
            lock.unlock();
        }
    }

    public void print(PrintJob job) {
        lock.lock();
        try {
            System.out.println("Printing Job #" + job.getJobNumber() +
                    ": " + job.getDocumentName());
            try {
                Thread.sleep(1000); // Simulate time taken to print
            } catch (InterruptedException e) {
                System.out.println("Printing was interrupted.");

                // Restore Interrupted Status
                Thread.currentThread().interrupt();
            }
            System.out.println("Printing completed for Job #" +
                    job.getJobNumber());
        } finally {
            lock.unlock();
        }
    }

    public void processQueue() {
        lock.lock();
        try {
            state.processQueue(this);
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        final int MAX_IDLE = 5; // seconds

        Printer printer = new Printer();
        DocumentRepository documentRepository = new DocumentRepository();

        // Three worker instances that share the same Printer & DocRespo objects
        PrinterWorker worker1 = new PrinterWorker(printer,
                documentRepository, MAX_IDLE);
        PrinterWorker worker2 = new PrinterWorker(printer,
                documentRepository, MAX_IDLE);
        PrinterWorker worker3 = new PrinterWorker(printer,
                documentRepository, MAX_IDLE);

        // Three threads

        Thread workerThread1 = new Thread(worker1);
        Thread workerThread2 = new Thread(worker2);
        Thread workerThread3 = new Thread(worker3);

        // Start the 3 worker threads
        workerThread1.start();
        workerThread2.start();
        workerThread3.start();

        try {
            // Wait for all worker threads to finish and record the time
            workerThread1.join();
            workerThread2.join();
            workerThread3.join();
        } catch (InterruptedException e) {
            System.out.println(
                    "Main thread interrupted while waiting for worker threads.");
            Thread.currentThread().interrupt();
        }
    }
}
