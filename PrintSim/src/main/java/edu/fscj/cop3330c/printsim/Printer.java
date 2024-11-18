// Printer.java
// D. Singletary
// 11/17/24
// printer simulation

package edu.fscj.cop3330c.printsim;

import java.util.LinkedList;
import java.util.Queue;

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
    private Queue<PrintJob> printQueue;
    private static int jobCounter = 0;
    private PrinterState state;

    public Printer() {
        printQueue = new LinkedList<>();
        state = new IdleState(); // Start in IdleState
    }

    public void setState(PrinterState state) {
        this.state = state;
    }

    public PrinterState getState() {
        return state;
    }

    public boolean hasPendingJobs() {
        return !printQueue.isEmpty();
    }

    public PrintJob pollJob() {
         return printQueue.poll();
    }

    public void addDocument(String document) {
        PrintJob job = new PrintJob(document, ++jobCounter);
        printQueue.offer(job);
        System.out.println("Document added to the queue: " +
                document + " (Job #" + job.getJobNumber() + ")");
    }

    public void print(PrintJob job) {
        System.out.println("Printing Job #" + job.getJobNumber() +
                ": " + job.getDocumentName());
        try {
            Thread.sleep(1000); // Simulate time taken to print
        } catch (InterruptedException e) {
            System.out.println("Printing was interrupted.");
        }
        System.out.println("Printing completed for Job #" +
                job.getJobNumber());
    }

    public void processQueue() {
        state.processQueue(this);
    }

    public static void main(String[] args) {
        final int MAX_IDLE = 5; // seconds

        Printer printer = new Printer();
        DocumentRepository documentRepository = new DocumentRepository();

        // Create and start two worker threads
        PrinterWorker worker = new PrinterWorker(printer,
                documentRepository, MAX_IDLE);

        Thread workerThread = new Thread(worker);

        workerThread.start();

        try {
            // Wait for both worker threads to finish and record the time
            workerThread.join();
        } catch (InterruptedException e) {
            System.out.println(
                    "Main thread interrupted while waiting for worker threads.");
        }
    }
}
