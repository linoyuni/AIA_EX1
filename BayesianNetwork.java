import java.util.*;

public class BayesianNetwork {
    private Map<String, Node> nodes;

    //Builder
    public BayesianNetwork() {
        nodes = new HashMap<>();
    }

    public void addNode(Node node) {
        nodes.put(node.getName(), node);
    }

    public Node getNode(String name) {
        return nodes.get(name);
    }

    public Collection<Node> getNodes() {
        return nodes.values();
    }

    public Set<String> getNodeNames() {
        return nodes.keySet();
    }

    public void printNetwork() {
        for (Node node : nodes.values()) {
            System.out.println(node.getName());
            System.out.println("Parents: " + node.getParents());
            System.out.println("Children: " + node.getChildren());
            System.out.println();
        }
    }

    public void printCPTs() {
        for (Node node : nodes.values()) {
            System.out.println(node.getName());
            node.getCPT().printTable();
            System.out.println();
        }
    }

    public Map<String, CPT> cpts() {
        Map<String, CPT> cpts = new HashMap<>();
        for (Node node : nodes.values()) {
            cpts.put(node.getName(), node.getCPT());
        }
        return cpts;
    }
}
