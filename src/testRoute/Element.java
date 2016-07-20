package testRoute;

public class Element implements Comparable<Element>{

	private int index;
	private int distance;
	
	public Element(){	}
	
	
	public Element(int index, int distance) {
		this.index = index;
		this.distance = distance;
	}
	
	public int getIndex(){
		return index;
	}
	
	public int getDistance(){
		return distance;
	}

	@Override
	public int compareTo(Element o) {
		return distance <= o.distance ? -1 : 1;
	}

}
