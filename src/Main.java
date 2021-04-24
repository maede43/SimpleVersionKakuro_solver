/*
    *
    * Title:  Kakuro Solver as CSP problem
    * Author: Maedeh Nadehi
    * Date:   April 2021
    *
*/
import java.util.Scanner;

public class Main {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        int columns = sc.nextInt();
        int rows = sc.nextInt();
        int board[][] = new int[rows][columns];
        // get puzzle board
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                board[i][j] = sc.nextInt();
            }
        }
        Problem CSP_problem = new Problem(rows, columns, board);
//        System.out.println(CSP_problem);
//        CSP_problem.printBoard();
        BacktrackingSearch bt = new BacktrackingSearch(CSP_problem);
        bt.search();
    }
}
