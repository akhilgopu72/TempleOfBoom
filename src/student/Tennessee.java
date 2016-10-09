package student;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import game.Cavern;
import game.Edge;
import game.EscapeState;
import game.ExploreState;
import game.Explorer;
import game.Node;
import game.NodeStatus;
import game.Tile;

public class Tennessee extends Explorer {
    /** Get to the orb in as few steps as possible. Once you get there, 
     * you must return from the function in order to pick
     * it up. If you continue to move after finding the orb rather 
     * than returning, it will not count.
     * If you return from this function while not standing on top of the orb, 
     * it will count as a failure.
     * 
     * There is no limit to how many steps you can take, but you will receive
     * a score bonus multiplier for finding the orb in fewer steps.
     * 
     * At every step, you know only your current tile's ID and the ID of all 
     * open neighbor tiles, as well as the distance to the orb at each of these tiles
     * (ignoring walls and obstacles). 
     * 
     * In order to get information about the current state, use functions
     * currentLocation(), neighbors(), and distanceToOrb() in ExploreState.
     * You know you are standing on the orb when distanceToOrb() is 0.
     * 
     * Use function moveTo(long id) in ExploreState to move to a neighboring 
     * tile by its ID. Doing this will change state to reflect your new position.
     * 
     * A suggested first implementation that will always find the orb, but likely won't
     * receive a large bonus multiplier, is a depth-first search.*/
    @Override public void getOrb(ExploreState state) {
	HashMap <Long, Integer> visited = new HashMap<Long, Integer>();
	Long previousValue = null;
	while (state.distanceToOrb() != 0)
	{
    	Long move = null;
		long columns = 0;
		ArrayList <Long> neighbors = new ArrayList <Long>();
		int distance = state.distanceToOrb();
		for (NodeStatus n : state.neighbors())
		{
			neighbors.add(n.getId());
			if (previousValue != null)
			{
    			if (Math.abs(n.getId() - state.currentLocation()) != 1 )
    			{
    				columns = Math.abs(n.getId() - state.currentLocation());
    			}
			}
			if (n.getDistanceToTarget() < distance 
					&& visited.containsKey(n.getId()) == false)
			{
				distance = n.getDistanceToTarget();
				move = n.getId();
	    			
			}
		}
			
		if (move == null)
		{
	    	if (previousValue != null)
	    	{
	    		/*System.out.println(neighbors);
	    		System.out.println("Previous Value is"  + previousValue);
	    		System.out.println("Current Location is " + state.currentLocation() );
	    		System.out.println("Columns is " + columns);**/
	    		if (neighbors.contains(previousValue + 2))
	    		{
	    			//System.out.println(1);
	    			move = state.currentLocation() + 1;
	    		}
	    		else if (neighbors.contains(previousValue - 2))
	    		{
	    			//System.out.println(2);
	    			move = state.currentLocation() - 1;
	    		}
	    		else if (columns != 0 && neighbors.contains(previousValue + (2* columns)))
	    		{
	    			//System.out.println(3);
	    			move = state.currentLocation() + columns;
	    		}
	    		else if (columns != 0 && neighbors.contains(previousValue - (2* columns)))
	    		{
	    			//System.out.println(4);
	    			move = state.currentLocation() - columns;
	    		}
	    		if (visited.containsKey(move))
	    		{
	    			if (visited.get(move) >= 2)
	    				move = null;
	    		}
	    	}
	    	//System.out.println("Move: " + move);
			int temp2 = Integer.MAX_VALUE;
			if (move == null)
			for (NodeStatus n1 : state.neighbors())
			{
				if (visited.containsKey(n1.getId()) == false)
				{
					move = n1.getId();
					break;
				}
				if (visited.containsKey(n1.getId()))
				{
					if (visited.get(n1.getId()) < temp2)
					{
						temp2 = visited.get(n1.getId());
						move = n1.getId();
					}
				}
			}
		}
		
		
		if (visited.containsKey(move))
		{
			int var = visited.get(move);
			visited.replace(move, var, var + 1);
		}
		else
			visited.put(move, 1);
		previousValue = state.currentLocation();
		//System.out.println(move);
		state.moveTo(move);
		//System.out.println("-------");
		
	}
	return;
	
    
}
    
    /** Get out the cavern before the ceiling collapses, trying to collect as much
     * gold as possible along the way. Your solution must ALWAYS get out before time runs
     * out, and this should be prioritized above collecting gold.
     * 
     * You now have access to the entire underlying graph, which can be accessed through EscapeState.
     * currentNode() and getExit() will return Node objects of interest, and getNodes()
     * will return a collection of all nodes on the graph. 
     * 
     * Note that the cavern will collapse in the number of steps given by stepsRemaining(),
     * and for each step this number is decremented by the weight of the edge taken. You can use
     * stepsRemaining() to get the time still remaining, seizeGold() to pick up any gold
     * on your current tile (this will fail if no such gold exists), and moveTo() to move
     * to a destination node adjacent to your current node.
     * 
     * You must return from this function while standing at the exit. Failing to do so before time
     * runs out or returning from the wrong location will be considered a failed run.
     * 
     * You will always have enough time to escape using the shortest path from the starting
     * position to the exit, although this will not collect much gold. For this reason, using 
     * Dijkstra's to plot the shortest path to the exit is a good starting solution. */
    public void getOut(EscapeState state) {
    	List <Node> answer = Paths.dijkstra(state.currentNode(), state.getExit());
    	List<Node> path;
    	int shortest = 0;
    	for(int i = 0; i < answer.size()-1; i++){
    		shortest = shortest + (answer.get(i).getEdge(answer.get(i+1))).length;
    	}
    	while (shortest  + 2*Cavern.MAX_EDGE_WEIGHT< state.stepsRemaining())
        {
        	MaxHeap<List<Node>> gold = new MaxHeap<List<Node>>();
    		for(Node n : state.getNodes())
    		{
    			path = Paths.dijkstra(state.currentNode(), n);
        		double totalGold = 0;
    			for (Node n1 : path)
    	    		totalGold = n1.getTile().getGold() + totalGold;    		
    			gold.add(path, totalGold);
    		}
    		path = gold.poll();
        	answer = Paths.dijkstra(state.currentNode(), state.getExit());
            for (int n = 1; n < path.size();n++)
            	{
            		if(shortest  + 2*Cavern.MAX_EDGE_WEIGHT< state.stepsRemaining())
            		{
            			if(state.currentNode().getTile().getGold() > 0.0)
            				state.seizeGold();
            			state.moveTo(path.get(n));
            		}
            		shortest = 0;
                	answer = Paths.dijkstra(state.currentNode(), state.getExit());
                	for(int i = 0; i < answer.size() - 1; i++)
                	{
                		shortest = shortest + (answer.get(i).getEdge(answer.get(i+1))).length;
                	}
            	
            	}
        }
    	answer = Paths.dijkstra(state.currentNode(), state.getExit());
        for (int n = 1; n < answer.size();n++)
        {
        	try {
        		state.seizeGold();
        		state.moveTo(answer.get(n));
        	}
        	catch (Exception e)
        	{
        	state.moveTo(answer.get(n));
        	}
        }
        return;
    }
}