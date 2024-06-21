import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

//326191269

/**
 * In order to implement the algorithms
 * creat an object of each class (VariableElimination, BayesBall)
 * enter at the constructor the network
 * then run the algorithms by running the methods of the objects
 * for BayesBall, run isIndependent method
 * for VariableElimination, run executeQuery method
 * enter the question as a string
 *
 * to parse a xml file, use the parseXML method in the XmlParse class
 * the method returns a BayesianNetwork object
 */


class Ex1 {
    public static void main(String[] args) {


//        try {
//            // Read the input file name from "input.txt"
//            BufferedReader reader = new BufferedReader(new FileReader("input.txt"));
//            String xmlFileName = reader.readLine();
//            BayesianNetwork network = new BayesianNetwork();
//
//            // Parse the XML file
//            File inputFile = new File(xmlFileName);
//            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//            Document doc = dBuilder.parse(inputFile);
//            doc.getDocumentElement().normalize();
//
//                // Parse variables
//                NodeList variableList = doc.getElementsByTagName("VARIABLE");
//                for (int i = 0; i < variableList.getLength(); i++) {
//                    Element variableElement = (Element) variableList.item(i);
//                    String variableName = variableElement.getElementsByTagName("NAME").item(0).getTextContent();
//                    Node node = new Node(variableName);
//
//                    NodeList outcomeList = variableElement.getElementsByTagName("OUTCOME");
//                    for (int j = 0; j < outcomeList.getLength(); j++) {
//                        String outcome = outcomeList.item(j).getTextContent();
//                        node.addOutcome(outcome);
//                    }
//
//                    network.addNode(node);
//                }
//
//                // Parse definitions
//                NodeList definitionList = doc.getElementsByTagName("DEFINITION");
//                for (int i = 0; i < definitionList.getLength(); i++) {
//                    Element definitionElement = (Element) definitionList.item(i);
//                    String forNodeName = definitionElement.getElementsByTagName("FOR").item(0).getTextContent();
//                    Node forNode = network.getNode(forNodeName);
//
//                    NodeList givenList = definitionElement.getElementsByTagName("GIVEN");
//                    List<Node> parents = new ArrayList<>();
//                    for (int j = 0; j < givenList.getLength(); j++) {
//                        String givenNodeName = givenList.item(j).getTextContent();
//                        Node parentNode = network.getNode(givenNodeName);
//                        forNode.addParent(parentNode);
//                        parentNode.addChild(forNode);
//                        parents.add(parentNode);
//                    }
//
//                    String[] probabilities = definitionElement.getElementsByTagName("TABLE").item(0).getTextContent().trim().split("\\s+");
//                    List<String> outcomes = forNode.getOutcomes();
//                    CPT cpt = new CPT();
//
//                    // Generate all combinations of outcomes and set probabilities in CPT
//                    XmlParse.generateCombinations(parents, outcomes, probabilities, cpt);
//
//
//
//                    //adding all the variables to the cpt
//                    for (Node parent : parents) {
//                        cpt.addVariable(parent.getName());
//                    }
//                    cpt.addVariable(forNode.getName());
//
//                    // Set the generated CPT for the current node
//                    forNode.setCPT(cpt);
//
//
//                }
//
//
//
//
//
//
//            // Print factors after initialization
//            VariableElimination ve = new VariableElimination(network);
//            BayesBall bb = new BayesBall(network);
//            // Read and process each line in the input file
//            String line;
//            while ((line = reader.readLine()) != null) {
//                if (line.contains("P(")) {
//                    ve.executeQuery(line);
//                } else {
//                    boolean a =bb.isIndependent(line);
//                    if(a){
//                        try(BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true))) {
//                            writer.write("yes");
//                            writer.newLine();
//                        }
//                        catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    else {
//                        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true))) {
//                            writer.write("no");
//                            writer.newLine();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                }
//            }
//            reader.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}





        XmlParse.parseXML("src/alarm_net.xml");

        BayesianNetwork network = XmlParse.parseXML("src/alarm_net.xml");

        BayesBall b = new BayesBall(network);
        VariableElimination ve = new VariableElimination(network);


        String bquestion1 = "B-E|";
        String bquestion2 = "B-E|J=T";

        if(b.isIndependent(bquestion1)){
            System.out.println("yes");
            try(BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true))) {
                writer.write("yes");
                writer.newLine();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            System.out.println("no");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true))) {
                writer.write("no");
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(b.isIndependent(bquestion2)){
            System.out.println("yes");
            try(BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true))) {
                writer.write("yes");
                writer.newLine();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            System.out.println("no");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true))) {
                writer.write("no");
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String vquestion1 = "P(B=T|J=T,M=T) A-E";
        String vquestion2 = "P(B=T|J=T,M=T) E-A";
        String vquestion3 = "P(J=T|B=T) A-E-M";
        String vquestion4 = "P(J=T|B=T) M-E-A";

        String ans1 = ve.executeQuery(vquestion1);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true))) {
            writer.write(ans1);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String ans2 = ve.executeQuery(vquestion2);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true))) {
            writer.write(ans2);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String ans3 = ve.executeQuery(vquestion3);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true))) {
            writer.write(ans3);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String ans4 = ve.executeQuery(vquestion4);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true))) {
            writer.write(ans4);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(ans1);
        System.out.println(ans2);
        System.out.println(ans3);
        System.out.println(ans4);




    }
}

