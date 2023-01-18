import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;

public class Graph {
    
    ArrayList<Node> trainGreen = new ArrayList<>();
    ArrayList<Node> trainRed = new ArrayList<>();
    ArrayList<Node> reindeerGreen = new ArrayList<>();
    ArrayList<Node> reindeerRed = new ArrayList<>();
    ArrayDeque<Node> activeNodes = new ArrayDeque<>();
    HashSet<Node> vehicleNodes = new HashSet<>();

    Node sourceNode = new Node();
    Node sinkNode = new Node(0,0);
    Node greenRegion = new Node(1,0);
    Node redRegion = new Node(1,0);
}
