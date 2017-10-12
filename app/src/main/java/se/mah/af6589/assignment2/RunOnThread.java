package se.mah.af6589.assignment2;

/**
 * Created by Gustaf Bohlin on 29/09/2017.
 */

public class RunOnThread {

    private Worker worker;
    private Buffer<Runnable> buffer = new Buffer<>();

    public void start() {
        if (worker == null) {
            worker = new Worker();
            worker.start();
        }
    }

    public void stop() {
        if (worker != null) {
            worker.interrupt();
            worker = null;
        }
    }

    public void execute(Runnable task) {
        buffer.put(task);
    }

    private class Worker extends Thread {
        public void run() {
            Runnable runnable;
            while (worker != null) {
                try {
                    runnable = buffer.get();
                    runnable.run();
                } catch (InterruptedException e) {
                    worker = null;
                }
            }
        }
    }
}
