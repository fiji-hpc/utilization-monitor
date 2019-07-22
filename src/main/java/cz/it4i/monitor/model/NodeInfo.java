package cz.it4i.monitor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NodeInfo {
	
	private int nodeId;
	
	private static final int MAX_POINTS_IN_HISTORY = 30;
	
	private List<Map<String, Object>> history;
	
	public NodeInfo(int nodeId){
		this.nodeId = nodeId;
		this.history = new ArrayList<Map<String, Object>>();
	}
	
	public int getNodeId(){
		return this.nodeId;
	}
	
	public double getCpuUtilization(){
		try {
			return this.getDataFromHistory(this.history.size()-1, "systemCpuLoad");
		} catch( Exception e) {
			return 0.0;
		}
	}
	
	public double getMemoryUtilization(){
		try {
			return this.getDataFromHistory(this.history.size()-1, "memoryUtilization");
		} catch( Exception e) {
			return 0.0;
		}
	}
	
	public double getSystemLoadAverage() {
		try {
			return this.getDataFromHistory(this.history.size()-1, "systemLoadAverage");
		} catch( Exception e) {
			return 0.0;
		}
	}

	public void addToHistory(Map<String, Object> dataPoint) {
		history.add(dataPoint);
		if(history.size() > MAX_POINTS_IN_HISTORY) {
			history.remove(0);
		}
				
	}

	public Double getDataFromHistory(int timePoint, String type) {
		Double dataPoint = Double.valueOf((String) history.get(timePoint).get(type));
		return dataPoint;
	}

	public int getNumberOfItemsInHistory() {
		return history.size();
	}
	
	public String toString() {
		return "Node id: "+nodeId+" history: "+history.toString();		
	}
}
