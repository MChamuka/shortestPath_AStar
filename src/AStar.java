//M.W.C Umesha- 20221519-w1956430

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class AStar {
    static List<Node> visitedNodes = new ArrayList<>();
    public static void main(String[] args) {
        ArrayList<String> finalOutcome=new ArrayList<>();
        System.out.print("Enter the file name: ");
        Scanner scanner = new Scanner(System.in);   //to take user input
        String file_name = scanner.nextLine();
        try {
            //below is to read the text file and get those data into a 2D array
            BufferedReader reader = new BufferedReader(new FileReader(file_name));

            int numRows = 0;
            int numCols = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                numRows++;
                if (line.length() > numCols) {
                    numCols = line.length();
                }
            }
            reader.close();   //closing the reader which is used to read from file
            char[][] maze = new char[numRows][numCols];    // declaring a 2d array to store the data read from file

            reader = new BufferedReader(new FileReader(file_name));
            int row = 0;
            while ((line = reader.readLine()) != null) {
                char[] characters = line.toCharArray();
                for (int col = 0; col < characters.length; col++) {
                    maze[row][col] = characters[col];
                }
                row++;
            }
            reader.close();

            // below codes are used to identify the rocks, ice, and Start and the finish
            int sRow = -1;
            int sCol = -1;
            int fRow = -1;
            int fCol = -1;

            for (int i = 0; i < maze.length; i++) {
                for (int j = 0; j < maze[i].length; j++) {   // Loop through columns
                    if (maze[i][j] == 'S') {        // to identify the 'Start node'
                        sRow = i;
                        sCol = j;
                    }
                    if (maze[i][j] == 'F') {      // to identify the 'end node'
                        fRow = i;
                        fCol = j;
                    }
                }
                if (sRow != -1 && sCol != -1 && fRow != -1 && fCol != -1) {
                    break;
                }
            }
            int startRow = sRow;
            int startCol = sCol;
            int finalRow = fRow;
            int finalCol = fCol;

            long startTime = System.currentTimeMillis(); //to store the start time


            int[][] shortestPath = astar(maze, startRow, startCol, finalRow, finalCol);


            if (shortestPath != null) {
                System.out.println("Shortest path from start to destination:");
            } else {
                System.out.println("No path found from start to destination");
            }

            //to print the final outcome
            finalOutcome=printCoordinates(visitedNodes.get(0),finalOutcome);
            Collections.reverse(finalOutcome);
            int x=startRow;
            int y=startCol;
            for (int i=0;i<finalOutcome.size();i++){
                String[]parts=finalOutcome.get(i).split(",");
                if(i==0){
                    System.out.println((i + 1) + ". Starts at (" + finalOutcome.get(i)+")");
                } else if(Integer.parseInt(parts[0])>x) {
                    System.out.println((i + 1) + ". move right to (" + finalOutcome.get(i)+")");
                } else if (Integer.parseInt(parts[0])<x) {
                    System.out.println((i + 1) + ". move left to (" + finalOutcome.get(i)+")");
                } else if (Integer.parseInt(parts[1])>y) {
                    System.out.println((i + 1) + ". move down to (" + finalOutcome.get(i)+")");
                } else if (Integer.parseInt(parts[1])<y) {
                    System.out.println((i + 1) + ". move up to (" + finalOutcome.get(i)+")");
                }else{
                    System.out.println((i + 1) + ". Start at (" + finalOutcome.get(i)+")");
                }
                x=Integer.parseInt(parts[0]);
                y=Integer.parseInt(parts[1]);
            }
            System.out.println((finalOutcome.size()+1)+". Move finally to("+(finalCol+1)+","+(finalRow+1)+")");
            System.out.println((finalOutcome.size()+2)+". Done!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // below is to implement the A* algorithm
    public static int[][] astar(char[][] maze, int startRow, int startCol, int finalRow, int finalCol) {
        int rows = maze.length;
        int cols = maze[0].length;

        int[][] dist = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            Arrays.fill(dist[i], Integer.MAX_VALUE);
        }
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<Node> closedSet = new HashSet<>();

        dist[startRow][startCol] = 0;

        Node startNode = new Node(startRow, startCol, 0, heuristic(startRow, startCol, finalRow, finalCol));
        openSet.offer(startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            visitedNodes.clear();
            visitedNodes.add(current);
            if (current.row == finalRow && current.col == finalCol) {
                return reconstructPath(current, rows, cols);
            }
            closedSet.add(current);

            //for moving up
            int neighbourRow = current.row;
            int weightCount = 0;
            //changed this sewin
            while (neighbourRow > 0 && maze[neighbourRow - 1][current.col] != '0') {
                if (maze[neighbourRow - 1][current.col] == 'F') {
                    Node finishNode = new Node(neighbourRow - 1, current.col, current.gCost + weightCount, heuristic(neighbourRow - 1, current.col, finalRow, finalCol));
                    finishNode.parent = current;
                    return reconstructPath(finishNode, rows, cols);
                }
                neighbourRow -= 1;
                weightCount += 1;
            }
            Node upNeighbor = new Node(neighbourRow, current.col, current.gCost + weightCount, heuristic(neighbourRow, current.col, finalRow, finalCol));

            //for moving down
            neighbourRow = current.row;
            weightCount = 0;
            while (neighbourRow < maze.length-1 && maze[neighbourRow + 1][current.col] != '0') {
                if (maze[neighbourRow + 1][current.col] == 'F') {
                    Node finishNode = new Node(neighbourRow + 1, current.col, current.gCost + weightCount, heuristic(neighbourRow + 1, current.col, finalRow, finalCol));
                    finishNode.parent = current;
                    return reconstructPath(finishNode, rows, cols);
                }
                neighbourRow += 1;
                weightCount += 1;
            }
            Node downNeighbor = new Node(neighbourRow, current.col, current.gCost + weightCount, heuristic(neighbourRow, current.col, finalRow, finalCol));

            //for moving left
            int neighbourCol = current.col;
            weightCount = 0;
            while (neighbourCol > 0 && maze[current.row][neighbourCol - 1] != '0') {
                if (maze[current.row][neighbourCol - 1] == 'F') {
                    Node finishNode = new Node(current.row, neighbourCol - 1, current.gCost + weightCount, heuristic(current.row, neighbourCol - 1, finalRow, finalCol));
                    finishNode.parent = current;
                    return reconstructPath(finishNode, rows, cols);
                }
                neighbourCol -= 1;
                weightCount += 1;
            }
            Node leftNeighbor = new Node(current.row, neighbourCol, current.gCost + weightCount, heuristic(current.row, neighbourCol, finalRow, finalCol));

            //for moving right
            neighbourCol = current.col;
            weightCount = 0;
            while (neighbourCol < maze[0].length-1 && maze[current.row][neighbourCol + 1] != '0') {
                if (maze[current.row][neighbourCol + 1] == 'F') {
                    Node finishNode = new Node(current.row, neighbourCol + 1, current.gCost + weightCount, heuristic(current.row, neighbourCol + 1, finalRow, finalCol));
                    finishNode.parent = current;
                    return reconstructPath(finishNode, rows, cols);
                }
                neighbourCol += 1;
                weightCount += 1;
            }
            Node rightNeighbor = new Node(current.row, neighbourCol, current.gCost + weightCount, heuristic(current.row, neighbourCol, finalRow, finalCol));

            Node[] neighbors = {upNeighbor, downNeighbor, leftNeighbor, rightNeighbor};
            for (Node neighbor : neighbors) {
                if (!closedSet.contains(neighbor) && neighbor.gCost < dist[neighbor.row][neighbor.col]) {
                    neighbor.parent = current;
                    dist[neighbor.row][neighbor.col] = neighbor.gCost;
                    openSet.offer(neighbor);
                }
            }
        }
        return null;
    }
    //below is to calculate the heuristic value for the algorithm
    public static int heuristic(int currentRow, int currentCol, int finalRow, int finalCol) {
        return (int) (Math.sqrt(Math.pow((currentRow - finalRow), 2) + Math.pow((currentCol - finalCol), 2)));
    }
    //this is get the reconstruct path
    public static int[][] reconstructPath(Node node, int rows, int cols) {
        int[][] path = new int[rows][cols];
        Node current = node;
        while (current != null) {
            path[current.row][current.col] = 1;
            current = current.parent;
        }
        return path;
    }
    //this method helps in diplaying the final outcome
    public static ArrayList<String> printCoordinates(Node node, ArrayList<String> finalOutcome) {
        while (node != null) {
            finalOutcome.add((node.col + 1) + "," + (node.row + 1));
            node = node.parent;
        }
        return finalOutcome;
    }
}