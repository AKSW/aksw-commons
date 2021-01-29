package org.aksw.commons.collector.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.aksw.commons.collector.core.AggInputBroadcastMap.AccInputBroadcastMap;
import org.aksw.commons.collector.domain.ParallelAggregator;
import org.junit.Assert;
import org.junit.Test;

public class AggBuilderTests {
	/**
	 * Test which create an inputBroadcastMap aggregator with one sub-aggregator for the maximum string length
	 * Two accumulators are then created of which each receives input.
	 * The two accumulators are then combined into a new on and it is checked whether the
	 * resulting string length is the correct one.
	 */
	@Test
	public void aggInputBroadcastMapTest() {

		ParallelAggregator<String, Integer, ?> subAgg = AggBuilder.inputTransform(String::length, AggBuilder.maxInteger());
		
		Map<String, ParallelAggregator<String, Integer, ?>> aggMap = new HashMap<>();
		aggMap.put("strLen", subAgg);
		
		AggInputBroadcastMap<String, String, Integer> agg = AggBuilder.inputBroadcastMap(aggMap);
		
		AccInputBroadcastMap<String, String, Integer> acc1 = agg.createAccumulator();
		AccInputBroadcastMap<String, String, Integer> acc2 = agg.createAccumulator();
		
		acc1.accumulate("test");
		acc2.accumulate("testtest");
	
		AccInputBroadcastMap<String, String, Integer> acc = agg.combine(acc1, acc2);
		
		Map<String, Integer> actual = acc.getValue();
		
		Map<String, Integer> expected = Collections.singletonMap("strLen", 8); // 8 = "testtest".length()
		Assert.assertEquals(expected, actual);
	}
}
