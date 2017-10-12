package se.mah.af6589.assignment2;

import java.util.LinkedList;

/**
 * Created by Gustaf Bohlin on 29/09/2017.
 */

public class Buffer<T> {

    private LinkedList<T> list = new LinkedList<>();

    public synchronized T get() throws InterruptedException {
        while (list.isEmpty())
            wait();
        return list.removeFirst();
    }

    public synchronized void put(T item) {
        list.addLast(item);
        notifyAll();
    }
}
