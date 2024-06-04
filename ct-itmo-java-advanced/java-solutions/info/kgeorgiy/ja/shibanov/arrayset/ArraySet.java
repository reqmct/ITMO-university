package info.kgeorgiy.ja.shibanov.arrayset;

import java.util.*;

public class ArraySet<T> extends AbstractSet<T> implements NavigableSet<T> {
    private final List<T> list;
    private final Comparator<? super T> cmp;

    private final Comparator<? super T> defaultCmp;

    private enum Comparison {LESS, LESS_OR_EQUAL, GREATER_OR_EQUAL, GREATER}

    public ArraySet() {
        this(null, null);
    }

    public ArraySet(Collection<? extends T> collection) {
        this(collection, null);
    }

    public ArraySet(Comparator<? super T> cmp) {
        this(null, cmp);
    }

    public <E extends T> boolean isSorted(Collection<E> collection) {
        var iterator = collection.iterator();
        if (!iterator.hasNext()) {
            return true;
        }

        var current = iterator.next();
        while (iterator.hasNext()) {
            var next = iterator.next();
            if (getComparatorOrDefault().compare(current, next) >= 0) {
                return false;
            }
            current = next;
        }

        return true;
    }

    private ArrayList<T> toSortArray(Collection<? extends T> collection) {
        if (collection == null) {
            return new ArrayList<>();
        }
        if (isSorted(collection)) {
            return new ArrayList<>(collection);
        }
        TreeSet<T> tmp = new TreeSet<>(getComparatorOrDefault());
        tmp.addAll(collection);
        return new ArrayList<>(tmp);
    }

    public ArraySet(Collection<? extends T> collection, Comparator<? super T> cmp) {
        this.cmp = cmp;
        this.defaultCmp = Collections.reverseOrder(cmp).reversed();
        list = Collections.unmodifiableList(toSortArray(collection));
    }

    private ArraySet(List<T> list, Comparator<? super T> cmp, Comparator<? super T> defaultCmp) {
        this.list = list;
        this.cmp = cmp;
        if (defaultCmp != null) {
            this.defaultCmp = defaultCmp;
        } else {
            this.defaultCmp = Collections.reverseOrder(cmp).reversed();
        }
    }

    private T getElement(int index) {
        if (isCorrectIndex(index)) {
            return list.get(index);
        }
        return null;
    }

    private int indexAdjustment(int ind, Comparison comparison) {
        switch (comparison) {
            case LESS -> {
                return ind >= 0 ? ind - 1 : -ind - 2;
            }
            case LESS_OR_EQUAL -> {
                return ind >= 0 ? ind : -ind - 2;
            }
            case GREATER -> {
                return ind >= 0 ? ind + 1 : -ind - 1;
            }
            case GREATER_OR_EQUAL -> {
                return ind >= 0 ? ind : -ind - 1;
            }
        }
        return -1;
    }

    private int binarySearchWithAdjustment(T t, Comparison comparison) {
        int ind = Collections.binarySearch(list, t, getComparatorOrDefault());
        return indexAdjustment(ind, comparison);
    }

    @Override
    public T lower(T t) {
        return getElement(binarySearchWithAdjustment(t, Comparison.LESS));
    }

    @Override
    public T floor(T t) {
        return getElement(binarySearchWithAdjustment(t, Comparison.LESS_OR_EQUAL));
    }

    @Override
    public T ceiling(T t) {
        return getElement(binarySearchWithAdjustment(t, Comparison.GREATER_OR_EQUAL));
    }

    @Override
    public T higher(T t) {
        return getElement(binarySearchWithAdjustment(t, Comparison.GREATER));
    }

    @Override
    public T pollFirst() {
        throw new UnsupportedOperationException("The pollFirst operation is not supported");
    }

    @Override
    public T pollLast() {
        throw new UnsupportedOperationException("The pollLast operation is not supported");
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @Override
    public NavigableSet<T> descendingSet() {
        return new ArraySet<>(list.reversed(),
                cmp == null ? null : cmp.reversed(),
                cmp == null ? defaultCmp.reversed() : defaultCmp);
    }

    @Override
    public Iterator<T> descendingIterator() {
        return descendingSet().iterator();
    }

    private boolean isCorrectIndex(int index) {
        return 0 <= index && index < size();
    }

    private NavigableSet<T> customSubSet(T fromElement, boolean fromInclusive, T toElement, boolean toInclusive) {
        int start = binarySearchWithAdjustment(fromElement, fromInclusive ? Comparison.GREATER_OR_EQUAL : Comparison.GREATER);
        int finish = binarySearchWithAdjustment(toElement, toInclusive ? Comparison.LESS_OR_EQUAL : Comparison.LESS);
        if (finish < start || !isCorrectIndex(start) || !isCorrectIndex(finish)) {
            return new ArraySet<>(comparator());
        }
        return new ArraySet<>(list.subList(start, finish + 1), comparator(), getDefaultComparator());
    }

    @Override
    public NavigableSet<T> subSet(T fromElement, boolean fromInclusive, T toElement, boolean toInclusive) {
        if (getComparatorOrDefault().compare(fromElement, toElement) > 0) {
            throw new IllegalArgumentException("Incorrect range of values");
        }
        return customSubSet(fromElement, fromInclusive, toElement, toInclusive);
    }

    @Override
    public NavigableSet<T> headSet(T toElement, boolean inclusive) {
        if (list.isEmpty()) {
            return new ArraySet<>(cmp);
        }
        return customSubSet(first(), true, toElement, inclusive);
    }

    @Override
    public NavigableSet<T> tailSet(T fromElement, boolean inclusive) {
        if (list.isEmpty()) {
            return new ArraySet<>(cmp);
        }
        return customSubSet(fromElement, inclusive, last(), true);
    }

    private Comparator<? super T> getDefaultComparator() {
        return defaultCmp;
    }

    private Comparator<? super T> getComparatorOrDefault() {
        if (cmp != null) {
            return cmp;
        }
        return getDefaultComparator();
    }

    @Override
    public Comparator<? super T> comparator() {
        return cmp;
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        return subSet(fromElement, true, toElement, false);
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        return headSet(toElement, false);
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        return tailSet(fromElement, true);
    }

    @Override
    public T first() {
        return list.getFirst();
    }

    @Override
    public T last() {
        return list.getLast();
    }

    @Override
    public int size() {
        return list.size();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object object) {
        return Collections.binarySearch(list, (T) object, getComparatorOrDefault()) >= 0;
    }
}
