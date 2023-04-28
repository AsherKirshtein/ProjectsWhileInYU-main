package edu.yu.introtoalgs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import edu.yu.introtoalgs.IntersectRectangles.Rectangle;

public class IntersectRectanglesTest
{
    @Test
    public void basicIntersectTest()
    {
        final Rectangle D = new Rectangle (0, 0, 2, 5);
        final Rectangle F = new Rectangle (2, 0, 2, 5);

        System.out.println("Basic Test 1:");
        System.out.println("Rectangle 1: [" + D.x + " , " + D.y + " , " + D.width + " , " + D.height + "]");
        System.out.println("Rectangle 2: [" + F.x + " , " + F.y + " , " + F.width + " , " + F.height + "]");

        Rectangle intersection = IntersectRectangles.intersect(D,F);

        System.out.println("\nRectangle Intersection: ["+ intersection.x + " , " + intersection.y + " , " + intersection.width + " , " + intersection.height + "]\n\n");

        assertEquals(2, intersection.x);
        assertEquals(0, intersection.y);
        assertEquals(0, intersection.width);
        assertEquals(5, intersection.height);
    }
    
    @Test
    public void basicIntersectTest2()
    {
        final Rectangle D = new Rectangle (1, 0, 4, 10);
        final Rectangle F = new Rectangle (2, 1, 2, 5);

        System.out.println("Basic Test 2:");
        System.out.println("Rectangle 1: [" + D.x + " , " + D.y + " , " + D.width + " , " + D.height + "]");
        System.out.println("Rectangle 2: [" + F.x + " , " + F.y + " , " + F.width + " , " + F.height + "]");

        Rectangle intersection = IntersectRectangles.intersect(D,F);

        System.out.println("\nRectangle Intersection: ["+ intersection.x + " , " + intersection.y + " , " + intersection.width + " , " + intersection.height + "]\n\n");

        assertEquals(2, intersection.x);
        assertEquals(1, intersection.y);
        assertEquals(2, intersection.width);
        assertEquals(5, intersection.height);
    }
    @Test
    public void basicIntersectTest3()
    {
        final Rectangle D = new Rectangle (0, 0, 10, 10);
        final Rectangle F = new Rectangle (1, 1, 8, 8);

        System.out.println("Basic Test 3:");
        System.out.println("Rectangle 1: [" + D.x + " , " + D.y + " , " + D.width + " , " + D.height + "]");
        System.out.println("Rectangle 2: [" + F.x + " , " + F.y + " , " + F.width + " , " + F.height + "]");

        Rectangle intersection = IntersectRectangles.intersect(D,F);

        System.out.println("\nRectangle Intersection: ["+ intersection.x + " , " + intersection.y + " , " + intersection.width + " , " + intersection.height + "]\n\n");

        assertEquals(1, intersection.x);
        assertEquals(1, intersection.y);
        assertEquals(8, intersection.width);
        assertEquals(8, intersection.height);
    }
    @Test
    public void basicNoIntersectTest()
    {
        final Rectangle D = new Rectangle (0, 0, 0, 0);
        final Rectangle F = new Rectangle (1, 1, 3, 4);

        System.out.println("Basic No IntersectTest:");
        System.out.println("Rectangle 1: [" + D.x + " , " + D.y + " , " + D.width + " , " + D.height + "]");
        System.out.println("Rectangle 2: [" + F.x + " , " + F.y + " , " + F.width + " , " + F.height + "]");

        Rectangle intersection = IntersectRectangles.intersect(D,F);

        System.out.println("\nRectangle Intersection: ["+ intersection.x + " , " + intersection.y + " , " + intersection.width + " , " + intersection.height + "]\n\n");

        assertEquals(0, intersection.x);
        assertEquals(0, intersection.y);
        assertEquals(-1, intersection.width);
        assertEquals(-1, intersection.height);
    } 
    @Test
    public void sameRectangleTest()
    {
        final Rectangle D = new Rectangle (1, 2, 3, 4);
        final Rectangle F = new Rectangle (1, 2, 3, 4);

        System.out.println("Same rectangle Test:");
        System.out.println("Rectangle 1: [" + D.x + " , " + D.y + " , " + D.width + " , " + D.height + "]");
        System.out.println("Rectangle 2: [" + F.x + " , " + F.y + " , " + F.width + " , " + F.height + "]");

        Rectangle intersection = IntersectRectangles.intersect(D,F);

        System.out.println("\nRectangle Intersection: ["+ intersection.x + " , " + intersection.y + " , " + intersection.width + " , " + intersection.height + "]\n\n");

        assertEquals(1, intersection.x);
        assertEquals(2, intersection.y);
        assertEquals(3, intersection.width);
        assertEquals(4, intersection.height);
    } 
    
    @Test
    public void weirdNumbersTest()
    {
        final Rectangle D = new Rectangle (0, 0, 0, 0);
        final Rectangle F = new Rectangle (0, 0, 69, 420);

        System.out.println("weirdNumbersTest:");
        System.out.println("Rectangle 1: [" + D.x + " , " + D.y + " , " + D.width + " , " + D.height + "]");
        System.out.println("Rectangle 2: [" + F.x + " , " + F.y + " , " + F.width + " , " + F.height + "]");

        Rectangle intersection = IntersectRectangles.intersect(D,F);

        System.out.println("\nRectangle Intersection: ["+ intersection.x + " , " + intersection.y + " , " + intersection.width + " , " + intersection.height + "]\n\n");

        assertEquals(0, intersection.x);
        assertEquals(0, intersection.y);
        assertEquals(0, intersection.width);
        assertEquals(0, intersection.height);
    }
    @Test
    public void NegativeXValuesTest()
    {
        final Rectangle D = new Rectangle (-10, 3, 2, 6);
        final Rectangle F = new Rectangle (-10, 3, 3, 4);

        System.out.println("Negative X values Test:");
        System.out.println("Rectangle 1: [" + D.x + " , " + D.y + " , " + D.width + " , " + D.height + "]");
        System.out.println("Rectangle 2: [" + F.x + " , " + F.y + " , " + F.width + " , " + F.height + "]");

        Rectangle intersection = IntersectRectangles.intersect(D,F);

        System.out.println("\nRectangle Intersection: ["+ intersection.x + " , " + intersection.y + " , " + intersection.width + " , " + intersection.height + "]\n\n");

        assertEquals(-10, intersection.x);
        assertEquals(3, intersection.y);
        assertEquals(2, intersection.width);
        assertEquals(4, intersection.height);
    }
    
    @Test
    public void NegativeYValuesTest()
    {
        final Rectangle D = new Rectangle (0, -4, 2, 4);
        final Rectangle F = new Rectangle (0, -2, 2, 2);

        System.out.println("Negative Y Values Test: ");
        System.out.println("Rectangle 1: [" + D.x + " , " + D.y + " , " + D.width + " , " + D.height + "]");
        System.out.println("Rectangle 2: [" + F.x + " , " + F.y + " , " + F.width + " , " + F.height + "]");

        Rectangle intersection = IntersectRectangles.intersect(D,F);

        System.out.println("\nRectangle Intersection: ["+ intersection.x + " , " + intersection.y + " , " + intersection.width + " , " + intersection.height + "]\n\n");

        assertEquals(0, intersection.x);
        assertEquals(-2, intersection.y);
        assertEquals(2, intersection.width);
        assertEquals(2, intersection.height);
    }
    @Test
    public void nullInputTest()
    {
        boolean exception = false;
        Rectangle D = new Rectangle(0, 0, 4, 4);
        Rectangle F = null;
        System.out.println("Throw exception(null rectangle) test: ");
        System.out.println("Rectangle 1: [" + D.x + " , " + D.y + " , " + D.width + " , " + D.height + "]");
        System.out.println("Rectangle 2: null rectangle");

        try
        {
            IntersectRectangles.intersect(D,F);
        }
        catch(IllegalArgumentException e)
        {
            exception = true;
        }
        assertEquals(true, exception);
        System.out.println("Illegal Argument Exception Thrown \n\n");

    }
    @Test
    public void equalsTest()
    {
        System.out.println("\nEquals Test:\n");
        final Rectangle D = new Rectangle (2, -4, 2, 4);
        final Rectangle F = new Rectangle (1, -2, 2, 4);

        System.out.println("Don't Equal Test: \n");
        System.out.println("Rectangle 1: [" + D.x + " , " + D.y + " , " + D.width + " , " + D.height + "]");
        System.out.println("Rectangle 2: [" + F.x + " , " + F.y + " , " + F.width + " , " + F.height + "]\n");

        assertEquals(false, D.equals(F));

        final Rectangle A = new Rectangle (2, -4, 2, 4);
        final Rectangle B = new Rectangle (2, -4, 2, 4);

        System.out.println("Negative Y Values Test: \n");
        System.out.println("Rectangle 1: [" + A.x + " , " + A.y + " , " + A.width + " , " + A.height + "]");
        System.out.println("Rectangle 2: [" + B.x + " , " + B.y + " , " + B.width + " , " + B.height + "]\n");

        assertEquals(true, A.equals(B));
    }
    
    @Test
    public void hashCodeTest()
    {
        System.out.println("HashCode Test \n");
        final Rectangle D = new Rectangle (2, -4, 2, 4);
        final Rectangle F = new Rectangle (1, -2, 2, 4);

        System.out.println("Don't Equal Test: ");
        System.out.println("Rectangle 1: [" + D.x + " , " + D.y + " , " + D.width + " , " + D.height + "]");
        System.out.println("Rectangle 2: [" + F.x + " , " + F.y + " , " + F.width + " , " + F.height + "]");

        System.out.println("Rectangle 1 hashcode: " + D.hashCode());
        System.out.println("Rectangle 2 hashcode: " + F.hashCode());

        assertNotEquals(D.hashCode(), F.hashCode());

        final Rectangle A = new Rectangle (2, -4, 2, 4);
        final Rectangle B = new Rectangle (2, -4, 2, 4);

        System.out.println("Negative Y Values Test: ");
        System.out.println("Rectangle 1: [" + A.x + " , " + A.y + " , " + A.width + " , " + A.height + "]");
        System.out.println("Rectangle 2: [" + B.x + " , " + B.y + " , " + B.width + " , " + B.height + "]");

        System.out.println("Rectangle 1 Hashcode: " + A.hashCode());
        System.out.println("Rectangle 2 Hashcode: " + B.hashCode());

        assertEquals(A.hashCode(), B.hashCode());
    }       
    
}
