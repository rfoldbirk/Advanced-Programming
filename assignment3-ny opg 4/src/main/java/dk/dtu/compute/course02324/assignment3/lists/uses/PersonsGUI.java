package dk.dtu.compute.course02324.assignment3.lists.uses;


//import dk.dtu.compute.course02324.assignment3.lists.implementations.GenericComparator;
//import dk.dtu.compute.course02324.assignment3.lists.types.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import jakarta.validation.constraints.NotNull;

import java.sql.SQLOutput;
import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 * A GUI element that is allows the user to interact and
 * change a list of persons.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class PersonsGUI extends GridPane {

    /**
     * The list of persons to be maintained in this GUI.
     */
    final private List<Person> persons;

    private GridPane personsPane;

    private int weightCount = 1;

    private TextArea textAreaExceptions;

    private Label averageWeight;
    private Label freqName;

    private Label minAge;
    private Label maxAge;

    /**
     * Constructor which sets up the GUI attached a list of persons.
     *
     * @param persons the list of persons which is to be maintained in
     *                this GUI component; it must not be <code>null</code>
     */
    public PersonsGUI(@NotNull List<Person> persons) {
        this.persons = persons;

        this.setVgap(5.0);
        this.setHgap(5.0);

        // text filed for user entering a name
        TextField field = new TextField();
        field.setPrefColumnCount(5);
        field.setText("name");

        // text field for weight
        TextField weightField = new TextField();
        weightField.setPrefColumnCount(5);
        weightField.setText("weight");

        // text field for index
        TextField indexField = new TextField();
        indexField.setPrefColumnCount(5);
        indexField.setText("Index Field");

        // text field for age
        TextField ageField = new TextField();
        ageField.setPrefColumnCount(5);
        ageField.setText("Age Field");

        // textAreaException
        textAreaExceptions = new TextArea();
        textAreaExceptions.setPrefRowCount(3);
        textAreaExceptions.setEditable(false);

        averageWeight = new Label("");
        freqName = new Label("");

        minAge = new Label("Min age: ");
        maxAge = new Label("Max Age: ");

        // TODO for all buttons installed below, the actions need to properly
        //      handle (catch) exceptions, and it would be nice if the GUI
        //      could also show the exceptions thrown by user actions on
        //      button pressed (cf. Assignment 2).

        // button for adding a new person to the list (based on
        // the name in the text field (the weight is just incrementing)
        // TODO a text field for the weight could be added to this GUI
        Button addButton = new Button("Add");
        addButton.setOnAction(
                e -> {
                    // makes sure that the GUI is updated accordingly
                    try {
                        int indexGetText = Integer.parseInt(indexField.getText());
                        Person person = new Person(field.getText(), Double.parseDouble(weightField.getText()), Integer.parseInt(ageField.getText()));
                        // person.setAge(Integer.parseInt(ageField.getText()));
                        persons.add(person);

                    } catch (NullPointerException error) {
                        if (textAreaExceptions != null){
                            textAreaExceptions.appendText(error.getMessage());
                        }
                    } catch (NumberFormatException erro) {
                        if (textAreaExceptions != null){
                            textAreaExceptions.appendText(erro.getMessage());
                        }
                    }
                    // makes sure that the GUI is updated accordingly
                    update();

                });

        // Comparator<Person> comparator = new GenericComparator<>();
        Comparator<Person> comparator = (p1, p2) -> p1.compareTo(p2);

        // button for sorting the list (according to the order of Persons,
        // which implement the interface Comparable, which is converted
        // to a Comparator by the GenericComparator above)
        Button sortButton = new Button("Sort");
        sortButton.setOnAction(
                e -> {
                        persons.sort(Comparator.naturalOrder());


                    // makes sure that the GUI is updated accordingly
                    update();
                });

        // button for clearing the list
        Button clearButton = new Button("Clear");
        clearButton.setOnAction(
                e -> {
                        persons.clear();
                        textAreaExceptions.clear();


                    // makes sure that the GUI is updated accordingly
                    update();
                });

        // Jeg har lavet en ny branch der hedder "time" lokalt
        // Jeg har skrevet opgave 3) ny funktionalitet herunder
        Button timePassButton = new Button("Time pass");
        timePassButton.setOnAction(
                e -> {
                    // 3b: Alle bliver 1 år ældre
                    persons.forEach(p -> p.setAge(p.getAge() + 1));

                    // 3c) 8% lagt oven på personer over 30 år
                    persons.replaceAll(p ->{
                        if (p.getAge() > 30){
                            Person updated = new Person(p.name, p.weight * 1.08, p.getAge());
                            updated.setAge(p.getAge());
                            return updated;
                        }
                        return  p;
                    });

                    // 3d) Personer på 99+ år skal fjernes
                    persons.removeIf(p -> p.getAge() >= 99);
                    update();
                }
        );

        Button indexButton = new Button("Add to index");
        indexButton.setOnAction(
            e -> {
                try {
                    int indexValue = Integer.parseInt(indexField.getText());
                    Person person = new Person(field.getText(), Double.parseDouble(weightField.getText()), Integer.parseInt(ageField.getText()));
                    person.setAge(Integer.parseInt(ageField.getText()));
                    persons.add(indexValue,person);
                } catch (NumberFormatException err){
                    if (textAreaExceptions != null){
                        textAreaExceptions.appendText(err.getMessage());
                    }
                } catch (NullPointerException error){
                    if (textAreaExceptions != null){
                        textAreaExceptions.appendText(error.getMessage());
                    }
                } catch (IndexOutOfBoundsException erro){
                    if (textAreaExceptions != null){
                        textAreaExceptions.appendText(erro.getMessage());
                    }
                }
                update();
        });

        // combines the above elements into vertically arranged boxes
        // which are then added to the left column of the grid pane
        VBox actionBox = new VBox(field, ageField, weightField, indexField, indexButton, addButton, sortButton, clearButton, averageWeight, freqName, minAge, maxAge, textAreaExceptions);
        actionBox.setSpacing(5.0);
        this.add(actionBox, 0, 0);

        // create the elements of the right column of the GUI
        // (scrollable person list) ...
        Label labelPersonsList = new Label("Persons:");

        personsPane = new GridPane();
        personsPane.setPadding(new Insets(5));
        personsPane.setHgap(5);
        personsPane.setVgap(5);

        ScrollPane scrollPane = new ScrollPane(personsPane);
        scrollPane.setMinWidth(300);
        scrollPane.setMaxWidth(300);
        scrollPane.setMinHeight(300);
        scrollPane.setMaxHeight(300);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // ... and adds these elements to the right-hand columns of
        // the grid pane
        VBox personsList = new VBox(labelPersonsList, scrollPane);
        personsList.setSpacing(5.0);
        this.add(personsList, 1, 0);

        // updates the values of the different components with the values from
        // the stack
        update();
    }

    /**
     * Updates the values of the GUI elements with the current values
     * from the list.
     */
    private void update() {
        double sumOfWeights = 0;
        Map<String, Integer> nameCounter = new HashMap<>();
        personsPane.getChildren().clear();
        // adds all persons to the list in the personsPane (with
        // a delete button in front of it)
        for (int i=0; i < persons.size(); i++) {
            Person person = persons.get(i);
            sumOfWeights += person.weight;

            if (nameCounter.containsKey(person.name)){
                int currentCount = nameCounter.get(person.name);
                nameCounter.put(person.name, currentCount + 1);
            } else {
                nameCounter.put(person.name, 1);
            }

            Label personLabel = new Label(i + ": " + person.toString());
            Button deleteButton = new Button("Delete");
            deleteButton.setOnAction(
                    e -> {
                        persons.remove(person);
                        update();
                    }
            );
            HBox entry = new HBox(deleteButton, personLabel);
            entry.setSpacing(5.0);
            entry.setAlignment(Pos.BASELINE_LEFT);
            personsPane.add(entry, 0, i);
        }

        // Frequent names
        int maxCount = 0;
        String mostFrequentName = "";

        for (Map.Entry<String, Integer> entry: nameCounter.entrySet()){
            if (entry.getValue() > maxCount){
                maxCount = entry.getValue();
                mostFrequentName = entry.getKey();
            }
        }

        // Gennemsnit
        //double average = 0;
//        if (!persons.isEmpty()){
//            average = (double) sumOfWeights/persons.size();
//        }

        // Nye gennemsnit
        double average = persons.stream()
                .mapToDouble(Person::getWeight)
                .average()
                .orElse(0.0);

        // min og max af alder
        int min = persons.stream().map(Person :: getAge).reduce(Integer :: min).orElse(0);
        int max = persons.stream().map(Person :: getAge).reduce(Integer :: max).orElse(0);

        averageWeight.setText("Average weight: " + average);
        freqName.setText("Frequently used name: " + mostFrequentName);

        minAge.setText("Minimum age: " + min);
        maxAge.setText("Maximum age: " + max);
    }

    // TODO this GUI could be extended by some additional widgets for issuing other
    //      operations of lists. And the possibly thrown exceptions should be caught
    //      in the event handler (and possibly shown in an additional text area for
    //      exceptions; see Assignment 2).

}
