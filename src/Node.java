//M.W.C Umesha- 20221519-w1956430
public class Node implements Comparable<Node> {
    int row;
    int col;
    int gCost; // weight/g cost
    int hCost; // Heuristic value
    Node parent; // Parent node
    Node(int row, int col, int gCost, int hCost) {
        this.row = row;
        this.col = col;
        this.gCost = gCost;
        this.hCost = hCost;
    }
    int fCost() {
        return gCost + hCost;
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.fCost(), other.fCost());
    }
}
