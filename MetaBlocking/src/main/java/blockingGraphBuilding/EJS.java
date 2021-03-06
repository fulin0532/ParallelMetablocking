/* 
 * Copyright (C) 2015 Vasilis Efthymiou <vefthym@ics.forth.gr>
 */
package blockingGraphBuilding;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobID;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.RunningJob;

public class EJS extends MapReduceBase implements Reducer<Text, Text, Text, DoubleWritable> {

	DoubleWritable toEmit = new DoubleWritable();
	DecimalFormat df;
	
	long Eb = 0;
	public void configure(JobConf conf) {
		try {
		JobClient client = new JobClient(conf);
        RunningJob parentJob = client.getJob(JobID.forName(conf.get("mapred.job.id")));        
		long mapperCounter = parentJob.getCounters().findCounter("org.apache.hadoop.mapred.Task$Counter",
					"MAP_OUTPUT_RECORDS").getCounter();
		Eb = mapperCounter / 2; //mapperCounter is always an even number
		} catch (IOException e) {			
			e.printStackTrace();
		}
		df = new DecimalFormat("#.###"); //format doubles to keep only first 3 decimal points (saves space)
	}
	
	/**
	 * @param key i,j entity ids
	 * @param value JS.vi and JS.vj (two values always)
	 * @param output key: i,j value: wij (EJSReducer) 
	 */
	public void reduce(Text _key, Iterator<Text> values,
			OutputCollector<Text, DoubleWritable> output, Reporter reporter) throws IOException {		
						
		String[] value1 = values.next().toString().split(",");
		String[] value2 = values.next().toString().split(",");
		double JS = Double.parseDouble(value1[0]);
		double vi = Double.parseDouble(value1[1]); //to save further casting (int->double)
		double vj = Double.parseDouble(value2[1]); //to save further casting (int->double)
		double outputValue = JS * Math.log10(Eb / vi) * Math.log10(Eb / vj);
		reporter.progress();
		toEmit.set(Double.parseDouble(df.format(outputValue)));
//		toEmit.set(outputValue);
		output.collect(_key, toEmit);
	}	
	
}
