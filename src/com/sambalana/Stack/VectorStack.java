package com.sambalana.Stack;

import java.util.Vector;

public class VectorStack<T> implements StackInterface<T> {

    private Vector<T> stack; // last element is the top entry in stack

    private static final int DEFAULT_INITIAL_CAPACITY = 50;

    public VectorStack() {
        this(DEFAULT_INITIAL_CAPACITY);
    }


    public VectorStack(int initialCapacity) {
        stack = new Vector<T>(initialCapacity);
    }


    public void push(T newEntry) {
        stack.add(newEntry);
    }


    public T pop() {
        if (!isEmpty())
            return stack.remove(stack.size() - 1);
        else
            return null;
    }


    public T peek() {
        if (!isEmpty())
            return stack.lastElement();
        return null;
    }


    private T get(int index) {
        if (index < stack.size())
            return stack.get(index);
        return null;
    }


    private boolean set(int index, T value) {
        if (index < stack.size()) {
            stack.set(index, value);
            return true;
        }
        return false;
    }

    public int size() { return stack.size(); }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public void clear() {
        stack.clear();
    }


    private static <T extends Comparable <? super T>> boolean isInOrder(VectorStack<T> stack) {
        return isInOrder(stack, 0, stack.size() - 1);
    }


    private static <T extends Comparable <? super T>> boolean isInOrder(VectorStack<T> stack, int first, int last) {
        for (int i = first; i <= last; i++)
            if (indexOfSmallest(stack, i, last) != i)
                return false;
        return true;
    }


    private static <T extends Comparable <? super T>> int indexOfSmallest(VectorStack<T> stack, int first, int last) {
        int smallest = first;

        for (int i = first; i <= last; i++) {
            if (stack.get(i).compareTo(stack.get(smallest)) < 0)
                smallest = i;
        }

        return smallest;
    }


    private static <T extends Comparable <? super T>> void interchange(VectorStack<T> stack, int index1, int index2) {
        T element = stack.get(index1);
        stack.set(index1, stack.get(index2));
        stack.set(index2, element);
    }


    public static <T extends Comparable <? super T>> void selectionSort(VectorStack<T> stack) {
        for (int i = 0; i < stack.size() - 1; i++) {
            int smallest = indexOfSmallest(stack, i, stack.size() - 1);
            if (smallest != i)
                interchange(stack, i, smallest);
        }
    }


    public static <T extends Comparable <? super T>> void selectionSortRecursive(VectorStack<T> stack, int pos) {
        if (pos < stack.size()) {
            int smallest = indexOfSmallest(stack, pos, stack.size() - 1);
            if (smallest != pos)
                interchange(stack, pos, smallest);
            selectionSortRecursive(stack, pos + 1);
        }
    }


    public static <T extends Comparable <? super T>> void bubbleSort(VectorStack<T> stack) {
        boolean done = false;
        int sortedElements = 0;
        while (!done) {
            done = true;
            sortedElements++;
            for (int current = 0; current < stack.size() - sortedElements; current++) {
                int next = current + 1;
                if (stack.get(current).compareTo(stack.get(next)) > 0) {
                    interchange(stack, current, next);
                    done = false;
                }
            }
        }
    }

    public static <T extends Comparable <? super T>> void bubbleSortRecursive(VectorStack<T> stack, int sortedElements) {
        boolean done = true;
        sortedElements++;
        for (int current = 0; current < stack.size() - sortedElements; current++) {
            int next = current + 1;
            if (stack.get(current).compareTo(stack.get(next)) > 0) {
                interchange(stack, current, next);
                done = false;
            }
        }
        if (!done)
            bubbleSortRecursive(stack, sortedElements);
    }
}