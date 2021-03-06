/* 
 * Copyright (C) 2015 Vasilis Efthymiou <vefthym@ics.forth.gr>
 */
package preprocessing;


import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.VIntWritable;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import preprocessing.VIntArrayWritable;


public class AfterFilteringBlockSizeCounterMapper extends MapReduceBase implements Mapper<VIntWritable, VIntArrayWritable, VIntWritable, VIntWritable> {
	
	static enum InputData {NOT_AN_ENTITY, NULL_PREFIX_ID, MALFORMED_PAIRS};
		
	Text outputValue = new Text();
	VIntWritable one = new VIntWritable(1);
	/**
	 * maps an input entity index into (blockId, 1) pair(s)
	 * the value is the entity id (input key) along with the num of blocks that contain it
	 * the key each time is a block id (each element of the input value array)
	 * @param key an entity id
	 * @param value an array of block ids that this entity belongs to
	 * @param output key: a block id (each element of the input value array) - value: 1, for each entity in this block
	 */
	public void map(VIntWritable key, VIntArrayWritable value,
			OutputCollector<VIntWritable, VIntWritable> output, Reporter reporter) throws IOException {
		 
		VIntWritable [] Bi = value.get(); 	
		for (VIntWritable bi : Bi) {			
			output.collect(bi, one);			
		}
		
	}

}
