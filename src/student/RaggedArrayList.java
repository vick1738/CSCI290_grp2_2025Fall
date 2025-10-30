/**  
 * File: RaggedArrayList.java
 * ****************************************************************************
 *                           Revision History
 * ****************************************************************************
 * 
 * 8/2015 - Anne Applin - Added formatting and JavaDoc 
 * 2015 - Bob Boothe - starting code
 * ****************************************************************************
 */
package student;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

/**
 * * 
 * The RaggedArrayList is a 2 level data structure that is an array of arrays.
 *
 * It keeps the items in sorted order according to the comparator. Duplicates
 * are allowed. New items are added after any equivalent items.
 *
 * NOTE: normally fields, internal nested classes and non API methods should all
 * be private, however they have been made public so that the tester code can
 * set them 
 * @author Bob Booth
 * @param <E>  A generic data type so that this structure can be built with any
 * data type (object)
 */
public class RaggedArrayList<E> implements Iterable<E> {

    // must be even so when split get two equal pieces

    private static final int MINIMUM_SIZE = 4;
    /**
     * The total number of elements in the entire RaggedArrayList
     */
    public int size;
    /**
     * really is an array of L2Array, but compiler won't let me cast to that
     */
    public Object[] l1Array;
    /**
     * The number of elements in the l1Array that are used.
     */
    public int l1NumUsed;
    /**
     * a Comparator object so we can use compare for Song
     */
    private Comparator<E> comp;

    /** Returns the comparator used by this RaggedArrayList.
     * @Author: Vivek */
    public Comparator<E> getComparator() {
        return comp;
    }


    /**
     * create an empty list always have at least 1 second level array even if
     * empty, makes code easier (DONE - do not change)
     *
     * @param c a comparator object
     */
    public RaggedArrayList(Comparator<E> c) {
        size = 0;
        // you can't create an array of a generic type
        l1Array = new Object[MINIMUM_SIZE];
        // first 2nd level array
        l1Array[0] = new L2Array(MINIMUM_SIZE);
        l1NumUsed = 1;
        comp = c;
    }

    /**
     * ***********************************************************
     * nested class for 2nd level arrays 
     * read and understand it.
     * (DONE - do not change)
     */
    public class L2Array {

        /**
         * the array of items
         */
        public E[] items;
        /**
         * number of items in this L2Array with values
         */
        public int numUsed;

        /**
         * Constructor for the L2Array
         *
         * @param capacity the initial length of the array
         */
        public L2Array(int capacity) {
            // you can't create an array of a generic type
            items = (E[]) new Object[capacity];
            numUsed = 0;
        }
    }// end of nested class L2Array

   
    // ***********************************************************
    
    /**
     * total size (number of entries) in the entire data structure 
     * (DONE - do not change)
     *
     * @return total size of the data structure
     */
    public int size() {
        return size;
    }

    /**
     * null out all references so garbage collector can grab them but keep
     * otherwise empty l1Array and 1st L2Array 
     * (DONE - Do not change)
     */
    public void clear() {
        size = 0;
        // clear all but first l2 array
        Arrays.fill(l1Array, 1, l1Array.length, null);
        l1NumUsed = 1;
        L2Array l2Array = (L2Array) l1Array[0];
        // clear out l2array
        Arrays.fill(l2Array.items, 0, l2Array.numUsed, null);
        l2Array.numUsed = 0;
    }

    /**
     * *********************************************************
     * nested class for a list position used only internally 2 parts: 
     * level 1 index and level 2 index
     */
    public class ListLoc {

        /**
         * Level 1 index
         */
        public int level1Index;

        /**
         * Level 2 index
         */
        public int level2Index;

        /**
         * Parameterized constructor DONE (Do Not Change)
         *
         * @param level1Index input value for property
         * @param level2Index input value for property
         */
        public ListLoc(int level1Index, int level2Index) {
            this.level1Index = level1Index;
            this.level2Index = level2Index;
        }

        /**
         * test if two ListLoc's are to the same location 
         * (done -- do not change)
         *
         * @param otherObj the other listLoc
         * @return true if they are the same location and false otherwise
         */
        public boolean equals(Object otherObj) {
            // not really needed since it will be ListLoc
            if (getClass() != otherObj.getClass()) {
                return false;
            }
            ListLoc other = (ListLoc) otherObj;

            return level1Index == other.level1Index
                    && level2Index == other.level2Index;
        }

        /**
         * move ListLoc to next entry when it moves past the very last entry it
         * will be one index past the last value in the used level 2 array. Can
         * be used internally to scan through the array for sublist also can be
         * used to implement the iterator.
         */
        public void moveToNext() {
            L2Array l2 = (L2Array) l1Array[level1Index];
            level2Index++;
            if (level2Index >= l2.numUsed) {
                level1Index++;
                level2Index = 0;
            }
        }


    }

    /**6.1
     * find 1st matching entry
     *
     * @param item the thing we are searching for a place to put.
     * @return ListLoc of 1st matching item or of 1st item greater than 
     * the item if no match this might be an unused slot at the end of a 
     * level 2 array
     */
    public ListLoc findFront(E item) {
        // TO DO in part 3 
        // For each L2 array, binary search for first index >= item
        for (int l1 = 0; l1 < l1NumUsed; l1++) {
            L2Array l2 = (L2Array) l1Array[l1];
            int lo = 0;
            int hi = l2.numUsed;
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                if (comp.compare(l2.items[mid], item) < 0) {
                    lo = mid + 1;
                } else {
                    hi = mid;
                }
            }
            if (lo < l2.numUsed) {
                return new ListLoc(l1, lo);
            }
        }
        // not found: return one-past-last position
        L2Array last = (L2Array) l1Array[l1NumUsed - 1];
        return new ListLoc(l1NumUsed - 1, last.numUsed);
    }

    /** 6.2
     * find location after the last matching entry or if no match, it finds
     * the index of the next larger item this is the position to add a new 
     * entry this might be an unused slot at the end of a level 2 array
     *
     * @param item the thing we are searching for a place to put.
     * @return the location where this item should go
     */
    public ListLoc findEnd(E item) {
        // TO DO in part 3
        for (int l1 = 0; l1 < l1NumUsed; l1++) {
            L2Array l2 = (L2Array) l1Array[l1];
            int lo = 0;
            int hi = l2.numUsed;
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                if (comp.compare(l2.items[mid], item) <= 0) {
                    lo = mid + 1;
                } else {
                    hi = mid;
                }
            }
            if (lo < l2.numUsed) {
                return new ListLoc(l1, lo);
            }
        }
        // not found: return one-past-last position
        L2Array last = (L2Array) l1Array[l1NumUsed - 1];
        return new ListLoc(l1NumUsed - 1, last.numUsed);
    }


    /**
     * add object after any other matching values findEnd will give the
     * insertion position
     *
     * @param item the thing we are searching for a place to put.
     * @return
     */
    public boolean add(E item) {
        // 1. Find where to insert
        ListLoc loc = findEnd(item);
        int i = loc.level1Index;
        int j = loc.level2Index;

        L2Array l2 = (L2Array) l1Array[i];

        // 2. Shift elements within the level-2 array
        System.arraycopy(l2.items, j, l2.items, j + 1, l2.numUsed - j);
        l2.items[j] = item;
        l2.numUsed++;
        size++;

        // 3. If that L2 is now full, decide whether to double or split
        if (l2.numUsed == l2.items.length) {
            if (l2.items.length < l1Array.length) {
                doubleL2(i);
            } else {
                splitL2(i);
            }
        }

        return true;
    }

    /** Double the capacity of a level-2 array (same index). */
    private void doubleL2(int index) {
        L2Array l2 = (L2Array) l1Array[index];
        l2.items = Arrays.copyOf(l2.items, l2.items.length * 2);
    }

    /** Double the capacity of the level-1 array. */
    private void doubleL1() {
        l1Array = Arrays.copyOf(l1Array, l1Array.length * 2);
    }

    /**
     * Split a full level-2 array into two halves, shifting the rest of L1 down.
     */
    private void splitL2(int index) {
        L2Array l2 = (L2Array) l1Array[index];
        int half = l2.numUsed / 2;

        L2Array newL2 = new L2Array(l2.items.length);

        // copy right half into newL2
        System.arraycopy(l2.items, half, newL2.items, 0, l2.numUsed - half);
        newL2.numUsed = l2.numUsed - half;

        // clear old right-half references
        Arrays.fill(l2.items, half, l2.numUsed, null);
        l2.numUsed = half;

        // shift L1 one step down to make space
        System.arraycopy(l1Array, index + 1, l1Array, index + 2, l1NumUsed - index - 1);
        l1Array[index + 1] = newL2;
        l1NumUsed++;

        if (l1NumUsed == l1Array.length)
            doubleL1();
    }


    /**
     * check if list contains a match
     *
     * @param item the thing we are looking for.
     * @return true if the item is already in the data structure
     */
    public boolean contains(E item) {
        ListLoc loc = findFront(item);
        if (loc.level1Index >= l1NumUsed) return false;
        L2Array l2 = (L2Array) l1Array[loc.level1Index];
        return loc.level2Index < l2.numUsed &&
                comp.compare(l2.items[loc.level2Index], item) == 0;
    }



    /**
     * copy the contents of the RaggedArrayList into the given array
     *
     * @param a - an array of the actual type and of the correct size
     * @return the filled in array
     */
    public E[] toArray(E[] a) {
        int totalSize = size();

        // If passed array is too small, create a new one of the correct type
        if (a.length < totalSize) {
            a = Arrays.copyOf(a, totalSize);
        }

        int pos = 0;
        for (int i = 0; i < l1NumUsed; i++) {
            L2Array l2 = (L2Array) l1Array[i];
            System.arraycopy(l2.items, 0, a, pos, l2.numUsed);
            pos += l2.numUsed;
        }

        // If array is larger than needed, null-terminate
        if (a.length > totalSize) {
            a[totalSize] = null;
        }

        return a;
    }




    /**
     * returns a new independent RaggedArrayList whose elements range from
     * fromElemnt, inclusive, to toElement, exclusive. The original list is
     * unaffected findStart and findEnd will be useful here
     *
     * @param fromElement the starting element
     * @param toElement the element after the last element we actually want
     * @return the sublist
     */
    public RaggedArrayList<E> subList(E fromElement, E toElement) {
        RaggedArrayList<E> result = new RaggedArrayList<>(comp);
        ListLoc start = findFront(fromElement);
        ListLoc end = findFront(toElement);

        for (int i = start.level1Index; i <= end.level1Index; i++) {
            L2Array l2 = (L2Array) l1Array[i];
            int begin = (i == start.level1Index) ? start.level2Index : 0;
            int finish = (i == end.level1Index) ? end.level2Index : l2.numUsed;
            for (int j = begin; j < finish; j++) {
                result.add(l2.items[j]);
            }
        }
        return result;
    }


    /**
     * returns an iterator for this list this method just creates an instance
     * of the inner Itr() class (DONE)
     *
     * @return an iterator
     */
    public Iterator<E> iterator() {
        return new Itr();
    }

    /**
     * Iterator is just a list loc. It starts at (0,0) and finishes with index2
     * 1 past the last item in the last block
     */
    private class Itr implements Iterator<E> {

        private ListLoc loc;

        /*
         * create iterator at start of list
         * (DONE)
         */
        Itr() {
            loc = new ListLoc(0, 0);
        }

        /**
         * check to see if there are more items
         */
        public boolean hasNext() {
            if (loc.level1Index >= l1NumUsed) return false;
            L2Array l2 = (L2Array) l1Array[loc.level1Index];
            if (loc.level2Index < l2.numUsed) return true;
            return loc.level1Index + 1 < l1NumUsed &&
                    ((L2Array) l1Array[loc.level1Index + 1]).numUsed > 0;
        }

        /**
         * return item and move to next throws NoSuchElementException if 
         * off end of list.  An exception is thrown here because calling 
         * next() without calling hasNext() shows a certain level or stupidity
         * on the part of the programmer, so it can blow up. They deserve it.
         */
        public E next() {
            if (!hasNext()) throw new NoSuchElementException();
            L2Array l2 = (L2Array) l1Array[loc.level1Index];
            E item = l2.items[loc.level2Index];
            loc.moveToNext();
            return item;
        }

        /**
         * Remove is not implemented. Just use this code. (DONE)
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }

        /**
         * Add an object after any matching values.
         * Uses findEnd() to get insertion location.
         * Handles shifting, doubling, and splitting as needed.
         *
         * @param item item to insert
         * @return true if successfully added
         */
    }


}
