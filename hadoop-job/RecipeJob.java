/**
 * Created by jethva on 1/26/16.
 */

import java.io.IOException;
import java.util.*;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.bson.BSON;
import org.bson.BSONObject;
import org.bson.Document;
import org.jsoup.Jsoup;
import org.w3c.dom.css.DocumentCSS;
import scraper.RecipeInformationScraper;
import scraper.RecipeSubscriber;
import scraper.Subscriber;

public class RecipeJob {

    private static final Subscriber<Document> recipeSubscriber = new RecipeSubscriber();
    private static final RecipeInformationScraper scraper = new RecipeInformationScraper(recipeSubscriber);

    public static class Map extends Mapper<LongWritable, Text, Text, Text> {

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String url = value.toString();
            String lastChar = url.charAt(url.length()-1) +"";
            context.write(new Text(lastChar),new Text(url));
        }
    }

    public static class Reduce extends Reducer<Text, Text, Text, Text> {

        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {

            for (Text val : values) {
                String currentUrl = val.toString();
                if(currentUrl.length() > 1) {

                    scraper.scrapeRecipeInformation(currentUrl);

                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();

        Job job = new Job(conf, "scraper");

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.waitForCompletion(true);
    }
}
