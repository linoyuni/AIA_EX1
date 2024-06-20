import java.util.*;

public class CPT {
    private List<String> variables;
    private Map<List<String>, Double> table;

    public CPT() {
        table = new HashMap<>();
        variables = new ArrayList<>();
    }

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

    public Set<List<String>> getConditions() {
        return table.keySet();
    }

    // Added join method
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

    public CPT join(CPT other, List<String> commonVariables, VariableElimination v) {
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
                            v.addNumMultiplication();
                        }
                    }
                    result.setProbability(newConditions, entry1.getValue() * entry2.getValue());
                }
            }
        }
        return result;
    }

    // Added matches method
    private boolean matches(List<String> conditions1, List<String> conditions2, List<String> commonVariables) {
        for (String var : commonVariables) {
            int index1 = conditions1.indexOf(var);
            int index2 = conditions2.indexOf(var);
            if (index1 != -1 && index2 != -1 && !conditions1.get(index1).equals(conditions2.get(index2))) {
                return false;
            }
        }
        return true;
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

    public Map<List<String>,Double> getTable() {
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

//    public static CPT eliminateVariables(Map<String, CPT> cpts, List<String> hiddenVars) {
//        Map<String, CPT> tempCPTs = new HashMap<>();
//        for (Map.Entry<String, CPT> entry : cpts.entrySet()) {
//            tempCPTs.put(entry.getKey(), new CPT((HashMap<List<String>, Double>) entry.getValue().getTable()));
//        }
//
//        for (String hiddenVar : hiddenVars) {
//            List<CPT> factorsWithHiddenVar = new ArrayList<>();
//            for (CPT cpt : tempCPTs.values()) {
//                if (cpt.containsVariable(hiddenVar)) {
//                    factorsWithHiddenVar.add(cpt);
//                }
//            }
//
//            if (!factorsWithHiddenVar.isEmpty()) {
//                CPT joinedCPT = factorsWithHiddenVar.get(0);
//                for (int i = 1; i < factorsWithHiddenVar.size(); i++) {
//                    joinedCPT = joinedCPT.join(factorsWithHiddenVar.get(i), Collections.singletonList(hiddenVar));
//                }
//                CPT marginalizedCPT = marginalize(joinedCPT, hiddenVar);
//
//                for (CPT factor : factorsWithHiddenVar) {
//                    tempCPTs.values().remove(factor);
//                }
//                tempCPTs.put(hiddenVar, marginalizedCPT);
//            }
//        }
//
//        CPT finalCPT = null;
//        for (CPT cpt : tempCPTs.values()) {
//            if (finalCPT == null) {
//                finalCPT = cpt;
//            } else {
//                finalCPT = finalCPT.join(cpt, Collections.emptyList());
//            }
//        }
//
//        return normalize(finalCPT);
//    }

     //New helper method added
    public static CPT marginalize(CPT cpt, String variable) {
        int count = 0;
        CPT marginalizedCPT = new CPT();
        for (Map.Entry<List<String>, Double> entry : cpt.getTable().entrySet()) {
            List<String> conditions = entry.getKey();
            List<String> newConditions = new ArrayList<>(conditions);
            newConditions.removeIf(cond -> cond.startsWith(variable + "="));
            double currentProbability = marginalizedCPT.getProbability(newConditions);
//            System.out.println("Current Probability: " + currentProbability);
            marginalizedCPT.setProbability(newConditions, currentProbability + entry.getValue());
//            System.out.println("New Probability: " + (currentProbability + entry.getValue()));
            count++;
        }
//        marginalizedCPT.printTable();
        return marginalizedCPT;
    }

    public static CPT marginalize(CPT cpt, String variable, VariableElimination v) {
        CPT marginalizedCPT = new CPT();
        for (Map.Entry<List<String>, Double> entry : cpt.getTable().entrySet()) {
            List<String> conditions = entry.getKey();
            List<String> newConditions = new ArrayList<>(conditions);
            newConditions.removeIf(cond -> cond.startsWith(variable + "="));
            double currentProbability = cpt.getProbability(newConditions);
            v.addNumAdditions();
            marginalizedCPT.setProbability(newConditions, currentProbability + entry.getValue());
        }
        System.out.println(v.numAdditions+" additions");
        return marginalizedCPT;
    }

    // New helper method added
    public static CPT normalize(CPT cpt) {
        double sum = 0.0;
        for (double value : cpt.getTable().values()) {
            sum += value;
        }
        CPT normalizedCPT = new CPT();
        for (Map.Entry<List<String>, Double> entry : cpt.getTable().entrySet()) {
            normalizedCPT.setProbability(entry.getKey(), entry.getValue() / sum);
        }
        return normalizedCPT;
    }
}
