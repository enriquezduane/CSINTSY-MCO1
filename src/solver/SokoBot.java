package solver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;

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
    return Objects.hash(x, y);
  }
}

public class SokoBot {
  private static final int[][] DIRECTIONS = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } }; // right, down, left, up

  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
    PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingInt(node -> node.f));
    Set<Node> closedSet = new HashSet<>();

    Node startNode = initializeStartNode(itemsData);
    List<Point> goals = initializeGoals(mapData);

    startNode.h = calculateHeuristic(startNode, goals);
    startNode.f = startNode.g + startNode.h;

    openList.add(startNode);

    while (!openList.isEmpty()) {
      Node currentNode = openList.poll(); // node with lowest f
      if (isGoalState(currentNode, goals)) {
        return reconstructPath(currentNode); // you need to implement this to reconstruct the path from start to goal
      }

      closedSet.add(currentNode);

      List<Node> neighbors = generateNeighbors(currentNode, mapData, itemsData);
      for (Node neighbor : neighbors) {
        if (closedSet.contains(neighbor)) {
          continue;
        }

        int tentativeG = currentNode.g + 1; // consider the cost to move as 1 for simplicity

        if (tentativeG < neighbor.g || !openList.contains(neighbor)) {
          neighbor.g = tentativeG;
          neighbor.h = calculateHeuristic(neighbor, goals);
          neighbor.f = neighbor.g + neighbor.h;
          neighbor.parent = currentNode;

          if (!openList.contains(neighbor)) {
            openList.add(neighbor);
          }
        }
      }
    }

    return "";
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

  public int calculateHeuristic(Node node, List<Point> goals) {
    int totalManhattanDistance = 0;
    for (Point crate : node.cratePositions) {
      int minDistance = Integer.MAX_VALUE;
      for (Point goal : goals) {
        int distance = Math.abs(crate.x - goal.x) + Math.abs(crate.y - goal.y);
        minDistance = Math.min(minDistance, distance);
      }
      totalManhattanDistance += minDistance;
    }
    return totalManhattanDistance;
  }

  public List<Node> generateNeighbors(Node node, char[][] mapData, char[][] itemsData) {
    List<Node> neighbors = new ArrayList<>();

    for (int[] dir : DIRECTIONS) {
      int newPlayerX = node.playerX + dir[0];
      int newPlayerY = node.playerY + dir[1];

      if (isValidMove(newPlayerX, newPlayerY, mapData, itemsData, node.cratePositions)) {
        List<Point> newCratePositions = new ArrayList<>(node.cratePositions);

        Point pushedCrate = getPushedCrate(newPlayerX, newPlayerY, newCratePositions);
        if (pushedCrate != null) {
          pushedCrate.x += dir[0];
          pushedCrate.y += dir[1];
        }

        if (isCrateMoveValid(pushedCrate, mapData, newCratePositions)) {
          neighbors.add(new Node(newPlayerX, newPlayerY, newCratePositions));
        }
      }
    }

    return neighbors;
  }

  private boolean isValidMove(int x, int y, char[][] mapData, char[][] itemsData, List<Point> cratePositions) {
    if (x < 0 || y < 0 || y >= mapData.length || x >= mapData[y].length || mapData[y][x] == '#') {
      return false;
    }

    Point point = new Point(x, y);
    return !cratePositions.contains(point);
  }

  private Point getPushedCrate(int x, int y, List<Point> cratePositions) {
    for (Point crate : cratePositions) {
      if (crate.x == x && crate.y == y) {
        return crate;
      }
    }
    return null;
  }

  private boolean isCrateMoveValid(Point crate, char[][] mapData, List<Point> cratePositions) {
    if (crate == null)
      return true;

    if (crate.x < 0 || crate.y < 0 || crate.y >= mapData.length || crate.x >= mapData[crate.y].length
        || mapData[crate.y][crate.x] == '#') {
      return false;
    }

    for (Point otherCrate : cratePositions) {
      if (otherCrate.x == crate.x && otherCrate.y == crate.y) {
        return false;
      }
    }

    return true;
  }

  public boolean isCrateBlocked(Point crate, char[][] mapData, List<Point> cratePositions) {
    int crateX = crate.x;
    int crateY = crate.y;
    for (int i = 0; i < DIRECTIONS.length; i++) {
      int[] dir = DIRECTIONS[i];
      int adjX = crateX + dir[0];
      int adjY = crateY + dir[1];

      // Check if adjacent tile in this direction is within bounds and not a wall
      if (adjX >= 0 && adjY >= 0 && adjY < mapData.length && adjX < mapData[adjY].length
          && mapData[adjY][adjX] != '#') {
        continue; // if it's not a wall, check next direction
      }

      // Find the orthogonal directions
      int[] orthoDir1 = DIRECTIONS[(i + 1) % 4];
      int[] orthoDir2 = DIRECTIONS[(i + 3) % 4];

      boolean isBlockedOrtho1 = isBlockedOrthogonal(crateX, crateY, orthoDir1, mapData, cratePositions);
      boolean isBlockedOrtho2 = isBlockedOrthogonal(crateX, crateY, orthoDir2, mapData, cratePositions);

      if (isBlockedOrtho1 && isBlockedOrtho2) {
        return true; // The crate is blocked in this direction
      }
    }
    return false; // The crate is not blocked in any direction
  }

  public boolean isBlockedOrthogonal(int x, int y, int[] dir, char[][] mapData, List<Point> cratePositions) {
    int adjX = x + dir[0];
    int adjY = y + dir[1];

    if (adjX < 0 || adjY < 0 || adjY >= mapData.length || adjX >= mapData[adjY].length) {
      return true; // Out of bounds is considered as blocked
    }

    char tile = mapData[adjY][adjX];
    Point point = new Point(adjX, adjY);

    return tile == '#' || cratePositions.contains(point);
  }

  public boolean isGoalState(Node node, List<Point> goals) {
    Set<Point> goalSet = new HashSet<>(goals);
    Set<Point> crateSet = new HashSet<>(node.cratePositions);
    return goalSet.equals(crateSet);
  }

  public String reconstructPath(Node goalNode) {
    StringBuilder path = new StringBuilder();
    Node currentNode = goalNode;
    while (currentNode.parent != null) {
      Node parentNode = currentNode.parent;
      int dx = currentNode.playerX - parentNode.playerX;
      int dy = currentNode.playerY - parentNode.playerY;
      String move = getMoveFromDelta(dx, dy);
      path.insert(0, move); // Insert at the beginning to reverse the path
      currentNode = parentNode;
    }
    return path.toString();
  }

  public String getMoveFromDelta(int dx, int dy) {
    if (dx == 1 && dy == 0)
      return "r";
    if (dx == -1 && dy == 0)
      return "l";
    if (dx == 0 && dy == 1)
      return "d";
    if (dx == 0 && dy == -1)
      return "u";
    return ""; // Or throw an exception for invalid delta
  }
}