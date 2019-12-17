package org.arijit.coucheventstore;

import java.util.AbstractQueue;
import java.util.List;
import java.util.Queue;
import java.util.ArrayList;
import java.util.Iterator;

public class ListenableQueue<E> extends AbstractQueue<E> {

    interface Listener<E> {
        void onElementAdded(E element);
    }

    private final Queue<E> delegate; // backing queue
    private final List<Listener<E>> listeners = new ArrayList<>();

    public ListenableQueue(Queue<E> delegate) {
        this.delegate = delegate;
    }

    public ListenableQueue<E> registerListener(final Listener<E> listener) {
        listeners.add(listener);
        return this;
    }

    @Override
    public boolean offer(final E e) {
        if (delegate.offer(e)) {
            listeners.forEach(listener -> listener.onElementAdded(e));
            return true;
        } else {
            return false;
        }
    }

    // following methods just delegate to backing instance
    @Override
    public E poll() {
        return delegate.poll();
    }

    @Override
    public E peek() {
        return delegate.peek();
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public Iterator<E> iterator() {
        return delegate.iterator();
    }

}