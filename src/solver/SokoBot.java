package solver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

class Node {
  int playerX, playerY;
  HashSet<Point> cratePositions;
  Node parent;

  Node(int playerX, int playerY, HashSet<Point> cratePositions) {
    this.playerX = playerX;
    this.playerY = playerY;
    this.cratePositions = cratePositions;
    this.parent = null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true; // check if same instance
    if (o == null || getClass() != o.getClass())
      return false;

    Node node = (Node) o;

    if (playerX != node.playerX)
      return false;
    if (playerY != node.playerY)
      return false;

    // Here we're comparing the crate positions lists.
    // This assumes the crate positions in both lists are in the same order.
    return cratePositions.equals(node.cratePositions);
  }

  @Override
  public int hashCode() {
    int result = playerX;
    result = 31 * result + playerY;
    result = 31 * result + cratePositions.hashCode();
    return result;
  }
}

class Point {
  int x, y;

  Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Point point = (Point) o;
    return x == point.x && y == point.y;
  }

  @Override
  public int hashCode() {
    return 31 * x + y;
  }
}

public class SokoBot {

  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
    Queue<Node> queue = new LinkedList<>();
    Set<Node> visited = new HashSet<>();

    Node startNode = initializeStartNode(itemsData);
    queue.add(startNode);

    int counter = 0;
    while (!queue.isEmpty()) {
      Node currentNode = queue.poll();
      System.out.println("curr x: " + currentNode.playerX + " curr y: " + currentNode.playerY);

      if (isGoalState(currentNode, mapData)) {
        System.out.println("count: " + counter);
        return reconstructPath(currentNode);
      }

      visited.add(currentNode);

      for (Node neighbor : getNeighbors(currentNode, mapData, itemsData)) {
        if (!visited.contains(neighbor)) {
          queue.add(neighbor);
          visited.add(neighbor);
        }
      }
      counter++;
    }

    return "lrlrlrlrlrlrlrlrlrlrlrlrlrrllrrllrrllrlrlrllrlrllrrl";
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
    HashSet<Point> cratePositions = new HashSet<>();

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

        HashSet<Point> newCratePositions = new HashSet<>(currentNode.cratePositions);
        boolean isCrateMoved = false;
        List<Point> cratesList = new ArrayList<>(newCratePositions);

        for (Point crate : cratesList) {
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

  private boolean crateCollision(int x, int y, HashSet<Point> cratePositions) {
    return cratePositions.contains(new Point(x, y));
  }
}
