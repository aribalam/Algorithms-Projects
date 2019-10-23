package assignments.baseballelimination;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.BST;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.Queue;


import java.util.Arrays;

public class BaseballElimination {

    private final int maxVertex;

    private String[] teams;
    private int[] wins;
    private int[] losses;
    private int[] left;
    private int[][] games;

    private BST<String, Integer> teamVertices;
    private int[][] gameVertices;

    private final int source;
    private final int target;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        if (filename == null)
            throw new IllegalArgumentException();

        In in = new In(filename);
        int n = in.readInt();
        in.readLine();

        // Initialize all arrays
        teamVertices = new BST<>();
        gameVertices = new int[n][n];

        teams = new String[n];
        wins = new int[n];
        losses = new int[n];
        left = new int[n];
        games = new int[n][n];

        // Process input
        for (int i = 0; i < n; i++) {
            String[] vals = in.readLine().trim().split("\\s+");
            teams[i] = vals[0];
            teamVertices.put(vals[0], i);
            wins[i] = Integer.parseInt(vals[1]);
            losses[i] = Integer.parseInt(vals[2]);
            left[i] = Integer.parseInt(vals[3]);

            for (int j = 0; j < n; j++)
                games[i][j] = Integer.parseInt(vals[4 + j]);
        }

        // Assign vertex no. to games
        int offset = n;
        for (int i = 0; i < n; i++) {
            for (int j = i+1; j < n; j++) {
                gameVertices[i][j] = offset;
                gameVertices[j][i] = offset++;
            }
        }

        source = offset++;
        target = offset++;
        maxVertex = offset;
    }

    // number of teams
    public int numberOfTeams() {
        return teams.length;
    }

    // all teams
    public Iterable<String> teams() {
        return Arrays.asList(teams);
    }

    // number of wins for given teams
    public int wins(String team) {
        if (!teamVertices.contains(team))
            throw new IllegalArgumentException();

        return wins[teamVertices.get(team)];
    }

    // number of wins for given team
    public int losses(String team) {
        if (!teamVertices.contains(team))
            throw new IllegalArgumentException();

        return losses[teamVertices.get(team)];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        if (!teamVertices.contains(team))
            throw new IllegalArgumentException();

        return left[teamVertices.get(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        if (!teamVertices.contains(team1) || !teamVertices.contains(team2))
            throw new IllegalArgumentException();

        return games[teamVertices.get(team1)][teamVertices.get(team2)];
    }

    // is given teams eliminated?
    public boolean isEliminated(String team) {
        if (team == null || !teamVertices.contains(team))
            throw new IllegalArgumentException();

        int vertex = teamVertices.get(team);
        int maxWins = -1;
        for (int i = 0; i < teams.length; i++)
            maxWins = Math.max(maxWins, wins[i]);

        if (wins[vertex] + left[vertex] < maxWins)
            return true;

        // Create a flow network and find Maxflow
        FlowNetwork network = createFlowNetwork(vertex);
        new FordFulkerson(network, source, target);

        for (FlowEdge e : network.adj(source)) {
            if (e.flow() != e.capacity())
                return true;
        }

        return false;
    }

    // subset R of teams that eliminates given teams; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (team == null || !teamVertices.contains(team))
            throw new IllegalArgumentException();

        int vertex = teamVertices.get(team);
        Queue<String> q = new Queue<>();

        int maxWins = -1;
        int maxTeam = -1;
        for (int i = 0; i < teams.length; i++) {
            if (wins[i] > maxWins) {
                maxWins = wins[i];
                maxTeam = i;
            }
        }

        if (wins[vertex] + left[vertex] < maxWins) {
            q.enqueue(teams[maxTeam]);
            return q;
        }

        // Create a flow network and find Maxflow
        FlowNetwork network = createFlowNetwork(vertex);
        FordFulkerson ff = new FordFulkerson(network, source, target);

        for (int v = 0; v < teams.length; v++)
            if (ff.inCut(v))
                q.enqueue(teams[v]);

        if (q.isEmpty())
            return null;

        return q;
    }

    private FlowNetwork createFlowNetwork(int vertex) {

        FlowNetwork network = new FlowNetwork(maxVertex);

        // Add Edges to Flow Network
        for (int i = 0; i < teams.length; i++) {
            if (i == vertex)    continue;
            for (int j = i+1; j < teams.length; j++) {
                if (j == vertex)    continue;
                // Add Edge between source and games with appropriate capacities
                network.addEdge(new FlowEdge(source, gameVertices[i][j], games[i][j]));
                // Add Edges between games and teams
                network.addEdge(new FlowEdge(gameVertices[i][j], i, Double.POSITIVE_INFINITY));
                network.addEdge(new FlowEdge(gameVertices[i][j], j, Double.POSITIVE_INFINITY));
            }
        }

        // Add Edges from teams to target with appropriate capacities
        for (int i = 0; i < teams.length; i++) {
            if (i == vertex)    continue;
            network.addEdge(new FlowEdge(i, target, wins[vertex] + left[vertex] - wins[i]));
        }

        return network;
    }

}
