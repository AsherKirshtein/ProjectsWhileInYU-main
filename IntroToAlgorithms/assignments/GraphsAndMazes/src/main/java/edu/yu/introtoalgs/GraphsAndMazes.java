
package edu.yu.introtoalgs;

import java.lang.Thread.State;
import java.util.*;

public class GraphsAndMazes 
{

  /** Given a maze (specified by a 2D integer array, and start and end
   * Coordinate instances), return a path (beginning with the start
   * coordinate, and terminating wih the end coordinate), that legally
   * traverses the maze from the start to end coordinates.  If no such
   * path exists, returns an empty list.  The path need need not be a
   * "shortest path".
   *
   * @param maze 2D int array whose "0" entries are interpreted as
   * "coordinates that can be navigated to in a maze traversal (can be
   * part of a maze path)" and "1" entries are interpreted as
   * "coordinates that cannot be navigated to (part of a maze wall)".
   * @param start maze navigation must begin here, must have a value
   * of "0"
   * @param end maze navigation must terminate here, must have a value
   * of "0"
   * @return a path, beginning with the start coordinate, terminating
   * with the end coordinate, and intervening elements represent a
   * legal navigation from maze start to maze end.  If no such path
   * exists, returns an empty list.  A legal navigation may only
   * traverse maze coordinates, may not contain coordinates whose
   * value is "1", may only traverse from a coordinate to one of its
   * immediate neighbors using one of the standard four compass
   * directions (no diagonal movement allowed).  A legal path may not
   * contain a cycle.  It is legal for a path to contain only the
   * start coordinate, if the start coordinate is equal to the end
   * coordinate.
   */
  private static boolean[][] visited;
  private static int currentX;
  private static int currentY;
  private static int [][] maze1;
  private static Stack<Coordinate> path;
  private static Coordinate finish;
  private static Coordinate beginning;
  private static int bottomRow;
  private static int finalColumn;

  
  public static List<Coordinate> searchMaze(final int[][] maze, final Coordinate start, final Coordinate end)
  {   
      int moves = 0;
      maze1 = maze;
      beginning = start;
      currentX = start.x;
      currentY = start.y;
      bottomRow = maze.length - 1;
      finalColumn = maze[0].length - 1;
      visited = new boolean[bottomRow+1][finalColumn+1];
      path = new Stack<>();
      path.push(start);
      finish = end;
      visited[start.x][start.y] = true;
      if(maze[start.x][start.y] == 1)
      {
        return new ArrayList<>();
      }

      Coordinate c = new Coordinate(currentX, currentY);
      while(!c.equals(finish))
      {
          move();
          moves++;
          if(path.empty())
          {
             return new ArrayList<>();
          }
          visited[currentX][currentY] = true;
          c = new Coordinate(currentX, currentY);
          path.push(c);
      }
      //System.out.println("\nmoves: "+ moves);
      return getList();
  }

  private static void move()
  {
      String direction = getDirection();
      switch(direction)
      {
         case("up"): currentX--;
         break;
         case("down"): currentX++;
         break;
         case("right"): currentY++;
         break;
         case("left"): currentY--;
         break;
         case("none"): goBack();
      }
  }
  private static List<Coordinate> getList()
  {
      List<Coordinate> finalPath = new ArrayList<>(path);
      if(finalPath.size() > 2)
      {
        if(finalPath.get(0).equals(finalPath.get(1)))
        {
          finalPath.remove(0);
        }
      }
      return finalPath; 
  }
  private static void goBack()
  {
    //System.out.println("go back"); 
    while(!path.empty())
     {
        Coordinate c = path.pop();
        currentX = c.x;
        currentY = c.y;
        if(getDirection() != "none")
        {
          if(c.equals(beginning))
          {
            path.push(c);
          }
          return;
        }
     }
     return;
  }

  private static String getDirection()
  {
     String direction = "none";
     if(currentX > 0) // try to move up
     {
       if(maze1[currentX-1][currentY] == 0)
       {
          if(!visited[currentX-1][currentY])
          {
             direction = "up";
             return direction;
          }
       }
     }
      if(currentX < bottomRow) // try to move down
      {
        if(maze1[currentX+1][currentY] == 0)
        {
            if(!visited[currentX+1][currentY])
            {
              direction = "down";
              return direction;
            }
        }
      }

      if(currentY > 0)
      {
        if(maze1[currentX][currentY-1] == 0)
        {
            if(!visited[currentX][currentY-1])
            {
              direction = "left";
              return direction;
            }
        }
      }

      if(currentY < finalColumn)
      {
        if(maze1[currentX][currentY+1] == 0)
        {
            if(!visited[currentX][currentY+1])
            {
              direction = "right";
              return direction;
            }
        }
      }
      return direction;
  }
  
  public static void main (final String[] args) 
  {
    final int[][] exampleMaze = 
    {
      {0, 0, 0},
      {0, 1, 1},
      {0, 1, 0}
    };

    final Coordinate start = new Coordinate(2, 0);
    final Coordinate end = new Coordinate(0, 2);
    final List<Coordinate> path = searchMaze(exampleMaze, start, end);
    System.out.println("path = " + path.toString());
  }

  /** A immutable coordinate in 2D space.
   *
   * Students must NOT modify the constructor (or its semantics) in any way,
   * but can ADD whatever they choose.
   */
 
  public static class Coordinate 
  { 
    public final int x, y;
    
    /** Constructor, defines an immutable coordinate in 2D space.
     *
     * @param x specifies x coordinate
     * @param y specifies x coordinate
     */
    public Coordinate(final int x, final int y) 
    {
      this.x = x;
      this.y = y;
    }
    @Override
    public String toString()
    {
      return "(" + this.x + "," + this.y + ")";
    }

    @Override
    public boolean equals(Object obj)
    {
      if(!(obj instanceof Coordinate))
      {
        return false;
      }
      Coordinate compareTo = (Coordinate) obj;
      if(obj == this)
      {
        return true;
      }
      if(compareTo.x == this.x && compareTo.y == this.y)
      {
        return true;
      }
      return false;
    }

      @Override
      public int hashCode()
      {
         return (x+69)*7 + (y+27)*3;
      }

   }

}