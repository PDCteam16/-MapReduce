                                             //importing the necessary java classes and hadoop packages
import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
 
public class WordCount {                                            // main class is WordCount
 
  public static class TokenizerMapper                                                
       extends Mapper<Object, Text, Text, IntWritable>{            //Designing and implementing a Mapper class-an essential for MapReduce
 
    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();
 
    public void map(Object key, Text value, Context context          //Defining a map class with error checking 
                    ) throws IOException, InterruptedException {
      StringTokenizer itr = new StringTokenizer(value.toString());
      while (itr.hasMoreTokens()) {
        word.set(itr.nextToken());
        context.write(word, one);
      }
    }
  }
 
  public static class IntSumReducer                                   //Designing and implementing a Reducer class-an essential for MapReduce
       extends Reducer<Text,IntWritable,Text,IntWritable> {
    private IntWritable result = new IntWritable();
 
    public void reduce(Text key, Iterable<IntWritable> values,         //Defining a map class with error checking 
                       Context context
                       ) throws IOException, InterruptedException {
      int sum = 0;
      for (IntWritable val : values) {
        sum += val.get();
      }
      result.set(sum);                                                   // Main logic of WordCount
      context.write(key, result);
    }
  }
 
  public static void main(String[] args) throws Exception {             /*Configuration object is called exclusively in Hadoop which has 
    Configuration conf = new Configuration();                            parameters that configure the underlying system */  
    Job job = Job.getInstance(conf, "word count");
    job.setJarByClass(WordCount.class);                                 //creating Mapper,Reducer and Driver class files
    job.setMapperClass(TokenizerMapper.class);
    job.setCombinerClass(IntSumReducer.class);
    job.setReducerClass(IntSumReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);                       
    FileInputFormat.addInputPath(job, new Path(args[0]));               //specifying the path of output file in Hadoop Directory File System
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
