package solver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

class Node {
  int playerX, playerY;
  List<Point> cratePositions;
  int g, h, f;
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
    PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(node -> node.f));
    Set<Node> closedSet = new HashSet<>();

    while (!openSet.isEmpty()) {
      Node currentNode = openSet.poll();

      if (isGoalState(currentNode)) {
        // We've found the goal state, so return the path to get there
        return reconstructPath(currentNode);
      }
    }

    try {
      Thread.sleep(3000);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
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

  public boolean isGoalState(Node node) {
    // In this example, we're not checking for the actual goal state, so always return false
    return false;
  }

  public String reconstructPath(Node node) {
    // Reconstruct the path from the start node to the current node
    StringBuilder path = new StringBuilder();
    while (node.parent != null) {
        if (node.playerX < node.parent.playerX) {
            path.insert(0, 'l');
        } else if (node.playerX > node.parent.playerX) {
            path.insert(0, 'r');
        } else if (node.playerY < node.parent.playerY) {
            path.insert(0, 'u');
        } else if (node.playerY > node.parent.playerY) {
            path.insert(0, 'd');
        }
        node = node.parent;
    }
    return path.toString();
  }
}
