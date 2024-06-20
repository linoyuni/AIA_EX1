import java.util.*;

public class Node {
    private String name;
    private List<String> outcomes;
    private List<Node> parents = new ArrayList<>();
    private List<Node> children = new ArrayList<>();
    private CPT cpt;
    boolean observed;

    public Node(String name) {
        this.name = name;
        this.outcomes = new ArrayList<>();
        this.parents = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<String> getOutcomes() {
        return outcomes;
    }

    public void addOutcome(String outcome) {
        outcomes.add(outcome);
    }

    public List<Node> getParents() {
        return parents;
    }

    public void addParent(Node parent) {
        parents.add(parent);
    }

    public void addChild(Node child) {
        children.add(child);
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setCPT(CPT cpt) {
        this.cpt = cpt;
    }

    public CPT getCPT() {
        return cpt;
    }

    public boolean isObserved() {
        return observed;
    }

    public void setObserved(boolean observed) {
        this.observed = observed;
    }


    public String[] getValues() {
        return outcomes.toArray(new String[0]);
    }
}
