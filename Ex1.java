import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Ex1 {

    public static void main(String[] args) {

        XmlParse.parseXML("src/alarm_net.xml");

        BayesianNetwork network = XmlParse.parseXML("src/alarm_net.xml");

        network.printCPTs();

        VariableElimination ve = new VariableElimination(network);
        VariableElimination ve1 = new VariableElimination(network);

        CPT newcpt=new CPT();
        newcpt=network.cpts().get("J").join(network.cpts().get("M"));
        newcpt.printTable();

        String question1="P(B=T|J=T,M=T) A-E";
        String question2="P(B=T|J=T,M=T) A-E";
        String question3="P(B=T|J=T,M=T) E-A";
        String question4="P(J=T|B=T) A-E-M";
        String question5="P(J=T|B=T) M-E-A";

        ve.executeQuery(question1);

        ve1.executeQuery(question2);

        ve.executeQuery(question3);

        ve.executeQuery(question4);

        ve.executeQuery(question5);

        newcpt=CPT.marginalize(network.cpts().get("A"), "B=T");
        newcpt.printTable();


//        BayesBall b = new BayesBall(network);
//
//        String question = "B-E|";
//
//        String question2="B-E|J=T";
//
//        boolean isDSeparated = b.isIndependent(question);
//
//        System.out.println(isDSeparated ? "yes" : "no");
//
//        isDSeparated = b.isIndependent(question2);
//
//        System.out.println(isDSeparated ? "yes" : "no");



//        if (args.length != 1) {
//            System.err.println("Usage: java Ex1 <input.txt>");
//            System.exit(1);
//        }
//
//        String inputFilePath = args[0];
//        File inputFile = new File(inputFilePath);
//        List<String> lines = readInputFile(inputFile);
//        if (lines == null || lines.isEmpty()) {
//            System.err.println("Error reading input file or file is empty.");
//            System.exit(1);
//        }
//
//        // The first line is the XML path
//        String xmlFilePath = lines.get(0);
//        File xmlFile = new File(xmlFilePath);
//
//        // Parse the Bayesian Network from XML
//        BayesianNetwork network = XMLParser.parseXML(xmlFile);
//
//        // Prepare the output
//        List<String> outputLines = new ArrayList<>();
//
//        // Process each question
//        for (int i = 1; i < lines.size(); i++) {
//            String question = lines.get(i).trim();
//            if (question.contains("P(")) {
//                // Variable elimination question
//                String result = processVariableEliminationQuestion(question, network);
//                outputLines.add(result);
//            } else {
//                // Bayes ball question
//                String result = processBayesBallQuestion(question, network);
//                outputLines.add(result);
//            }
//        }
//
//        // Write the output to a file
//        writeOutputFile(outputLines);
//    }
//
//    private static List<String> readInputFile(File inputFile) {
//        List<String> lines = new ArrayList<>();
//        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                lines.add(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//        return lines;
//    }
//
//    private static void writeOutputFile(List<String> lines) {
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {
//            for (String line : lines) {
//                writer.write(line);
//                writer.newLine();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static String processBayesBallQuestion(String question, BayesianNetwork network) {
//        // Example question: A-B|E1=e1,E2=e2,…,Ek=ek
//        String[] parts = question.split("\\|");
//        String[] nodes = parts[0].split("-");
//        String startNode = nodes[0];
//        String endNode = nodes[1];
//
//        Set<String> evidence = new HashSet<>();
//        if (parts.length > 1) {
//            String[] evidences = parts[1].split(",");
//            for (String evidencePair : evidences) {
//                String[] eParts = evidencePair.split("=");
//                evidence.add(eParts[0]);
//            }
//        }
//
//        // Perform Bayes Ball algorithm
//        BayesBall bayesBall = new BayesBall(network);
//        boolean isDSeparated = bayesBall.isIndependent(startNode, endNode, evidence);
//
//        return isDSeparated ? "no" : "yes";
//    }
//
//    private static String processVariableEliminationQuestion(String question, BayesianNetwork network) {
//        // Example question: P(Q=q|E1=e1, E2=e2, …, Ek=ek) H1-H2-…-Hj
//        Pattern pattern = Pattern.compile("P\\(([^=]+)=([^|]+)\\|([^\\)]+)\\)\\s*([^\\s]*)");
//        Matcher matcher = pattern.matcher(question);
//
//        if (!matcher.find()) {
//            throw new IllegalArgumentException("Invalid question format: " + question);
//        }
//
//        String queryVar = matcher.group(1).trim();
//        String queryValue = matcher.group(2).trim();
//        String evidenceStr = matcher.group(3).trim();
//        String hiddenVarsStr = matcher.group(4).trim();
//
//        // Parse evidence
//        Map<String, String> evidenceMap = new HashMap<>();
//        String[] evidences = evidenceStr.split(",");
//        for (String evidencePair : evidences) {
//            String[] eParts = evidencePair.split("=");
//            evidenceMap.put(eParts[0].trim(), eParts[1].trim());
//        }
//
//        // Parse hidden variables
//        Set<String> hiddenVars = new HashSet<>();
//        if (!hiddenVarsStr.isEmpty()) {
//            String[] hiddenVarArr = hiddenVarsStr.split("-");
//            hiddenVars.addAll(Arrays.asList(hiddenVarArr));
//        }
//
//        // Perform Variable Elimination
//        VariableElimination ve = new VariableElimination(network);
//        VariableElimination.Result result = ve.inference(queryVar, queryValue, evidenceMap, hiddenVars);
//
//        // Format the result to 5 decimal places
//        StringBuilder sb = new StringBuilder();
//        sb.append(String.format("%.5f", result.getProbability()));
//        sb.append(",").append(result.getNumAdditions());
//        sb.append(",").append(result.getNumMultiplications());
//
//        return sb.toString();
    }
}
