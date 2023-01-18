import java.util.Map;

public class PushRelabel {
    public static void saturateBags(Graph graph,int number){
        graph.sourceNode.height = number;
        for (Map.Entry<Node, Edge> nodeEdgePair : graph.sourceNode.adjacentEdges.entrySet()) {
            Node node = nodeEdgePair.getKey();
            Edge edge = nodeEdgePair.getValue();
            int flowValue = edge.capacity;
            node.excessFlow += flowValue;
            edge.capacity = 0;
            node.adjacentEdges.get(graph.sourceNode).capacity = flowValue;
            graph.activeNodes.add(node);
            node.isActive = true;
        }
    }
    
    public static void push(Graph graph,Node firstNode, Node secondNode, Edge edge){
        int flowValue = Math.min(firstNode.excessFlow, edge.capacity);
        firstNode.excessFlow -= flowValue;
        edge.capacity -= flowValue;
        secondNode.excessFlow += flowValue;
        secondNode.adjacentEdges.get(firstNode).capacity += flowValue;      
        if(!secondNode.equals(graph.sinkNode)&&!secondNode.isActive&&!secondNode.equals(graph.sourceNode)&&secondNode.height<=graph.sourceNode.height){
            graph.activeNodes.add(secondNode);
            secondNode.isActive = true;
        }
        if(firstNode.excessFlow==0){
            graph.activeNodes.remove(firstNode);
            firstNode.isActive = false;
        }


    }

    public static void relabel(Graph graph, Node node){
        node.height++;
        if(graph.vehicleNodes.contains(node)){
            graph.vehicleNodes.remove(node);
        }
        if(node.height>graph.sourceNode.height){
            graph.activeNodes.remove(node);
            node.isActive = false;
        }
    }

    public static void discharge(Graph graph, Node node){
        if(node.excessFlow>0){
            while(node.pointer<node.currentArc.size()){
                Node adjacentNode = node.currentArc.get(node.pointer);
                if(node.height==adjacentNode.height+1&&node.adjacentEdges.get(adjacentNode).capacity>0){
                    push(graph,node, adjacentNode, node.adjacentEdges.get(adjacentNode));
                    if(node.excessFlow==0)return;
                }
                node.pointer++;
            }
        }
        if(node.excessFlow>0){
            node.pointer = 0;
            relabel(graph, node);
        }
    }

    public static void run(Graph graph){
        while(!graph.activeNodes.isEmpty()){
            if(graph.vehicleNodes.isEmpty()){
                push(graph,graph.greenRegion, graph.sinkNode, graph.greenRegion.adjacentEdges.get(graph.sinkNode));
                push(graph, graph.redRegion, graph.sinkNode, graph.redRegion.adjacentEdges.get(graph.sinkNode));
                return;
            }
            Node node = graph.activeNodes.getFirst();
            int oldHeight = node.height;
            discharge(graph,node);
            if(oldHeight<node.height&&!node.isBag){
                graph.activeNodes.poll();
                graph.activeNodes.addLast(node);
            }
        }
    }
}
