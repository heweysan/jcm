package pentomino.core.devices;

import javax.print.DocPrintJob;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;

public class PrintJobWatcher {

	// true if it is safe to close the print job's input stream
    private boolean done = false;
 
    public PrintJobWatcher() {
 
    }
 
    public PrintJobWatcher(DocPrintJob job) {
        setPrintJob(job);
    }
	
    public void setPrintJob(DocPrintJob job) {
        // Add a listener to the print job
        job.addPrintJobListener(
                new PrintJobAdapter() {
                    public void printJobCanceled(PrintJobEvent printJobEvent) {
                    	System.out.println("JobCanceled");
                        allDone();
                    }
 
                    public void printJobCompleted(PrintJobEvent printJobEvent) {
                    	System.out.println("JobCompleted");
                    	allDone();
                    }
 
                    public void printJobFailed(PrintJobEvent printJobEvent) {
                    	System.out.println("JobFailed");
                    	allDone();
                    }
 
                    public void printJobNoMoreEvents(PrintJobEvent printJobEvent) {
                    	System.out.println("JobNoMoreEvents");
                        allDone();
                    }
 
                    public void printJobRequiresAttention(PrintJobEvent printJobEvent) {
                    	System.out.println("JobRequiresAttention");
                        allDone();
                    }
                    void allDone() {
                        synchronized (PrintJobWatcher.this) {
                            done = true;
                            PrintJobWatcher.this.notify();
                        }
                    }
                });
    }
    
    public synchronized void waitForDone() {
        try {
            while (!done) {
                wait();
            }
        } catch (InterruptedException e) {
        }
    }
}
