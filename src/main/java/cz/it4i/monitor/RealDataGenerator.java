package cz.it4i.monitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.it4i.parallel.MultipleHostParadigm;

public class RealDataGenerator implements IDataGenerator{

	@Override
	public List<Map<String, Object>> generate(MultipleHostParadigm paradigm) {
		Map<String, Object> parameters = new HashMap<>();
		return paradigm.runOnHosts(UtilizationDataCollector.class.getName(), parameters, paradigm.getHosts());
	}
}
