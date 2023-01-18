import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;



public class project4main {
    public static void main(String[] args) throws Exception {
        Scanner scan = new Scanner(Paths.get(args[0]));
        BufferedWriter write = Files.newBufferedWriter(Paths.get(args[1]));
        Graph graph = new Graph();
        directRegionsToSink(graph);
        int vehicleNumber = scanTheVehicles(scan, graph);
        HashMap<String,Integer> bagsWithoutLetterA = new HashMap<String,Integer>();
        int bagNumber = scanTheBags(scan, graph, bagsWithoutLetterA);
        bagsWithoutLetterA.clear();
        PushRelabel.saturateBags(graph,(vehicleNumber+bagNumber+4)*10);
        PushRelabel.run(graph);
        scan.close();
        write.write(String.valueOf(totalBagCapacities-graph.sinkNode.excessFlow));
        write.close();

    }
    static int totalBagCapacities = 0;

    private static Integer scanTheBags(Scanner scan, Graph graph, HashMap<String, Integer> bagsWithoutLetterA) {
        int numberOfBags = scan.nextInt();
        for (int i = 0; i < numberOfBags; i++) {
            String bagType = scan.next().trim();
            int bagCapacity = scan.nextInt();
            if(bagCapacity==0){
                continue;
            }
            if(bagType.charAt(0)!='a'){
                if(bagsWithoutLetterA.keySet().contains(bagType)){
                    bagsWithoutLetterA.replace(bagType, bagsWithoutLetterA.get(bagType)+bagCapacity);
                }
                else{
                    bagsWithoutLetterA.put(bagType, bagCapacity);
                }
            }
            else{
                Node bagNode = new Node(3,0);
                bagNode.isBag = true;
                totalBagCapacities += bagCapacity;
                directSourceToBags(graph, bagNode, bagCapacity);
                directBagsToVehicles(graph, bagNode, bagCapacity, bagType);
                }
            }
        
        for (Map.Entry<String, Integer> entry : bagsWithoutLetterA.entrySet()) {
            String bagType = entry.getKey();
            Integer capacity = entry.getValue();
            Node bagNode = new Node(3,0);
            bagNode.isBag = true;
            totalBagCapacities += capacity;
            directSourceToBags(graph, bagNode, capacity);
            directBagsToVehicles(graph, bagNode, capacity, bagType);
        }
        graph.trainGreen.clear();graph.trainRed.clear();graph.reindeerGreen.clear();graph.reindeerGreen.clear();

        return numberOfBags;
    }

    private static Integer scanTheVehicles(Scanner scan, Graph graph) {
        int greenRegionTrainNumber = scan.nextInt();
        if(greenRegionTrainNumber==0){
            scan.nextLine();
        }
        for (int i = 0; i < greenRegionTrainNumber; i++) {
            int capacity = scan.nextInt();
            Node greenRegionTrainNode = new Node(2,0);
            directVehiclesToGreenRegion(graph, greenRegionTrainNode, capacity);
            graph.trainGreen.add(greenRegionTrainNode);
            graph.vehicleNodes.add(greenRegionTrainNode);
            
        }
        int redRegionTrainNumber = scan.nextInt();
        if(redRegionTrainNumber==0){
            scan.nextLine();
        }
        for (int i = 0; i < redRegionTrainNumber; i++) {
            int capacity = Integer.parseInt(scan.next());
            Node redRegionTrainNode = new Node(2,0);
            directVehiclesToRedRegion(graph, redRegionTrainNode, capacity);
            graph.trainRed.add(redRegionTrainNode);
            graph.vehicleNodes.add(redRegionTrainNode);
        }
        int greenRegionReindeerNumber = scan.nextInt();
        if(greenRegionReindeerNumber==0){
            scan.nextLine();
        }
        for (int i = 0; i < greenRegionReindeerNumber; i++) {
            int capacity = Integer.parseInt(scan.next());
            Node greenRegionReindeerNode = new Node(2,0);
            directVehiclesToGreenRegion(graph, greenRegionReindeerNode, capacity);
            graph.reindeerGreen.add(greenRegionReindeerNode);
            graph.vehicleNodes.add(greenRegionReindeerNode);
        }
        int redRegionReindeerNumber = scan.nextInt();
        if(redRegionReindeerNumber==0){
            scan.nextLine();
        }
        for (int i = 0; i < redRegionReindeerNumber; i++) {
            int capacity = Integer.parseInt(scan.next());
            Node redRegionReindeerNode = new Node(2,0);
            directVehiclesToRedRegion(graph, redRegionReindeerNode, capacity);
            graph.reindeerRed.add(redRegionReindeerNode);
            graph.vehicleNodes.add(redRegionReindeerNode);
        }
        return (greenRegionReindeerNumber+redRegionReindeerNumber+greenRegionTrainNumber+redRegionTrainNumber);
    }

    public static void directRegionsToSink(Graph graph){
        graph.greenRegion.addEdge(graph.sinkNode,new Edge(Integer.MAX_VALUE,graph.greenRegion,graph.sinkNode));
        graph.redRegion.addEdge(graph.sinkNode,new Edge(Integer.MAX_VALUE, graph.redRegion, graph.sinkNode));
        graph.sinkNode.addEdge(graph.greenRegion, new Edge(0, graph.sinkNode, graph.greenRegion));
        graph.sinkNode.addEdge(graph.redRegion, new Edge(0, graph.sinkNode, graph.redRegion));

    }

    public static void directVehiclesToRedRegion(Graph graph,Node node,int capacity){
        node.addEdge(graph.redRegion,new Edge(capacity, node, graph.redRegion));
        graph.redRegion.addEdge(node,new Edge(0, graph.redRegion, node));
    }

    public static void directVehiclesToGreenRegion(Graph graph,Node node,int capacity){
        node.addEdge(graph.greenRegion,new Edge(capacity, node, graph.greenRegion));
        graph.greenRegion.addEdge(node,new Edge(0, graph.greenRegion, node));
    }
    
    public static void directSourceToBags(Graph graph,Node node,int capacity){
        graph.sourceNode.addEdge(node,new Edge(capacity, graph.sourceNode, node));
        node.addEdge(graph.sourceNode,new Edge(0, node, graph.sourceNode));
    }

    public static void directBagsToVehicles(Graph graph,Node node,int capacity,String bagType){
        boolean isTypeContainsA = bagType.contains("a");
        ArrayList<Node> vehiclesToIterate = new ArrayList<>();
        if(bagType.equals("abd")||bagType.equals("bd")){
            vehiclesToIterate = graph.trainGreen;
        }
        else if(bagType.equals("abe")||bagType.equals("be")){
            vehiclesToIterate = graph.reindeerGreen;
        }
        else if(bagType.equals("acd")||bagType.equals("cd")){
            vehiclesToIterate = graph.trainRed;
        }
        else if(bagType.equals("ace")||bagType.equals("ce")){
            vehiclesToIterate = graph.reindeerRed;
        }
        else if(bagType.equals("ab")||bagType.equals("b")){
            vehiclesToIterate.addAll(graph.trainGreen);
            vehiclesToIterate.addAll(graph.reindeerGreen);
        }
        else if(bagType.equals("ac")||bagType.equals("c")){
            vehiclesToIterate.addAll(graph.trainRed);
            vehiclesToIterate.addAll(graph.reindeerRed);
        }
        else if(bagType.equals("ad")||bagType.equals("d")){
            vehiclesToIterate.addAll(graph.trainGreen);
            vehiclesToIterate.addAll(graph.trainRed);
        }
        else if(bagType.equals("ae")||bagType.equals("e")){
            vehiclesToIterate.addAll(graph.reindeerGreen);
            vehiclesToIterate.addAll(graph.reindeerRed);
        }
        else if(bagType.equals("a")){
            vehiclesToIterate.addAll(graph.trainGreen);
            vehiclesToIterate.addAll(graph.trainRed);
            vehiclesToIterate.addAll(graph.reindeerGreen);
            vehiclesToIterate.addAll(graph.reindeerRed);
        }
        for (Node vehicleNode : vehiclesToIterate) {
            if(!isTypeContainsA){
                node.addEdge(vehicleNode,new Edge(capacity, node, vehicleNode));
            }
            else{
                node.addEdge(vehicleNode,new Edge(1, node, vehicleNode));
            }
            vehicleNode.addEdge(node,new Edge(0, vehicleNode, node));
        }
    }

    
}
