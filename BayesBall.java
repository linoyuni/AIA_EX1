import java.util.HashSet;
import java.util.Set;

public class BayesBall {
    private BayesianNetwork network;
    private Set<Node> visited;

    Set<String> evidence = new HashSet<>();

    public BayesBall(BayesianNetwork network) {
        this.network = network;
        this.visited = new HashSet<>();
    }


    public boolean isIndependent(String question) {
        // Example question: A-B|E1=e1,E2=e2,…,Ek=ek
        String[] parts = question.split("\\|");
        String[] nodes = parts[0].split("-");
        String startName = nodes[0];
        String endName = nodes[1];
        if (parts.length > 1) {
            String[] evidences = parts[1].split(",");
            for (String evidencePair : evidences) {
                String[] eParts = evidencePair.split("=");
                addEvidence(eParts[0]);
            }
        }


        Node startNode = network.getNode(startName);
        Node endNode = network.getNode(endName);

        // Mark evidence nodes as observed
        for (String e : evidence) {
            Node node = network.getNode(e);
            if (node != null) {
                node.setObserved(true);
            }
        }

        return !canReach(startNode, endNode);
    }

    //Helper method
    private boolean canReach(Node startNode, Node endNode) {
        visited.clear();
        return canReachHelper(startNode, endNode, null);
    }

    //check if there is a path from startNode to endNode
    private boolean canReachHelper(Node current, Node target, Node from) {
        if (current == target) {
            return true;
        }

        if (visited.contains(current)) {
            return false;
        }


        // If current node is observed
        if (current.isObserved()) {
            if (from == null || current.getParents().contains(from)) {
                visited.add(current);
                for (Node parent : current.getParents()) {
                    if (canReachHelper(parent, target, current)) {
                        return true;
                    }
                }
            }
        } else {
            // If current node is not observed
            if (from == null || current.getParents().contains(from)) {
                for (Node child : current.getChildren()) {
                    if (canReachHelper(child, target, current)) {
                        return true;
                    }
                }
            }
            if (from == null || current.getChildren().contains(from)) {
                visited.add(current);
                for (Node parent : current.getParents()) {
                    if (canReachHelper(parent, target, current)) {
                        return true;
                    }
                }
                for (Node child : current.getChildren()) {
                    if (canReachHelper(child, target, current)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void addEvidence(String evidence) {
        this.evidence.add(evidence);
    }
}
