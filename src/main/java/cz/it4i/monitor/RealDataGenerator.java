package cz.it4i.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scijava.parallel.Status;

import cz.it4i.parallel.MultipleHostParadigm;

public class RealDataGenerator implements DataGenerator{

	@Override
	public List<Map<String, Object>> generate(MultipleHostParadigm paradigm) {
		if(paradigm.getStatus() == Status.ACTIVE) {
			Map<String, Object> parameters = new HashMap<>();
			return paradigm.runOnHosts(UtilizationDataCollector.class.getName(), parameters, paradigm.getHosts());
		}
		return new ArrayList<>();
	}
}
