import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Find records in a csv file that are possible duplicate
 */
public class FindDuplicates {

  private static final int TOLERANCE = 50;

  public static void main(String[] args) {
    
    if (args.length != 1) {
      System.out.println("Usage: FindDuplicates [filename of csv]");
      return;
    }
    //gets data from file
    List<List<String>> table = parseCsv(args[0]);

    if (table.size() == 0 || table.get(0).size() == 0) {
      return; //returns if file is malformed
    }

    table.remove(0);//remove the header

    //generate a table of weights (heavier weight on distance between names)
    WeightTable weights = new WeightTable(table.get(0).size());
    weights.setWeight(1, 5);
    weights.setWeight(2, 5);

    List<List<List<String>>> possibleDups = new ArrayList<>();
    double distance;
    boolean hadDup;
    List<List<String>> set = new ArrayList<>();

    //compare each row to all other rows to look for dups
    for (int i = 0; i < table.size() - 1; i++) {
      hadDup = false;
      for (int j = i + 1; j < table.size(); j++) {
        //if distance is less than tolerance remove from table and add to different list
        distance = findDistanceBetweenRecords(table.get(i), table.get(j), weights);
        if (distance < TOLERANCE) {
          set.add(table.remove(j));
          j--;
          hadDup = true;
        }
      }

      //if this row itself had a duplicate remove it from the table and add it to a different list
      if (hadDup) {
        set.add(table.remove(i));
        possibleDups.add(set);
        set = new ArrayList<>();
        i--;
        hadDup = false;
      }
    }
    //at this point only non duplicate entries should be in table

    //print the duplicates
    System.out.println("Possible Duplicate Entries: ");
    int i = 1;
    for (List<List<String>> s: possibleDups) {
      System.out.println("Set " + i);
      i++;
      for (List<String> record : s) {
        System.out.println(record);
      }
    }

    //print the non duplicates
    System.out.println("\nNon-duplicate Entries: ");
    for (List<String> record : table) {
      System.out.println(record);
    }
  }

  /**
   * Sums the distances of all the levenshtein distances between two strings with the same index in
   * a list
   *
   * @param a a list of strings
   * @param b a list of strings
   * @param w a weight table
   * @return a distance between the two given lists
   */
  private static double findDistanceBetweenRecords(List<String> a, List<String> b, WeightTable w) {
    double sum = 0;
    if (a.size() > b.size()) {
      List<String> temp = a;
      a = b;
      b = temp;
    }
    if (a.size() < 1) {
      throw new IllegalArgumentException("Rows must contain at least one column.");
    }
    for (int i = 1; i < a.size(); i++) {
      sum += (levenDistance(a.get(i), b.get(i)) * w.getWeight(i));
    }
    return sum;
  }

  /**
   * Parses the CSV file from the given path into a List of List of String
   *
   * @param path file to parse
   * @return a list of rows where each row is a list of strings
   */
  private static List<List<String>> parseCsv(String path) {
    Scanner sc = null;
    try {
      sc = new Scanner(new File(System.getProperty("user.dir") + "/" + path)).useDelimiter("\n");
    } catch (IOException e) {
      System.out.println("File path not found.");
      return new ArrayList<>();
    }
    List<List<String>> table = new ArrayList<>();
    while (sc.hasNext()) {
      //found the regex to split strings on ',' but ignoring in quotes on stack overflow
      table.add(Arrays.asList(sc.next().split(",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)")));
    }
    return table;
  }

  /**
   * Calculates the Levenshtein distance between two strings. Taken from rosettacode.org
   *
   * @param a a String
   * @param b another String
   * @return the Levenshtein distance between a and b
   */
  private static int levenDistance(String a, String b) {
    a = a.toLowerCase();
    b = b.toLowerCase();
    // i == 0
    int[] costs = new int[b.length() + 1];
    for (int j = 0; j < costs.length; j++)
      costs[j] = j;
    for (int i = 1; i <= a.length(); i++) {
      // j == 0; nw = lev(i - 1, j)
      costs[0] = i;
      int nw = i - 1;
      for (int j = 1; j <= b.length(); j++) {
        int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
        nw = costs[j];
        costs[j] = cj;
      }
    }
    return costs[b.length()];
  }

  //class for holding the weights of an index in an array
  private static class WeightTable {
    double[] weights;

    public WeightTable(int size) {
      this.weights = new double[size];
      for (int i = 0; i < size; i++) {
        this.weights[i] = 1.0;
      }
    }

    public void setWeight(int index, double weight) {
      this.weights[index] = weight;
    }

    public double getWeight(int index) {
      return weights[index];
    }
  }
}