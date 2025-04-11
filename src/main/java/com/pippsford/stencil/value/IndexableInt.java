package com.pippsford.stencil.value;

/**
 * An indexable value that works like a FOR loop counter.
 *
 * @author Simon Greatrix on 21/02/2021.
 */
public class IndexableInt implements Indexable<Integer> {

  private final int end;

  private final int size;

  private final int start;

  private final int step;


  /**
   * Create a new instance.
   *
   * @param start the first int returned (inclusive)
   * @param end   the last int returned (exclusive)
   * @param step  the step size (can be negative, but not zero)
   */
  public IndexableInt(int start, int end, int step) {
    this.start = start;
    this.end = end;
    this.step = step;
    if (step == 0) {
      throw new IllegalArgumentException("Step must not be zero");
    }
    int range = end - start;
    int t = (step > 0) ? step - 1 : step + 1;
    size = Math.max((range + t) / step, 0);
  }


  /**
   * Create a new instance. The step size will be either +1 or -1 depending upon the order of start and end.
   *
   * @param start the first int returned (inclusive)
   * @param end   the last int returned (exclusive)
   */
  public IndexableInt(int start, int end) {
    this(start, end, (end >= start) ? 1 : -1);
  }


  @Override
  public Integer apply(int index) {
    if (index < 0 || size <= index) {
      throw new IndexOutOfBoundsException(index);
    }
    return start + step * index;
  }


  public int getEnd() {
    return end;
  }


  public int getStart() {
    return start;
  }


  public int getStep() {
    return step;
  }


  public int size() {
    return size;
  }

}
