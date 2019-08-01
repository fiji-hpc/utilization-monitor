package cz.it4i.monitor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NodeInfo {

	private int nodeId;

	private static final int MAX_POINTS_IN_HISTORY = 30;

	private List<Map<String, Object>> history;

	public NodeInfo(int nodeId) {
		this.nodeId = nodeId;
		this.history = new ArrayList<>();
	}

	public int getNodeId() {
		return this.nodeId;
	}

	public double getCpuUtilization() {
		try {
			return (double) this.getDataFromHistory(this.history.size() - 1, "systemCpuLoad");
		} catch (Exception e) {
			return 0.0;
		}
	}

	public double getMemoryUtilization() {
		try {
			return (double) this.getDataFromHistory(this.history.size() - 1, "memoryUtilization");
		} catch (Exception e) {
			return 0.0;
		}
	}

	public double getSystemLoadAverage() {
		try {
			return (double)this.getDataFromHistory(this.history.size() - 1, "systemLoadAverage");
		} catch (Exception e) {
			return 0.0;
		}
	}

	public void addToHistory(Map<String, Object> dataPoint) {
		history.add(dataPoint);
		if (history.size() > MAX_POINTS_IN_HISTORY) {
			history.remove(0);
		}

	}

	public Object getDataFromHistory(int timePoint, String type) {
		return history.get(timePoint).get(type);
	}

	public int getNumberOfItemsInHistory() {
		return history.size();
	}

	@Override
	public String toString() {
		return "Node id: " + nodeId + " history: " + history.toString();
	}
}
