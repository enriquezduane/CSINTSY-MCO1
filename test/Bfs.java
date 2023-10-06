import java.util.LinkedList;
import java.util.Queue;

public class Bfs {
  static final int ROW = 4;
  static final int COL = 4;

  // up, right, down, left
  static int dRow[] = { -1, 0, 1, 0 };
  static int dCol[] = { 0, 1, 0, -1 };

  public static boolean isValid(boolean vis[][], int row, int col) {
    // if cell is out of bounds
    if (row < 0 || col < 0 || row >= ROW || col >= COL) {
      return false;
    }

    // if cell is already visited
    if (vis[row][col]) {
      return false;
    }
    return true;
  }

  public static void BFS(int grid[][], boolean vis[][], int startX, int startY, int endX, int endY) {
    Queue<Coordinate> q = new LinkedList<>();
    // row and col are the starting node
    q.add(new Coordinate(startX, startY));

    // keep track of parents for path reconstruction
    int parentX[][] = new int[ROW][COL];
    int parentY[][] = new int[ROW][COL];

    // mark it as visited
    vis[startX][startY] = true;

    while (!(q.isEmpty())) {
      // get current coordinate
      Coordinate curr = q.peek();

      // get each coordinate indexes
      int x = curr.x;
      int y = curr.y;

      // stop when destination is reached
      if (x == endX && y == endY) {
        break;
      }

      // remove from queue
      q.remove();

      // find neighboring cells
      for (int i = 0; i < 4; i++) {
        int adjX = x + dRow[i];
        int adjY = y + dCol[i];

        if (isValid(vis, adjX, adjY)) {
          q.add(new Coordinate(adjX, adjY));
          vis[adjX][adjY] = true;

          // store parent of adjacent cell
          parentX[adjX][adjY] = x;
          parentY[adjX][adjY] = y;
        }
      }
    }
    reconstructPath(grid, parentX, parentY, startX, startY, endX, endY);
  }

  public static void reconstructPath(int[][] grid, int[][] parentX, int[][] parentY, int startX, int startY, int endX,
      int endY) {
    int x = endX;
    int y = endY;

    System.out.println("shortest path:");

    // loop until it hasn't reached the start coordinates
    while (x != startX || y != startY) {
      System.out.print(grid[x][y] + " -> ");
      int tempX = parentX[x][y];
      y = parentY[x][y];
      x = tempX;
    }
    System.out.println(grid[startX][startY]);
  }

  public static void main(String[] args) {

    // Given input matrix
    int grid[][] = { { 1, 2, 3, 4 },
        { 5, 6, 7, 8 },
        { 9, 10, 11, 12 },
        { 13, 14, 15, 16 } };

    /*
     * 1 2 3 4
     * 5 6 7 8
     * 9 10 11 12
     * 13 14 15 16
     */

    // Declare the visited array
    boolean[][] vis = new boolean[ROW][COL];

    BFS(grid, vis, 0, 0, 2, 2);
  }
}