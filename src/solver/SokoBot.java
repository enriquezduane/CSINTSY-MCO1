package solver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

class Node {
  int playerX, playerY;
  List<Point> cratePositions;
  int f, g, h;
  Node parent;

  Node(int playerX, int playerY, List<Point> cratePositions) {
    this.playerX = playerX;
    this.playerY = playerY;
    this.cratePositions = cratePositions;
    this.f = 0;
    this.g = 0;
    this.h = 0;
    this.parent = null;
  }

  public void printPosition() {
    System.out.println("( " + playerX + "," + playerY + " )");
  }
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
    Node startNode = initializeStartNode(itemsData);
    openList.add(startNode);
    startNode.printPosition();

    return "udlr";
  }

  public Node initializeStartNode(char[][] itemsData) {
    int playerX = -1, playerY = -1;
    List<Point> cratePositions = new ArrayList<>();

    for (int y = 0; y < itemsData.length; y++) {
      for (int x = 0; x < itemsData[y].length; x++) {
        if (itemsData[y][x] == '@') {
          playerX = x;
          playerY = y;
        } else if (itemsData[y][x] == '$') {
          cratePositions.add(new Point(x, y));
        }
      }
    }
    return new Node(playerX, playerY, cratePositions);
  }
}
