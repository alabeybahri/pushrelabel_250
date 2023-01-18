import java.util.ArrayList;
import java.util.HashMap;

public class Node {
    int pointer = 0;
    int ID;
    int height;
    int excessFlow;
    boolean isBag = false;
    boolean isActive = false;
    HashMap<Node,Edge> adjacentEdges = new HashMap<>();
    ArrayList<Node> currentArc = new ArrayList<>();

    public void addEdge(Node node,Edge edge){
        adjacentEdges.put(node, edge);
        currentArc.add(node);
    }

    public Node(int height,int excessFlow) {
        this.height = height;
        this.excessFlow = excessFlow;
    }
    public Node(){
    }


}
