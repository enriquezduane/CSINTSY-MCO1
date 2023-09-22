package solver;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

class Node {
  int playerX, playerY;
  List<Point> cratePositions;
  int g, h, f;
  Node parent;
}

class Point {
  int x, y;

  Point(int x, int y) {
    this.x = x;
    this.y = y;
  }
}

public class SokoBot {

  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
    PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingInt(node -> node.f));
    try {
      Thread.sleep(3000);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return "udlr";
  }
}
