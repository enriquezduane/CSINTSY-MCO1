package solver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

class Node {
  int playerX, playerY;
  List<Point> cratePositions;
  Node parent;

  Node(int playerX, int playerY, List<Point> cratePositions) {
    this.playerX = playerX;
    this.playerY = playerY;
    this.cratePositions = cratePositions;
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
    Queue<Node> queue = new LinkedList<>();
    Set<Node> visited = new HashSet<>();

    Node startNode = initializeStartNode(itemsData);
    queue.add(startNode);

    int count = 0;

    while (!queue.isEmpty()) {
      Node currentNode = queue.poll();
      System.out.println("curr x: " + currentNode.playerX + " curr y: " + currentNode.playerY);

      if (isGoalState(currentNode, mapData)) {
        System.out.println("States generated: " + count);
        return reconstructPath(currentNode);
      }

      visited.add(currentNode);

      for (Node neighbor : getNeighbors(currentNode, mapData, itemsData)) {
        if (!visited.contains(neighbor)) {
          queue.add(neighbor);
          visited.add(neighbor);
        }
      }
      count++;
    }

    return "lrlrlrlrlrlrlrlr";
  }

  public List<Point> initializeGoals(char[][] mapData) {
    List<Point> goals = new ArrayList<>();
    for (int y = 0; y < mapData.length; y++) {
      for (int x = 0; x < mapData[y].length; x++) {
        if (mapData[y][x] == '.') {
          goals.add(new Point(x, y));
        }
      }
    }
    return goals;
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

  public boolean isGoalState(Node node, char[][] mapData) {
    // Check if all crates are on target positions

    for (Point crate : node.cratePositions) {
      int crateX = crate.x;
      int crateY = crate.y;

      // If the crate is not on a target position, it's not a goal state
      if (mapData[crateY][crateX] != '.') {
        return false;
      }
      // return true;
    }

    // All crates are on target positions, it's a goal state
    return true;
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

  public List<Node> getNeighbors(Node currentNode, char[][] mapData, char[][] itemsData) {
    List<Node> neighbors = new ArrayList<>();
    int playerX = currentNode.playerX;
    int playerY = currentNode.playerY;

    // Possible movements: up, down, left, right
    int[] dX = { 0, 0, -1, 1 };
    int[] dY = { -1, 1, 0, 0 };

    for (int i = 0; i < 4; i++) {
      int newX = playerX + dX[i];
      int newY = playerY + dY[i];

      // Check if the new position is within bounds and is not a wall
      if (newX >= 0 && newX < mapData[0].length && newY >= 0 && newY < mapData.length &&
          mapData[newY][newX] != '#') {

        List<Point> newCratePositions = new ArrayList<>(currentNode.cratePositions);
        boolean isCrateMoved = false;

        for (Point crate : new ArrayList<>(newCratePositions)) { // Iterate over a new list to avoid
                                                                 // ConcurrentModificationException
          if (crate.x == newX && crate.y == newY) {
            int newCrateX = crate.x + dX[i];
            int newCrateY = crate.y + dY[i];

            if (newCrateX >= 0 && newCrateX < mapData[0].length &&
                newCrateY >= 0 && newCrateY < mapData.length &&
                mapData[newCrateY][newCrateX] != '#' &&
                !crateCollision(newCrateX, newCrateY, newCratePositions)) {

              newCratePositions.remove(crate);
              newCratePositions.add(new Point(newCrateX, newCrateY));
              isCrateMoved = true;
            } else {
              // Unable to move crate, skip this neighbor
              isCrateMoved = false;
              break;
            }
          }
        }

        // Add neighbor node if valid
        if (isCrateMoved || !crateCollision(newX, newY, newCratePositions)) {
          Node neighborNode = new Node(newX, newY, newCratePositions);
          neighborNode.parent = currentNode;
          neighbors.add(neighborNode);
        }
      }
    }
    return neighbors;
  }

  private boolean crateCollision(int x, int y, List<Point> cratePositions) {
    for (Point crate : cratePositions) {
      if (crate.x == x && crate.y == y) {
        return true;
      }
    }
    return false;
  }
}
