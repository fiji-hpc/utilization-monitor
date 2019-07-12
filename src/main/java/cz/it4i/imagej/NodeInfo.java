package cz.it4i.imagej;

public class NodeInfo {
	
	public int nodeId;
	
	public double cpuUtilization;
	
	public double memoryUtilization;

	NodeInfo(int nodeId, double cpuUtilization, double memoryUtilization){
		this.nodeId = nodeId;
		this.cpuUtilization = cpuUtilization;
		this.memoryUtilization = memoryUtilization;
	}
	
	public int getNodeId(){
		return this.nodeId;
	}
	
	public double getCpuUtilization(){
		return this.cpuUtilization;
	}
	
	public double getMemoryUtilization(){
		return this.memoryUtilization;
	}
	
	public void setNodeId(int nodeId){
		this.nodeId = nodeId;
	}
	
	public void setCpuUtilization(double cpuUtilization){
		this.cpuUtilization = cpuUtilization;
	}
	
	public void setMemoryUtilization(double memoryUtilization){
		this.memoryUtilization = memoryUtilization;
	}
}
