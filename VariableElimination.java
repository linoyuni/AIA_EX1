import java.util.*;

public class VariableElimination {

    private BayesianNetwork network;
    private Map<String, CPT> factors;

    int numAdditions = 0;
    int numMultiplications = 0;

    public VariableElimination(BayesianNetwork network) {
        this.network = network;
        this.factors = new HashMap<>();
    }

    public void addNumAdditions(){
        numAdditions++;
    }

    public void addNumMultiplication(){
        numMultiplications++;
    }

//    public Result inference(String queryVar, String queryValue, Map<String, String> evidenceMap, List<String> hiddenVars) {
//        // Initialize CPTs for all variables in the network
//        // Placeholder for actual counts (assuming they are not tracked in this implementation)
//
//
//        for (Node node : network.getNodes()) {
//            factors.put(node.getName(), node.getCPT());
//        }
//
//        // Incorporate evidence
//        for (Map.Entry<String, String> evidence : evidenceMap.entrySet()) {
//            String var = evidence.getKey();
//            String value = evidence.getValue();
//            CPT evidenceCPT = new CPT();
//            for (List<String> conditions : factors.get(var).getConditions()) {
//                if (conditions.contains(var + "=" + value)) {
//                    evidenceCPT.setProbability(conditions, factors.get(var).getProbability(conditions));
//                }
//            }
//            factors.put(var, evidenceCPT);
//        }
//
//        // Eliminate hidden variables
//        CPT resultCPT = eliminateVariables(factors, hiddenVars, this);
//
//        // Extract the final probability of the query variable given the evidence
//        double queryProbability = 0.0;
//        for (Map.Entry<List<String>, Double> entry : resultCPT.getTable().entrySet()) {
//            List<String> conditions = entry.getKey();
//            if (conditions.contains(queryVar + "=" + queryValue)) {
//                queryProbability += entry.getValue();
//            }
//        }

    public Result inference(String queryVar, String queryValue, Map<String, String> evidenceMap, List<String> hiddenVars) {
        // Initialize CPTs for all variables in the network
        for (Node node : network.getNodes()) {
            factors.put(node.getName(), node.getCPT());
        }

        // Incorporate evidence
        for (Map.Entry<String, String> evidence : evidenceMap.entrySet()) {
            String var = evidence.getKey();
            String value = evidence.getValue();
            CPT evidenceCPT = new CPT();
            for (List<String> conditions : factors.get(var).getConditions()) {
                if (conditions.contains(var + "=" + value)) {
                    evidenceCPT.setProbability(conditions, factors.get(var).getProbability(conditions));
                }
            }
            factors.put(var, evidenceCPT);
        }

        // Eliminate hidden variables
        CPT resultCPT = eliminateVariables(factors, hiddenVars, this);

        // Extract the final probability of the query variable given the evidence
        double queryProbability = 0.0;
        for (Map.Entry<List<String>, Double> entry : resultCPT.getTable().entrySet()) {
            List<String> conditions = entry.getKey();
            if (conditions.contains(queryVar + "=" + queryValue)) {
                queryProbability += entry.getValue();
            }
        }

        // Normalize the result
        resultCPT = CPT.normalize(resultCPT);

        // Return the result with the number of additions and multiplications
        return new Result(queryProbability, numAdditions, numMultiplications);
    }

    public static CPT eliminateVariables(Map<String, CPT> cpts, List<String> hiddenVars, VariableElimination v) {
        Map<String, CPT> tempCPTs = new HashMap<>();
        for (Map.Entry<String, CPT> entry : cpts.entrySet()) {
            tempCPTs.put(entry.getKey(), new CPT((HashMap<List<String>, Double>) entry.getValue().getTable()));
        }

        for (String hiddenVar : hiddenVars) {
            List<CPT> factorsWithHiddenVar = new ArrayList<>();
            for (CPT cpt : tempCPTs.values()) {
                if (cpt.containsVariable(hiddenVar)) {
                    factorsWithHiddenVar.add(cpt);
                }
            }

            if (!factorsWithHiddenVar.isEmpty()) {
                CPT joinedCPT = factorsWithHiddenVar.get(0);
                for (int i = 1; i < factorsWithHiddenVar.size(); i++) {
                    joinedCPT = joinedCPT.join(factorsWithHiddenVar.get(i), Collections.singletonList(hiddenVar), v);
                }
                CPT marginalizedCPT = CPT.marginalize(joinedCPT, hiddenVar, v);

                for (CPT factor : factorsWithHiddenVar) {
                    tempCPTs.values().remove(factor);
                }
                tempCPTs.put(hiddenVar, marginalizedCPT);
            }
        }

        CPT finalCPT = null;
        for (CPT cpt : tempCPTs.values()) {
            if (finalCPT == null) {
                finalCPT = cpt;
            } else {
                finalCPT = finalCPT.join(cpt, Collections.emptyList(), v);
            }
        }

        return finalCPT;
    }

    public void executeQuery(String question){
        String[] parts = question.split(" ");
        String[] nodes = parts[0].split("-");
        String[] hidden ={};
        if(parts.length > 1) {
            hidden = parts[1].split("-");
        }
        question=question.substring(question.indexOf("(")+1,question.indexOf(")"));

        String[] query = question.split("\\|");

        String queryVar= "";
        String queryValue = "";

        //parse query variables
        for(String q: query[0].split(",")){
            String[] qParts = q.split("=");
            queryVar = qParts[0];
            queryValue = qParts[1];
        }
        Map<String, String> evidenceMap = new HashMap<>();
        if(query.length>1)
        {

            for(String e: query[1].split(",")){
                String[] eParts = e.split("=");
                evidenceMap.put(eParts[0], eParts[1]);
            }
        }

        String s = (inference(queryVar, queryValue, evidenceMap, Arrays.asList(hidden)).toString());

        System.out.println(s);




    }

    private void observeEvidence(Map<String, String> evidenceMap) {
        //observing evidence and leaving only the factors that contain the evidence and only the entries that match the evidence and then remove the evidence
        for (Map.Entry<String, String> entry : evidenceMap.entrySet()) {
            String var = entry.getKey();
            String value = entry.getValue();
            CPT factor = factors.get(var);
            CPT newFactor = new CPT();
            for (Map.Entry<List<String>, Double> factorEntry : factor.getTable().entrySet()) {
                List<String> conditions = factorEntry.getKey();
                double probability = factorEntry.getValue();
                if (conditions.contains(value)) {
                    newFactor.setProbability(conditions, probability);
                }
            }
            factors.put(var, newFactor);
        }
    }

    private void eliminateVariable(CPT table, String var) {
        //summing out the variable we want to eliminate and removing it form the resulting table
        CPT newTable = new CPT();
        for (Map.Entry<List<String>, Double> entry : table.getTable().entrySet()) {
            List<String> conditions = entry.getKey();
            double probability = entry.getValue();
            for (String value : network.getNode(var).getValues()) {
                List<String> newConditions = new ArrayList<>(conditions);
                newConditions.add(value);
                double sumProbability = 0;
                for (String hiddenValue : network.getNode(var).getValues()) {
                    newConditions.set(newConditions.size() - 1, hiddenValue);
                    sumProbability += probability * table.getProbability(newConditions);
                }
                newConditions.remove(newConditions.size() - 1);
            }
        }

    }





    public static class Result {
        private final double probability;
        private final int numAdditions;
        private final int numMultiplications;

        public Result(double probability, int numAdditions, int numMultiplications) {
            this.probability = probability;
            this.numAdditions = numAdditions;
            this.numMultiplications = numMultiplications;
        }

        public double getProbability() {
            return probability;
        }

        public int getNumAdditions() {
            return numAdditions;
        }

        public int getNumMultiplications() {
            return numMultiplications;
        }

        public String toString(){
            return "Probability: "+probability+" Additions: "+numAdditions+" Multiplications: "+numMultiplications;
        }
    }
}
