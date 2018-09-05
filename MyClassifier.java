import java.util.*;
import java.lang.*;
import java.io.*;

//Name: Nicholas Zaidan
//SID: 450158298
//Unikey: nzai0011

//Name: William Smith
//SID: 310275350
//Unikey: wsmi6415


public class MyClassifier{
    //Global classifierInstance variable which stores the complete list of testing data and training data
    static ClassifierCalculation classifierInstance = new ClassifierCalculation();

    public static void main(String[] args) throws FileNotFoundException{
        //Read file function
        readFile(args);

        int iteration = args[2].charAt(0) - '0'; //The amount of time KNN iterates
        switch (args[2]) {
            case "NB": //Run naivebeyes in MyClassifier
                naiveBeyes();
                break;
            case "CFS":
                stratifier(args);
                break;
            default:
                classifierInstance.naiveBeyes(iteration); //
                kNearestNeighbours(iteration); //Run KNN
                displayClass();
                break;
        }

    }
    //Comparison function for values
    public static boolean comparison(double yes, double no){
        return yes >= no;
    }

      public static void stratifier(String[] args) throws FileNotFoundException{
      PrintWriter pw = new PrintWriter(new File("test1.csv"));
      StringBuilder sb = new StringBuilder();
      Scanner trainingFile = new Scanner(new File (args[0])); //Inputs training data
      ClassifierCalculation tempHolder = new ClassifierCalculation();
      ClassifierTraining tempSet;
      ClassifierTraining tempSet2;
      ArrayList<ClassifierTraining> yes = new ArrayList<>();
      ArrayList<ClassifierTraining> no = new ArrayList<>();
      int x;

      while(trainingFile.hasNextLine()){
        String data[] = trainingFile.nextLine().split(","); //line is split into array
        x = data.length;
        tempSet = new ClassifierTraining(x); //Override the training data
        int i = 0;
        int columnLength = tempSet.collection.length; //Column length  var
        while (i < columnLength) {

            //If not the last column, place double into holder object
            if( i != columnLength - 1){
                tempSet.collection[i] = Double.parseDouble(data[i]);
            }
            //If it is the last column, transfer string into a value where 0 = No and 1 = Yes
            else if(i == columnLength - 1){

                switch (data[i]) {
                    case "yes": //If yes set to 1
                        tempSet.collection[i] = 1;
                        break;
                    case "no": //If no set to 0
                        tempSet.collection[i] = 0;
                        break;
                }

            }

            i++;
        }
        if(tempSet.getClassVariable() == 1){
          yes.add(tempSet);
        }
        else if(tempSet.getClassVariable() == 0){
          no.add(tempSet);
        }
      }

      double totalSizeOfPima = yes.size() + no.size();
      int entriesPerFold = (int)(Math.round(totalSizeOfPima/10));

      for(int i = 0; i < 10; i++){
        double[] holdingData;
        int foldNumber = i + 1;
        sb.append("fold" + foldNumber +"\n");
        int counterForEntries = 0;
        while(yes.size() != 0 || no.size() != 0){
          if(counterForEntries == 34){
            break;
          }
          if(no.size() != 0){
            tempSet2 = no.remove(0);
            holdingData = tempSet2.getFullDataCollection();

            for(int j = 0;  j < holdingData.length; j++){
              if(j == holdingData.length - 1){
                sb.append("no");
              }
              else{
                sb.append(holdingData[j] + ",");
              }
            }
            sb.append("\n");
          }

          if(yes.size() != 0){
            tempSet = yes.remove(0);
            holdingData = tempSet.getFullDataCollection();

            for(int j = 0; j < holdingData.length; j++ ){
              if(j == holdingData.length - 1){
                sb.append("yes");
              }
              else{
                sb.append(holdingData[j] + ",");
              }
            }
            sb.append("\n");
          }
          counterForEntries++;
        }
        sb.append("\n");
      }
      System.out.println(yes.size() + " " + no.size());
      pw.write(sb.toString());
      pw.close();
    }

    //The Naive Beyes function which determines whether the compututional yes or computional no is higher, and prints accordingly
    public static void naiveBeyes(){
        List<ClassifierTesting> list = classifierInstance.getTestingData();

        for (ClassifierTesting aList : list) {
            double totalNo = classifierInstance.computeNo(aList.getFullDataCollection()); //Compute the Naive Beyes for No
            double totalYes = classifierInstance.computeYes(aList.getFullDataCollection()); //Compute the Naive Beyes for Yes
            if (comparison(totalYes, totalNo)) { //If Yes is bigger than No
                System.out.println("Yes");
            } else if (!comparison(totalYes, totalNo)) { //If No is bigger than Yes
                System.out.println("No");
            }
        }
    }

    //Print the class variable
    public static void displayClass(){
        List<ClassifierTesting> testers = classifierInstance.getTestingData();
        for (ClassifierTesting tester : testers) {
            if (tester.getClassVariable() == 0) {
                System.out.println("No");
            } else {
                if (tester.getClassVariable() == 1) {
                    System.out.println("Yes");
                }
            }

        }
    }
    public static void kNearestNeighbours(int k){
        List<ClassifierTraining> trainers = classifierInstance.getTrainingSet(); //List of trainers
        List<ClassifierTesting> testers = classifierInstance.getTestingData(); //List of tests

        for (int i = 0; i < testers.size(); i++) {
            for (int j = 0; j < trainers.size(); j++) {
                //Runs the distance calculation  using the trainer row of j in against the testing row of i
                double dist = distanceEuclidean(trainers.get(j).getFullDataCollection(), testers.get(i).getFullDataCollection());
                //Afterwards, set the KNN arraylist inside the Testing object
                testers.get(i).calcKNN(dist, trainers.get(j).getClassVariable(), k);
            }
            testers.get(i).getClassVariable();
        }
    }
    //Calculate the distance formula to be used in the kNearestNeighbours function
    public static double distanceEuclidean(double[] trainingData, double[] testingData){
        double distance = 0;
        int i = 0;
        if (i < testingData.length) {
            do {
                distance = distance + Math.pow(trainingData[i] - testingData[i], 2);
                i++;
            } while (i < testingData.length);
        }
        distance = Math.sqrt(distance);
        return distance;
    }

    //Gathering and setting the data
    public static void readFile(String[] args) throws FileNotFoundException{
        Scanner trainingFile = new Scanner(new File (args[0])); //Inputs training data
        Scanner testingFile = new Scanner(new File (args[1])); //Inputs testing data
        ClassifierTraining tempSet; //Dataset holder
        ClassifierTesting tempTestSet; //Dataset holder
        int x;
        //While the training file has lines
        while(trainingFile.hasNextLine()){
            String data[] = trainingFile.nextLine().split(","); //line is split into array
            x = data.length;
            tempSet = new ClassifierTraining(x); //Override the training data
            int columnLength = tempSet.collection.length; //Column length  var

            //Columns
            int i = 0;
            while (i < columnLength) {

                //If not the last column, place double into holder object
                if( i != columnLength - 1){
                    tempSet.collection[i] = Double.parseDouble(data[i]);
                }
                //If it is the last column, transfer string into a value where 0 = No and 1 = Yes
                else if(i == columnLength - 1){

                    switch (data[i]) {
                        case "yes": //If yes set to 1
                            tempSet.collection[i] = 1;
                            break;
                        case "no": //If no set to 0
                            tempSet.collection[i] = 0;
                            break;
                    }

                }

                i++;
            }
            //Add the set to the classifier instance
            classifierInstance.addTrainingSet(tempSet);
        }

        while(testingFile.hasNextLine()){
            String[] data = testingFile.nextLine().split(",");

            x = data.length;
            tempTestSet = new ClassifierTesting(x);
            int columnLength = tempTestSet.collection.length;

            int i = 0;
            while (i < columnLength) {
                tempTestSet.collection[i] = Double.parseDouble(data[i]);
                i++;
            }
            classifierInstance.addTestingSet(tempTestSet);
        }
    }
}


class ClassifierTraining{

    double[] collection;

    //Constructor for setting array for columns
    ClassifierTraining(int length){
        this.collection = new double[length];
    }
    public int getAmountOfColumns(){
      return this.collection.length;
    }
    //Returning the full collection
    public double[] getFullDataCollection(){
        return this.collection;
    }

    //Return the class variable (1 or 0)
    public double getClassVariable(){
        return collection[collection.length-1];
    }
}

class ClassifierTesting{

    double classVariable;
    int k;
    double[] collection;
    ArrayList<double[]> knn = new ArrayList<>();

    //Constructor for setting the length for columns
    ClassifierTesting(int length){
        this.collection = new double[length];
    }

    //Returns the list for all the columns
    public double[] getFullDataCollection(){
        return this.collection;
    }

    //Calculating the kNearestNeighbours through a comparator and arraylist.
    void calcKNN(double dist, double classVariable, int k){
        this.k = k;
        double[] temp = {dist, classVariable};
        knn.add(temp);

        knn.sort(Comparator.comparingDouble(o -> o[0]));
        // check
        if (this.knn.size() > k) {
            do {
                this.knn.remove(knn.size() - 1);
            } while (this.knn.size() > k);
        }
    }

    //Return 1 or 0 depending on the class variable
    double getClassVariable(){
        int index = 0;
        for (int i1 = 0, knnSize = knn.size(); i1 < knnSize; i1++) {
            double[] i = knn.get(i1);
            if (i[1] == 1) index++;
        }
        classVariable = index >= (k + 1) / 2 ? 1 : 0;
        return this.classVariable;
    }

}

class ClassifierCalculation{
    List<ClassifierTraining> trainerComplete = new ArrayList<>();
    List<ClassifierTesting> testerComplete = new ArrayList<>();
    static double totalYes = 0;
    static double totalNo = 0;
    static double totalSize = 0;

    boolean naiveBeyes;
    int k;
    int[] kNearestNeighbours;

    void addTrainingSet(ClassifierTraining x){
        trainerComplete.add(x);
    }
    //Setting the total amount of entries in the set
    void setTotalSize(){
        this.totalSize = trainerComplete.size();
    }

    //Setting total number of yes's in the set
    void setTotalYes(){
        int index = 0;
        for (ClassifierTraining aTrainerComplete : trainerComplete) {
            if (aTrainerComplete.getClassVariable() == 1) {
                index++;
            }
        }
        this.totalYes = index;
    }

    //Setting total number of no's in the set
    void setTotalNo(){
        int index = 0;
        for (ClassifierTraining aTrainerComplete : trainerComplete) {
            if (aTrainerComplete.getClassVariable() == 0) {
                index++;
            }
        }
        this.totalNo = index;
    }

    //Return the set of Training Classifiers
    List<ClassifierTraining> getTrainingSet(){
        return this.trainerComplete;
    }

    //Return the set of Testing Classifiers
    List<ClassifierTesting> getTestingData(){
        return this.testerComplete;
    }
    //Add the Testing object to the list
    void addTestingSet(ClassifierTesting x){
        testerComplete.add(x);
    }

    //Setting values for functions
    void naiveBeyes(int k){

        // iterations are complete, set bool and cease
        switch (k) {
            case -1:
                this.naiveBeyes = true;
                break;
            default:
                this.k = k;
                this.naiveBeyes = false;
                this.kNearestNeighbours = new int[k];
                break;
        }
    }

    //Compute values for the Naive Beyes, specifically for yes
    double computeYes(double[] data){
        setTotalYes();
        setTotalSize();
        double value = totalYes/totalSize;
        int i = 0;
        while (i < data.length) {
            value *= rowCalculation(1, data[i], i);
            i++;
        }

        return value;
    }

    //Compute values for the Naive Beyes, specifically for no
    double computeNo(double[] data){
        setTotalNo();
        setTotalSize();
        double value =  totalNo/totalSize;
        int i = 0;
        while (i < data.length) {
            value *= rowCalculation(0, data[i], i);
            i++;
        }

        return value;
    }

    //Runs first part of the Nieve Beyes calculation
    double rowCalculation(int classVariable, double x, int index){
        double sum = 0;
        int n = 0; //To find total yes or no

        for (int i = 0, trainerCompleteSize = trainerComplete.size(); i < trainerCompleteSize; i++) {
            ClassifierTraining t = trainerComplete.get(i);
            double[] dataCollection = t.getFullDataCollection();
            double classCollection = t.getClassVariable();
            //Method to help determine the mean
            switch (classVariable) {
                case 1: //If yes
                    if (classCollection == 1) {
                        sum += dataCollection[index];
                        n++;
                    }
                    break;
                default: //If no
                    if (classCollection == 0) {
                        sum += dataCollection[index];
                        n++;
                    }
                    break;
            }
        }
        //Creating the mean
        double mean = sum/n;
        double diffSquared = 0;

        //Help create the standard deviation
        for (int i = 0, trainerCompleteSize = trainerComplete.size(); i < trainerCompleteSize; i++) {
            ClassifierTraining t = trainerComplete.get(i);

            double[] dataCollection = t.getFullDataCollection();
            double classCollection = t.getClassVariable();

            switch (classVariable) {
                case 1:
                    if (classCollection == 1) {
                        diffSquared += Math.pow((dataCollection[index] - mean), 2);
                    }
                    break;
                default:
                    if (classCollection == 0) {
                        diffSquared += Math.pow((dataCollection[index] - mean), 2);
                    }
                    break;
            }
        }
        //Setting the standard deviation
        double diffDivide = Math.sqrt(diffSquared/(n-1));
        // probability calculations
        double calc1 = 1/(diffDivide * (Math.sqrt (2*Math.PI) ));
        double calc2 = Math.exp(-1 * (Math.pow(x - mean, 2) / (2*Math.pow(diffDivide, 2))));
        return calc1*calc2;

    }
}
