package edu.yu.introtoalgs;

public class IntersectRectangles 
{

  /** This constant represents the fact that two rectangles don't intersect.
   *
   * @see #intersectRectangle
   * @warn you may not modify this constant in any way
   */
  public final static Rectangle NO_INTERSECTION = new Rectangle(0, 0, -1, -1);

  /** An immutable class that represents a 2D Rectangle.
   *
   * @warn you may not modify the instance variables in any way, you are
   * encouraged to add to the current set of variables and methods as you feel
   * necessary.
   */
  public static class Rectangle 
  {
    // safe to make instance variables public because they are final, now no
    // need to make getters
    public final int x;
    public final int y;
    public final int width;
    public final int height;

    /** Constructor: see the requirements doc for the precise semantics.
     *
     * @warn you may not modify the currently defined semantics in any way, you
     * may add more code if you so choose.
     */
    public Rectangle (final int x, final int y, final int width, final int height)
    {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
    }
    @Override
    public int hashCode()
    {
      int hashCode = this.x^3 + this.y^4 + this.width^5 +this.height^6;
      return hashCode; 
    }

    @Override
    public boolean equals(Object o)
    {
      if(this == o)
      {
        return true;
      }

      if(!(o instanceof Rectangle))
      {
        return false;
      }

      Rectangle r = (Rectangle) o;

      if(this.x != r.x || this.y != r.y || this.height != r.height || this.width != r.width)
      {
        return false;
      }
      return true;
    }
  }

  /** If the two rectangles intersect, returns the rectangle formed by their
   * intersection; otherwise, returns the NO_INTERSECTION Rectangle constant.
   *
   * @param r1 one rectangle
   * @param r2 the other rectangle
   * @param a rectangle representing the intersection of the input parameters
   * if they intersect, NO_INTERSECTION otherwise.  See the requirements doc
   * for precise definition of "rectangle intersection"
   * @throws IllegalArgumentException if either parameter is null.
   */
  public static Rectangle intersect(final Rectangle r1, final Rectangle r2)
  {
    if(r1 == null || r2 == null)
    {
        throw new IllegalArgumentException();
    }
    int x = getX(r1,r2);
    int width = getX2(r1, r2) - x;
    int y = getY(r1, r2);
    int height = getY2(r1, r2) - y;

    Rectangle rectangleIntersect = new Rectangle(x, y, width, height);

    if(isInvalid(rectangleIntersect))
    {
        return NO_INTERSECTION;
    }
    return rectangleIntersect;
    // supply a more useful implementation!
  }
  private static int getY(Rectangle r1, Rectangle r2)
  {
    int y;
    if(r1.y < r2.y)
    {
      y = r2.y;
    }
    else
    {
      y = r1.y;
    }
    return y;
  }

  private static int getX(Rectangle r1, Rectangle r2)
  {
    int x;
    if(r1.x < r2.x)
    {
      x = r2.x;
    }
    else
    {
      x = r1.x;
    }
    return x;
  }
  /* I found it easiest to find all of the positions of the rectangle
  * and then build the smaller intersection rectangle within those points
  * X2 = Height + X(Starting position) and similarly Y2 = Width + Y(Starting position)
  */
  private static int getX2(Rectangle r1, Rectangle r2)
  {
    int width;
    if(r1.width  + r1.x > r2.width + r2.x)
    {
      width = r2.width + r2.x ;
    }
    else
    {
      width = r1.width +r1.x;
    }
    return width;
  }

  private static int getY2(Rectangle r1, Rectangle r2)
  {
    int height;
    if(r1.height + r1.y > r2.height + r2.y)
    {
      height = r2.height + r2.y;
    }
    else
    {
      height = r1.height + r1.y;
    }
    return height;
  }

  private static boolean isInvalid(Rectangle r1)
  {
    if(0 > r1.width  || 0 > r1.height)
    {
      return true;
    }
    return false;
  }
} // class
