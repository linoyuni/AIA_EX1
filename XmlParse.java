import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

public class XmlParse {

    public static BayesianNetwork parseXML(String path) {
        BayesianNetwork network = new BayesianNetwork();

        try {
            File file = new File(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            // Parse variables
            NodeList variableList = doc.getElementsByTagName("VARIABLE");
            for (int i = 0; i < variableList.getLength(); i++) {
                Element variableElement = (Element) variableList.item(i);
                String variableName = variableElement.getElementsByTagName("NAME").item(0).getTextContent();
                Node node = new Node(variableName);

                NodeList outcomeList = variableElement.getElementsByTagName("OUTCOME");
                for (int j = 0; j < outcomeList.getLength(); j++) {
                    String outcome = outcomeList.item(j).getTextContent();
                    node.addOutcome(outcome);
                }

                network.addNode(node);
            }

            // Parse definitions
            NodeList definitionList = doc.getElementsByTagName("DEFINITION");
            for (int i = 0; i < definitionList.getLength(); i++) {
                Element definitionElement = (Element) definitionList.item(i);
                String forNodeName = definitionElement.getElementsByTagName("FOR").item(0).getTextContent();
                Node forNode = network.getNode(forNodeName);

                NodeList givenList = definitionElement.getElementsByTagName("GIVEN");
                List<Node> parents = new ArrayList<>();
                for (int j = 0; j < givenList.getLength(); j++) {
                    String givenNodeName = givenList.item(j).getTextContent();
                    Node parentNode = network.getNode(givenNodeName);
                    forNode.addParent(parentNode);
                    parentNode.addChild(forNode);
                    parents.add(parentNode);
                }

                String[] probabilities = definitionElement.getElementsByTagName("TABLE").item(0).getTextContent().trim().split("\\s+");
                List<String> outcomes = forNode.getOutcomes();
                CPT cpt = new CPT();

                // Generate all combinations of outcomes and set probabilities in CPT
                generateCombinations(parents, outcomes, probabilities, cpt);



                //adding all the variables to the cpt
                for (Node parent : parents) {
                    cpt.addVariable(parent.getName());
                }
                cpt.addVariable(forNode.getName());

                // Set the generated CPT for the current node
                forNode.setCPT(cpt);


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        network.printNetwork(); // Assuming you have a method to print the network structure

        return network;
    }

    private static void generateCombinations(List<Node> parents, List<String> outcomes, String[] probabilities, CPT cpt) {
        int count = 0;
        // Calculate the number of outcomes for parents
        int numParentOutcomes = 1;
        for (Node parent : parents) {
            numParentOutcomes *= parent.getOutcomes().size();
        }

        // Iterate through all combinations of parent outcomes
        for (int parentCombinationIndex = 0; parentCombinationIndex < numParentOutcomes; parentCombinationIndex++) {
            List<String> conditions = new ArrayList<>();
            int index = parentCombinationIndex;

            // Construct the current parent combination
            for (Node parent : parents) {
                List<String> parentOutcomes = parent.getOutcomes();
                int numOutcomes = parentOutcomes.size();
                int parentOutcomeIndex = index % numOutcomes;
                conditions.add(parentOutcomes.get(parentOutcomeIndex));
                index /= numOutcomes;
            }

            // Reverse the conditions list to match the order of parents
            Collections.reverse(conditions);

            // Iterate through outcomes of the current node
            for (String outcome : outcomes) {
                conditions.add(outcome);
                double probability = Double.parseDouble(probabilities[count++]); // Assuming one probability per set of conditions
                cpt.setProbability(new ArrayList<>(conditions), probability);
                conditions.remove(conditions.size() - 1);
            }
        }
    }
}

