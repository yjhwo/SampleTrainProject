package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class testDijkstra {

	public static void main(String[] args) {
		Graph g = new Graph();
		g.addVertex("서울", Arrays.asList(new Vertex("용산", 6), new Vertex("부산", 350), new Vertex("부전", 337)));
		g.addVertex("용산", Arrays.asList(new Vertex("서울", 6), new Vertex("전주", 199), new Vertex("여수EXPO", 301)));
		g.addVertex("부산", Arrays.asList(new Vertex("서울", 350)));
		g.addVertex("부전", Arrays.asList(new Vertex("서울", 337)));
		g.addVertex("전주", Arrays.asList(new Vertex("용산", 199), new Vertex("여수EXPO", 100)));
		g.addVertex("여수EXPO", Arrays.asList(new Vertex("용산", 301), new Vertex("전주", 100)));
		
		System.out.println(g.getShortestPath("전주", "서울"));
	}

}

class Vertex implements Comparable<Vertex> {

	private String id;
	private Integer distance;

	public Vertex(String id, Integer distance) {
		super();
		this.id = id;
		this.distance = distance;
	}

	public String getId() {
		return id;
	}

	public Integer getDistance() {
		return distance;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((distance == null) ? 0 : distance.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		Vertex other = (Vertex) obj;
		
		if (distance == null) {
			if (other.distance != null)
				return false;
		} else if (!distance.equals(other.distance))
			return false;
		
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		
		return true;
	}

	@Override
	public String toString() {
		return "Vertex [id=" + id + ", distance=" + distance + "]";
	}

	@Override
	public int compareTo(Vertex o) {
		if (this.distance < o.distance)
			return -1;
		else if (this.distance > o.distance)
			return 1;
		else
			return this.getId().compareTo(o.getId());
	}

}

class Graph {

	private final Map<String, List<Vertex>> vertices;

	public Graph() {
		this.vertices = new HashMap<String, List<Vertex>>();
	}

	public void addVertex(String string, List<Vertex> vertex) {
		this.vertices.put(string, vertex);
	}

	public List<String> getShortestPath(String start, String finish) {
		final Map<String, Integer> distances = new HashMap<String, Integer>();
		final Map<String, Vertex> previous = new HashMap<String, Vertex>();		// 연결된 이전 노드 보려고
		PriorityQueue<Vertex> nodes = new PriorityQueue<Vertex>();

		for (String vertex : vertices.keySet()) {			
			if (vertex == start) {
				distances.put(vertex, 0);
				nodes.add(new Vertex(vertex, 0));
			} else {
				distances.put(vertex, Integer.MAX_VALUE);
				nodes.add(new Vertex(vertex, Integer.MAX_VALUE));
			}
			previous.put(vertex, null);			
		}
		System.out.print("nodes:");
		for (Vertex v : nodes) {
			System.out.print(v);
		}
		System.out.println();
		while (!nodes.isEmpty()) {
			Vertex smallest = nodes.poll();
			System.out.println("smallest:"+smallest);
			
			if (smallest.getId() == finish) {
				final List<String> path = new ArrayList<String>();
				
				while (previous.get(smallest.getId()) != null) {
					path.add(smallest.getId());
					smallest = previous.get(smallest.getId());
				}
				
				return path;
			}

			System.out.println(distances.get(smallest.getId()));
			if (distances.get(smallest.getId()) == Integer.MAX_VALUE) {
				break;
			}

			for (Vertex neighbor : vertices.get(smallest.getId())) {
				Integer alt = distances.get(smallest.getId()) + neighbor.getDistance();
				if (alt < distances.get(neighbor.getId())) {
					distances.put(neighbor.getId(), alt);
					previous.put(neighbor.getId(), smallest);

					System.out.println(neighbor.getId()+":"+distances.get(neighbor.getId()));
										
					forloop: for (Vertex n : nodes) {
						if (n.getId() == neighbor.getId()) {
							nodes.remove(n);
							n.setDistance(alt);
							nodes.add(n);
							
							System.out.println(":"+n);
							break forloop;
						}
					}
				}
			}
		}

		return new ArrayList<String>(distances.keySet());
	}

}