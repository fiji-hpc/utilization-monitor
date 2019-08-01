package cz.it4i.monitor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cz.it4i.parallel.MultipleHostParadigm;

public class FakeDataGenerator implements IDataGenerator{

	private long fakeTime = 0;
	
	private Random rand = new Random();
	
	@Override
	public List<Map<String, Object>> generate(MultipleHostParadigm paradigm) {
		fakeTime += 1000;
		int fakeNumberOfNodes = 20;
		List<Map<String, Object>> allData = new LinkedList<>();
		for (int index = 0; index < fakeNumberOfNodes; index++) {
			Map<String, Object> aNodeData = new HashMap<>();
			aNodeData.put("uptime", fakeTime);
			double fakeMemoryUtilization = rand.nextDouble();
			aNodeData.put("memoryUtilization", fakeMemoryUtilization);
			aNodeData.put("systemCpuLoad", rand.nextDouble());
			aNodeData.put("availableProcessors", 128);
			aNodeData.put("totalPhysicalMemorySize", 64 * (long) Math.pow(10, 9));
			aNodeData.put("committedVirtualMemorySize", 729411584L);
			aNodeData.put("totalSwapSpaceSize", 12489793536L);
			aNodeData.put("freeSwapSpaceSize", 5837094912L);
			aNodeData.put("processCpuLoad", rand.nextDouble());
			aNodeData.put("processCpuTime", fakeTime * (long) Math.pow(10, 6));
			aNodeData.put("systemLoadAverage", rand.nextDouble() * (fakeNumberOfNodes + 1));
			aNodeData.put("name", "Fake-Linux");
			aNodeData.put("arch", "amd64");
			aNodeData.put("version", "3.10.0-957.12.2.el7.x86_64");
			aNodeData.put("vmVendor", "Fake Corporation");
			aNodeData.put("vmName", "Fake Virtual Machine 64bit");
			aNodeData.put("vmVersion", "fake-1.0");
			aNodeData.put("freePhysicalMemorySize", 16L * (long) Math.pow(10, 9));
			aNodeData.put("classPath", "one;two;three;");
			allData.add(aNodeData);
		}
		return allData;
	}
	
}
