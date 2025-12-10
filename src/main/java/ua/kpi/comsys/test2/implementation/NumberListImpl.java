/*
 * Copyright (c) 2014, NTUU KPI, Computer systems department and/or its affiliates. All rights reserved.
 * NTUU KPI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package ua.kpi.comsys.test2.implementation;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.*;

import ua.kpi.comsys.test2.NumberList;

/**
 * Variant: 4.
 * C3 = 1 (Circular unidirectional)
 * C5 = 4 (Decimal) -> Additional: Binary
 * C7 = 4 (Remainder of division of two numbers)
 *
 * @author Zhovmir Oleksandr, Group IC-32
 */

public class NumberListImpl implements NumberList {

    private static class Node {
        Byte item;
        Node next;

        Node(Byte element, Node next) {
            this.item = element;
            this.next = next;
        }
    }

    private Node head;
    private Node tail; // Зберігаємо хвіст для швидкого додавання в кільцевому списку
    private int size;

    /**
     * Default constructor. Returns empty <tt>NumberListImpl</tt>
     */
    public NumberListImpl() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * from file, defined in string format.
     *
     * @param file - file where number is stored.
     */
    public NumberListImpl(File file) {
        this();
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (!lines.isEmpty()) {
                String content = lines.get(0).trim();
                initFromDecimalString(content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * in string notation.
     *
     * @param value - number in string notation.
     */
    public NumberListImpl(String value) {
        this();
        initFromDecimalString(value);
    }

    private void initFromDecimalString(String value) {
        if (value == null || value.isEmpty()) return;
        BigInteger bigInt = new BigInteger(value);
        String hexString = bigInt.toString(16);

        for (char c : hexString.toCharArray()) {
            byte digit = (byte) Character.digit(c, 16);
            this.add(digit);
        }
    }

    private NumberListImpl(BigInteger value, int radix) {
        this();
        String strValue = value.toString(radix);
        for (char c : strValue.toCharArray()) {
            byte digit = (byte) Character.digit(c, radix);
            this.add(digit);
        }
    }

    /**
     * Saves the number, stored in the list, into specified file
     * in <b>decimal</b> scale of notation.
     *
     * @param file - file where number has to be stored.
     */
    public void saveList(File file) {
        try {
            Files.write(file.toPath(), this.toDecimalString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns student's record book number, which has 4 decimal digits.
     *
     * @return student's record book number.
     */
    public static int getRecordBookNumber() {
        return 4;
    }

    /**
     * Returns new <tt>NumberListImpl</tt> which represents the same number
     * in other scale of notation, defined by personal test assignment (Binary).<p>
     *
     * Does not impact the original list.
     *
     * @return <tt>NumberListImpl</tt> in Binary scale of notation.
     */
    public NumberListImpl changeScale() {
        BigInteger currentVal = this.toBigInteger(16);
        return new NumberListImpl(currentVal, 2);
    }

    /**
     * Returns new <tt>NumberListImpl</tt> which represents the result of
     * additional operation, defined by personal test assignment (Modulo).<p>
     *
     * Does not impact the original list.
     *
     * @param arg - second argument of additional operation
     * @return result of additional operation (this % arg).
     */
    public NumberListImpl additionalOperation(NumberList arg) {
        BigInteger val1 = this.toBigInteger(16);

        BigInteger val2;
        if (arg instanceof NumberListImpl) {
            val2 = ((NumberListImpl) arg).toBigInteger(16);
        } else {
            StringBuilder sb = new StringBuilder();
            for (Byte b : arg) {
                sb.append(Integer.toString(b, 16));
            }
            val2 = new BigInteger(sb.toString(), 16);
        }

        if (val2.equals(BigInteger.ZERO)) {
            throw new ArithmeticException("Division by zero");
        }

        BigInteger remainder = val1.remainder(val2);

        return new NumberListImpl(remainder, 16);
    }

    private BigInteger toBigInteger(int radix) {
        if (isEmpty()) return BigInteger.ZERO;
        StringBuilder sb = new StringBuilder();
        Node current = head;
        do {
            sb.append(Integer.toString(current.item, radix));
            current = current.next;
        } while (current != head);
        return new BigInteger(sb.toString(), radix);
    }

    /**
     * Returns string representation of number, stored in the list
     * in <b>decimal</b> scale of notation.
     *
     * @return string representation in <b>decimal</b> scale.
     */
    public String toDecimalString() {
        return toBigInteger(16).toString(10);
    }

    @Override
    public String toString() {
        if (isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        Node current = head;
        do {
            sb.append(Integer.toHexString(current.item).toUpperCase());
            current = current.next;
            if (current != head) {
                sb.append(", ");
            }
        } while (current != head);
        return sb.append(']').toString();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    @Override
    public Iterator<Byte> iterator() {
        return new Iterator<Byte>() {
            private Node current = head;
            private int count = 0;

            @Override
            public boolean hasNext() {
                return count < size;
            }

            @Override
            public Byte next() {
                if (!hasNext()) throw new NoSuchElementException();
                Byte val = current.item;
                current = current.next;
                count++;
                return val;
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        int i = 0;
        for (Byte b : this) {
            result[i++] = b;
        }
        return result;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public boolean add(Byte e) {
        if (e == null) throw new NullPointerException();
        final Node newNode = new Node(e, null);

        if (head == null) {
            head = newNode;
            tail = newNode;
            tail.next = head;
        } else {
            tail.next = newNode;
            tail = newNode;
            tail.next = head;
        }
        size++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (head == null || o == null) return false;

        Node current = head;
        Node prev = tail;

        for (int i = 0; i < size; i++) {
            if (o.equals(current.item)) {
                unlink(prev, current);
                return true;
            }
            prev = current;
            current = current.next;
        }
        return false;
    }

    private void unlink(Node prev, Node target) {
        if (size == 1) {
            head = null;
            tail = null;
        } else {
            prev.next = target.next;
            if (target == head) {
                head = target.next;
                tail.next = head;
            }
            if (target == tail) {
                tail = prev;
                tail.next = head;
            }
        }
        size--;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object e : c)
            if (!contains(e))
                return false;
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends Byte> c) {
        boolean modified = false;
        for (Byte e : c)
            if (add(e))
                modified = true;
        return modified;
    }

    @Override
    public boolean addAll(int index, Collection<? extends Byte> c) {
        checkPositionIndex(index);
        boolean modified = false;
        for (Byte e : c) {
            add(index++, e);
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object e : c) {
            while (contains(e)) {
                remove(e);
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Iterator<Byte> it = iterator();
        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
            }
        }
        Node current = head;
        Node prev = tail;
        if (head == null) return false;

        int originalSize = size;
        for (int i = 0; i < originalSize; i++) {
            Node nextNode = current.next;
            if (!c.contains(current.item)) {
                unlink(prev, current);
                modified = true;
            } else {
                prev = current;
            }
            current = nextNode;
        }
        return modified;
    }

    @Override
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof List)) return false;

        ListIterator<Byte> e1 = listIterator();
        ListIterator<?> e2 = ((List<?>) o).listIterator();
        while (e1.hasNext() && e2.hasNext()) {
            Byte o1 = e1.next();
            Object o2 = e2.next();
            if (!(o1 == null ? o2 == null : o1.equals(o2)))
                return false;
        }
        return !(e1.hasNext() || e2.hasNext());
    }

    @Override
    public Byte get(int index) {
        checkElementIndex(index);
        return node(index).item;
    }

    @Override
    public Byte set(int index, Byte element) {
        checkElementIndex(index);
        Node x = node(index);
        Byte oldVal = x.item;
        x.item = element;
        return oldVal;
    }

    @Override
    public void add(int index, Byte element) {
        checkPositionIndex(index);
        if (index == size) {
            add(element);
        } else {
            Node newNode = new Node(element, null);
            if (index == 0) {
                newNode.next = head;
                head = newNode;
                tail.next = head;
            } else {
                Node prev = node(index - 1);
                newNode.next = prev.next;
                prev.next = newNode;
            }
            size++;
        }
    }

    @Override
    public Byte remove(int index) {
        checkElementIndex(index);
        Node target;
        if (index == 0) {
            target = head;
            unlink(tail, head);
        } else {
            Node prev = node(index - 1);
            target = prev.next;
            unlink(prev, target);
        }
        return target.item;
    }

    @Override
    public int indexOf(Object o) {
        int index = 0;
        if (o == null) return -1;
        if (head == null) return -1;

        Node current = head;
        do {
            if (o.equals(current.item)) return index;
            current = current.next;
            index++;
        } while (current != head);

        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        int index = 0;
        int lastIndex = -1;
        if (head == null || o == null) return -1;

        Node current = head;
        do {
            if (o.equals(current.item)) lastIndex = index;
            current = current.next;
            index++;
        } while (current != head);

        return lastIndex;
    }

    @Override
    public ListIterator<Byte> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<Byte> listIterator(int index) {
        checkPositionIndex(index);
        return new ListIterator<Byte>() {
            private Node current = (index == size) ? head : node(index);
            private int nextIndex = index;

            @Override
            public boolean hasNext() {
                return nextIndex < size;
            }

            @Override
            public Byte next() {
                if (!hasNext()) throw new NoSuchElementException();
                Byte val = current.item;
                current = current.next;
                nextIndex++;
                return val;
            }

            @Override
            public boolean hasPrevious() {
                return nextIndex > 0;
            }

            @Override
            public Byte previous() {
                if (!hasPrevious()) throw new NoSuchElementException();
                Node prevNode = node(nextIndex - 1);
                current = prevNode;
                nextIndex--;
                return prevNode.item;
            }

            @Override
            public int nextIndex() { return nextIndex; }
            @Override
            public int previousIndex() { return nextIndex - 1; }
            @Override
            public void remove() { throw new UnsupportedOperationException(); }
            @Override
            public void set(Byte e) { throw new UnsupportedOperationException(); }
            @Override
            public void add(Byte e) { throw new UnsupportedOperationException(); }
        };
    }

    @Override
    public List<Byte> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("SubList not implemented for this task");
    }

    @Override
    public boolean swap(int index1, int index2) {
        if (index1 < 0 || index1 >= size || index2 < 0 || index2 >= size) {
            return false;
        }
        if (index1 == index2) return true;

        Byte val1 = get(index1);
        Byte val2 = get(index2);

        set(index1, val2);
        set(index2, val1);

        return true;
    }

    @Override
    public void sortAscending() {
        // Простий Bubble Sort
        if (size <= 1) return;
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                if (get(j) > get(j + 1)) {
                    swap(j, j + 1);
                }
            }
        }
    }

    @Override
    public void sortDescending() {
        if (size <= 1) return;
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                if (get(j) < get(j + 1)) {
                    swap(j, j + 1);
                }
            }
        }
    }

    @Override
    public void shiftLeft() {
        if (size <= 1) return;
        head = head.next;
        tail = tail.next;
    }

    @Override
    public void shiftRight() {
        if (size <= 1) return;
        Node newTail = node(size - 2); 
        tail = newTail;
        head = tail.next;
    }

    private Node node(int index) {
        Node x = head;
        for (int i = 0; i < index; i++)
            x = x.next;
        return x;
    }

    private void checkElementIndex(int index) {
        if (!isElementIndex(index))
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    private void checkPositionIndex(int index) {
        if (!isPositionIndex(index))
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    private boolean isElementIndex(int index) {
        return index >= 0 && index < size;
    }

    private boolean isPositionIndex(int index) {
        return index >= 0 && index <= size;
    }
}
