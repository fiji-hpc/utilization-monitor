package cz.it4i.monitor;

import java.util.List;
import java.util.Map;

import cz.it4i.parallel.MultipleHostParadigm;

public interface IDataGenerator {
	public List<Map<String, Object>> generate(MultipleHostParadigm paradigm);
}