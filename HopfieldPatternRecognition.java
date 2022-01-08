
// Date: Nov. 9, 2021

// Description: This is a hopfield net, used trained to recognise bitmap images

import java.io.*; // Import this class to handle errors
import java.util.Arrays; // Import Arrays
import java.util.Random; // Import the Random Class
import java.util.Scanner; // Import the Scanner class to read text files

//Main function

    // ask the user whether they want to train or deploy

//Helper function to read file for training

//Helper function to read file for testing

public class HopfieldPatternRecognition
{
  public static void main(String[] args) {
    String Answer = "Yes";
    Scanner keyboard = new Scanner(System.in);
    
    while (Answer.equalsIgnoreCase("Yes")) 
    { // while loop to run program again
        try 
        {
            BufferedReader br;
            File file = null;
            File testFile = null;
            // variable declaration 
            System.out.println("Welcome to my second neural network - A Hopfield Autonet!");
            System.out.print("Enter 1 to train a net, enter 2 to test the net: ");

            int initFileChoice = Integer.parseInt(keyboard.nextLine());

            if(initFileChoice == 1)
            {
                // ask user for trainingFile
                System.out.print("What is the name of the file with the training data: ");
                String trainingFile = keyboard.nextLine();
                training(trainingFile);
            }
            if(initFileChoice == 2)
            {
                // ask user for testFile and trainedWeightsFile
                System.out.print("What is the name of the file with the testing data: ");
                String testingFile = keyboard.nextLine();
                System.out.print("Enter the name of the file with trained weights: ");
                String trainedWeightsFile = keyboard.nextLine();
                testing(testingFile, trainedWeightsFile);
            }
        } // opening try block close
        catch (Exception e)
        {  
            e.printStackTrace();
        } // catch block close
        System.out.println("Continue? (Yes/No)");
        Answer = keyboard.nextLine();
    } // while loop close
    keyboard.close();
  } // main method close


    //helper function to read file for testing
    public static void training(String filename)
    {
        // variable declaration
        int[][] tempMatrix = null;
        int[][] trainedMatrix = null;
        int[][] weightSet = null;
        // read training file filename
        
        File file = new File(filename);
        String imageDimensionString;
        String[] tokensDimension;
        int imageDimension = 0;
        String numImagesString;
        String[] tokensNumImages;
        int numImages = 0;
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));

            imageDimensionString = br.readLine();
            tokensDimension = imageDimensionString.split("\\s");
            imageDimension = Integer.parseInt(tokensDimension[0]);

            numImagesString = br.readLine();
            tokensNumImages = numImagesString.split("\\s");
            numImages = Integer.parseInt(tokensNumImages[0]);
        }catch(Exception t)
        {
           t.printStackTrace();
        }
        weightSet = new int[numImages][imageDimension];
        read(weightSet, numImages, imageDimension, file);
        //System.out.print(Arrays.toString(weightSet[0]));
        tempMatrix = new int[imageDimension][imageDimension];
        trainedMatrix = new int[imageDimension][imageDimension];
        
        //for each training set
        for(int x = 0; x < weightSet.length; x++)
        {
            //set times set transposed
            for(int i = 0; i < weightSet[0].length; i++)
            {
                for(int j = 0; j < weightSet[0].length; j++)
                {
                    tempMatrix[i][j] = weightSet[x][i]*weightSet[x][j];
                }
            }
            //save to trained matrix
            for(int i = 0; i < weightSet[0].length; i++)
            {
                for(int j = 0; j < weightSet[0].length; j++)
                {
                    trainedMatrix[i][j] += tempMatrix[i][j];
                }
            }
        }
        //set self referances to 0
        for(int i = 0; i < weightSet[0].length; i++)
        {
                trainedMatrix[i][i] = 0;
        }
        //write results
        System.out.print("What file should we write the weights to: ");
        Scanner keyboard = new Scanner(System.in);
        String weightWriteFile = keyboard.nextLine();
        writeWeightsToFile(trainedMatrix, weightWriteFile, weightSet);
    }

    //helper function to read file for training
    public static void testing(String filename, String weightsFile)
    {
       //variables
       int[] xInput = null;
       int[] yOutput = null;
       int epochs = 0;
       int yIn = 0;
       boolean change = false;
       int[][] testSet = null;
       int[][] trainedWeights = null;
       boolean converged = false;
       boolean [] visitedPoints = null;
       int randomIndex = 0;
       int yA = 0;
       int [][] patterns = null;

    // read testSet from testing file
    // ReturningAnArray obj = new ReturningAnArray();
    // read training file filename

        try{
            File results = new File(filename);
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String imageDimensionString = br.readLine();
            String[] tokensDimension = imageDimensionString.split("\\s");
            int imageDimension = Integer.parseInt(tokensDimension[0]);
            xInput = new int[imageDimension];
            yOutput = new int[imageDimension];
            

            String numImagesString = br.readLine();
            String[] tokensNumImages = numImagesString.split("\\s");
            int numImages = Integer.parseInt(tokensNumImages[0]);

            // read in testing file
            testSet = new int[numImages][imageDimension];
            read(testSet, numImages, imageDimension, results);           

            // read first line of trained weights file with dimensions
            File weightFile = new File(weightsFile);
            br = new BufferedReader(new FileReader(weightFile));
            String firstLine = br.readLine();
            int dimension = Integer.parseInt(firstLine);
            trainedWeights = new int[dimension][dimension];
            String nextLine = br.readLine();
            int numStoredPatterns = Integer.parseInt(nextLine);
            patterns = new int[numStoredPatterns][imageDimension];
            readWeightsFromFile(weightFile, trainedWeights, patterns);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
       
       for (int k = 0; k < testSet.length; k++) // k = numImages
       {
        //set xInput equal to testSet
        for(int t = 0; t < testSet[k].length; t++) // t = imageDimension
        {
            xInput[t] = testSet[k][t];
        }
        //set xi = yi (xi is input string of 100)
        for(int i = 0; i < xInput.length; i++)
            {yOutput[i] = xInput[i];}
        //epoch
        epochs = 0;
        converged = false;
        while(!converged)
        {
                epochs++;
                change = false;
                //initialize all points to false
                visitedPoints = new boolean[trainedWeights.length];
                for(int i = 0; i < visitedPoints.length; i++){
                    visitedPoints[i] = false;
                }
                //for each i (100 times)
                for(int i = 0; i < xInput.length; i++) 
                {
                    //select a random point 
                    do
                    {
                         randomIndex = (int)(Math.random() * trainedWeights.length);
                    } while (visitedPoints[randomIndex]);
                    visitedPoints[randomIndex] = true;
                    //calcualte yin of i
                    yIn = xInput[i];
                    for(int j = 0; j < xInput.length; j++) // 100
                    {
                        yIn += yOutput[j]*trainedWeights[j][i];
                    }
                    //activation func.
                    if(yIn < 0)
                    {
                        yA = -1;
                    }
                    else if (yIn > 0)
                    {
                        yA = 1;
                    }
                    else
                    {
                        yA = yOutput[i];
                    }
                    //broadcast (change yi to new value)
                    if(yOutput[i] != yA)
                    {
                        change = true;
                    }
                    yOutput[i] = yA;
                }
                //check if converged
                if(!change){converged = true;}
                if(!converged)
                {
                    for (int i = 0; i < xInput.length; i++)
                    {xInput[i] = yOutput[i];}
                }
        }
        //compare yOutput to Known patterns
        boolean match = false;
        for(int c = 0; c < patterns.length; c++)
        {
            boolean diff = false;
            for(int i = 0; i < patterns[0].length; i++)
            {
                if(patterns[c][i] != yOutput[i]) 
                {
                    diff = true;
                }
            }
            if(!diff) // if diff = false, meaning yOutput matches a known pattern
            {
                System.out.println("Matches pattern : " + c);
                match = true;
            }
        }
        if(!match) // if none of the outputs match known patterns
        {System.out.println("No pattern match");} 
       }

    }//end of testing method
    
    public static void writeWeightsToFile(int[][] weights, String filename, int[][] patterns)
    {
        try{
            File writeFile = new File(filename);
            BufferedWriter bw = new BufferedWriter(new FileWriter(writeFile));
            if(!writeFile.exists()){
                writeFile.createNewFile();
            }
            bw.write(weights.length + "\n");
            bw.write(patterns.length + "\n");
            bw.write("\n");
            for(int i = 0; i < weights.length; i++){
                for(int j = 0; j < weights[0].length; j++)
                    bw.write(weights[i][j] + ",");
                bw.write("\n");
            }
            bw.write("Known Patterns\n");
            for(int i = 0; i < patterns.length; i++) // numImages
            {
                for(int j = 0; j < patterns[0].length;j++) // imageDimension
                {
                    bw.write(patterns[i][j] + ",");
                }
                bw.write("\n");
            }
            bw.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }//end of writeWeightsToFile

    public static void readWeightsFromFile(File filename, int[][] trainedWeights, int[][] patterns)
    {
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            br.readLine(); // read dimension
            br.readLine(); // read num
            br.readLine(); //read blank
            String currentLine = br.readLine();
            int count = 0;
            //read in weights
            while (!currentLine.equals("Known Patterns"))
            {
                String[] currentTokenLine = currentLine.split("\\s*,\\s*");
                for (int t = 0; t < trainedWeights[0].length; t++)
                {
                    trainedWeights[count][t] = Integer.parseInt(currentTokenLine[t]);
                }
                currentLine = br.readLine();
                count++;
            }
            //read in known patterns
            currentLine = br.readLine();
            count = 0;
            while (!currentLine.isEmpty())
            {
                String[] currentTokenLine = currentLine.split("\\s*,\\s*");
                for(int i = 0; i < patterns[1].length; i++) 
                {
                    patterns[count][i] = Integer.parseInt(currentTokenLine[i]);
                }
                currentLine = br.readLine();
                if(currentLine == null)
                {
                    currentLine = "";
                }
                count++;
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }//end of readWeightsFromFile
    
    public static void read(int[][] initSet, int numImages, int imageDimension, File filename)
    { 
        try
        {  
            char currentChar;
            BufferedReader br = new BufferedReader(new FileReader(filename));  
            br.readLine();// demensions
            br.readLine();// number of sets
            br.readLine();// blank line after dimensions
            for (int n = 0; n < numImages; n++) // for each image
            {
                int count = 0; // reset count to 0 at the beginning of each image
                String currentLine = br.readLine();
                while (!currentLine.isEmpty()) // while line is not blank
                {
                    //System.out.println(currentLine);
                    //String[] lineTokens = currentLine.split("",10); // split current line
                    //System.out.println(Arrays.toString(lineTokens));
                    for (int l = 0; l < currentLine.length(); l++) // for each token 
                    {
                        currentChar = currentLine.charAt(l);
                        if (currentChar == 'O')
                        {
                            //System.out.println(lineTokens[l]);
                            initSet[n][count] = 1;
                            count++; // count keeps place in imageDimension (initSet column)
                        }
                        else if (currentChar == ' ')
                        {
                            //System.out.println(lineTokens[l]);
                            initSet[n][count] = -1;
                            count++;
                        }
                        else
                        {
                            //System.out.println("NONE: " + lineTokens[l]);
                        }
                    }
                    currentLine = br.readLine();
                    if(currentLine == null)
                    {
                        currentLine = "";
                    }
                }// close while loop through individual image
            } // close for loop through numImages
        } // closes try block
        catch(IOException e)
        {
            e.printStackTrace();
        } // end of catch
    } //end of read 
} // proj2 class close