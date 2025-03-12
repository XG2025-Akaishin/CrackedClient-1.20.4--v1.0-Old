package me.alpha432.oyvey.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;

/**
 * Implementation of https://guava.dev/releases/15.0/api/docs/com/google/common/collect/EvictingQueue.html
 * backed by a first-in last-out {@link ArrayDeque}.
 *
 * @author linus
 * @since 1.0
 * @param <E>
 *
 * @see ArrayDeque
 */
public class EvictingQueue<E> extends ArrayDeque<E>
{
    //
    private final int limit;

    /**
     *
     * @param limit
     */
    public EvictingQueue(int limit)
    {
        this.limit = limit;
    }

    /**
     *
     * @param element element whose presence in this collection is to be ensured
     * @return
     */
    @Override
    public boolean add(@NotNull E element)
    {
        boolean add = super.add(element);
        while (add && size() > limit)
        {
            super.remove();
        }
        return add;
    }

    /**
     *
     * @param element element whose presence in this collection is to be ensured
     */
    @Override
    public void addFirst(@NotNull E element)
    {
        super.addFirst(element);
        while (size() > limit)
        {
            super.removeLast();
        }
    }

    /**
     *
     * @return
     */
    public int getLimit()
    {
        return limit;
    }
}
