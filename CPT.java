import java.util.*;

public class CPT {
    private List<String> variables;
    private Map<List<String>, Double> table;

    //Builder
    public CPT() {
        table = new HashMap<>();
        variables = new ArrayList<>();
    }

    //Builder
    public CPT(HashMap<List<String>, Double> table) {
        this.table = new HashMap<>(table);
        variables = new ArrayList<>();
    }

    public void setProbability(List<String> conditions, double probability) {
        table.put(conditions, probability);
    }

    public double getProbability(List<String> conditions) {
        return table.getOrDefault(conditions, 0.0);
    }

    public void printTable() {
        System.out.println(this.variables);
        for (Map.Entry<List<String>, Double> entry : table.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

    //Get all the variables that are in the table
    public Set<List<String>> getConditions() {
        return table.keySet();
    }

    //Join two CPTs (for VE)
    public CPT join(CPT other, List<String> commonVariables, VariableElimination v) {
        CPT result = new CPT();
        List<String> newVariables = new ArrayList<>(variables);
        for (String var : other.variables) {
            if (!newVariables.contains(var)) {
                newVariables.add(var);
            }
        }
        result.variables = newVariables;
        v.addNumMultiplication(); // Call for each multiplication

        for (Map.Entry<List<String>, Double> entry1 : table.entrySet()) {
            for (Map.Entry<List<String>, Double> entry2 : other.table.entrySet()) {
                if (matches(entry1.getKey(), entry2.getKey(), commonVariables)) {
                    List<String> newConditions = new ArrayList<>(entry1.getKey());
                    for (String condition : entry2.getKey()) {
                        if (!newConditions.contains(condition)) {
                            newConditions.add(condition);
                        }
                    }
                    double newProbability = entry1.getValue() * entry2.getValue();

                    result.setProbability(newConditions, newProbability);
                }
            }
        }
        return result;
    }

    //Check if two conditions match
    private boolean matches(List<String> conditions1, List<String> conditions2, List<String> commonVariables) {
        for (String var : commonVariables) {
            String value1 = conditions1.stream().filter(cond -> cond.startsWith(var + "=")).findFirst().orElse(null);
            String value2 = conditions2.stream().filter(cond -> cond.startsWith(var + "=")).findFirst().orElse(null);
            if (value1 == null || value2 == null || !value1.equals(value2)) {
                return false;
            }
        }
        return true;
    }

    //Marginalize a CPT
    public static CPT marginalize(CPT cpt, String variable, VariableElimination v) {
        CPT marginalizedCPT = new CPT();
        v.addNumAdditions(); // Call for each addition
        for (Map.Entry<List<String>, Double> entry : cpt.getTable().entrySet()) {
            List<String> conditions = entry.getKey();
            List<String> newConditions = new ArrayList<>(conditions);
            newConditions.removeIf(cond -> cond.startsWith(variable + "="));
            double currentProbability = marginalizedCPT.getProbability(newConditions);
            marginalizedCPT.setProbability(newConditions, currentProbability + entry.getValue());
        }
        return marginalizedCPT;
    }

    //Normalize a CPT
    public static CPT normalize(CPT cpt) {
        double sum = cpt.getTable().values().stream().mapToDouble(Double::doubleValue).sum();
        CPT normalizedCPT = new CPT();
        for (Map.Entry<List<String>, Double> entry : cpt.getTable().entrySet()) {
            normalizedCPT.setProbability(entry.getKey(), entry.getValue() / sum);
        }
        return normalizedCPT;
    }

    // Join two CPTs (for self checking)
    public CPT join(CPT other) {
        List<String> commonVariables = new ArrayList<>();
        for (String var : variables) {
            if (other.containsVariable(var) && this.containsVariable(var)) {
                commonVariables.add(var);
            }
        }
        CPT result = new CPT();
        List<String> newVariables = new ArrayList<>(variables);
        for (String var : other.variables) {
            if (!newVariables.contains(var)) {
                newVariables.add(var);
            }
        }
        result.variables = newVariables;

        for (Map.Entry<List<String>, Double> entry1 : table.entrySet()) {
            for (Map.Entry<List<String>, Double> entry2 : other.table.entrySet()) {
                if (matches(entry1.getKey(), entry2.getKey(), commonVariables)) {
                    List<String> newConditions = new ArrayList<>(entry1.getKey());
                    for (String condition : entry2.getKey()) {
                        if (!newConditions.contains(condition)) {
                            newConditions.add(condition);
                        }
                    }
                    result.setProbability(newConditions, entry1.getValue() * entry2.getValue());
                }
            }
        }
        return result;
    }


    public double[] getProbabilities(String queryValue) {
        double[] probabilities = new double[variables.size()];
        for (int i = 0; i < variables.size(); i++) {
            List<String> conditions = new ArrayList<>();
            conditions.add(variables.get(i) + "=" + queryValue);
            probabilities[i] = getProbability(conditions);
        }
        return probabilities;
    }

    public Map<List<String>, Double> getTable() {
        return this.table;
    }

    public void addVariable(String name) {
        variables.add(name);
    }

    public boolean containsVariable(String name) {
        return variables.contains(name);
    }

    public int getVariableIndex(String name) {
        return variables.indexOf(name);
    }

}