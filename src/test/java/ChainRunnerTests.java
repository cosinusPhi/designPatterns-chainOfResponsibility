
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.m9d.edu.patterns.bvr.chainofresponsibilitypattern.Chain;
import de.m9d.edu.patterns.bvr.chainofresponsibilitypattern.ChainFactory;
import de.m9d.edu.patterns.bvr.chainofresponsibilitypattern.ChainRunner;
import de.m9d.edu.patterns.bvr.chainofresponsibilitypattern.LinkFunctional;
import de.m9d.edu.patterns.bvr.chainofresponsibilitypattern.Solution;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Manfred Dreese
 */
public class ChainRunnerTests
{
    private static Logger log = Logger.getLogger(ChainRunnerTests.class.getName());
    
    private ChainFactory chainFactory;
    private Chain<String> chain;
    
    Gson gson;
    
    public ChainRunnerTests()
    {
    }
    
    @Before
    public void setUp()
    {
        gson = new GsonBuilder().setPrettyPrinting().create();        
        chainFactory = new ChainFactory();
        chain = chainFactory.create();
        
        chain
          .registerLink(new LinkFunctional<>("uppercase", (t, s) -> t.runNext(s.toUpperCase())))
          .registerLink(new LinkFunctional<>("prefix", (t, s) -> t.runNext("bääm-".concat(s))))
          .registerLink(new LinkFunctional<>("makeAwesome", (t, s) -> t.runNext(s.concat(" is awesome"))))
          .registerLink(new LinkFunctional<>("explicit close", (t, s) -> s));
    }
    
    private static List<String> getWork(int cardinality)
    {
        Random rnd = new Random();
        
        List<String> result = IntStream.rangeClosed(1, cardinality)
          .mapToObj(n -> String.format("Item_%d_%d", n, rnd.nextLong()))
          .collect(Collectors.toList());
        
        return result;
    }
    
    @Test
    public void chainRunner_runSingle_succeed()    
    {
        String input = getWork(1).get(0);
        ChainRunner<String> chainRunner = new ChainRunner<>(chain);        
        Solution<String> result = chainRunner.process(input);        
    }
    
    @Test
    public void chainRunner_runMultipleWithSingleElement_succeed()    
    {
        List<String> somedata = getWork(1);
        ChainRunner<String> chainRunner = new ChainRunner<>(chain);        
        List<Solution<String>> result = chainRunner.processMultiple(somedata);        
    }
    
    @Test
    public void chainRunner_runMultiple_succeed()    
    {        
        List<String> somedata = getWork(10000);
        ChainRunner<String> chainRunner = new ChainRunner<>(chain);        
        List<Solution<String>> result = chainRunner.processMultiple(somedata);

        // Check for shift or accidents
        for (int idx = 0; idx < 1000; idx++)
        {            
            Assert.assertTrue(result.get(idx).getInput().contains(String.format("Item_%d_", idx + 1)));            
            Assert.assertTrue(result.get(idx).getOutput().contains(String.format("ITEM_%d_", idx + 1)));            
        }
    }
    
    @Test
    public void chainRunner_runMultipleParallel_succeed()    
    {        
        List<String> somedata = getWork(10000);
        ChainRunner<String> chainRunner = new ChainRunner<>(chain);        
        List<Solution<String>> result = chainRunner.processMultipleParallel(somedata);

        // Check for shift or accidents
        for (int idx = 0; idx < 1000; idx++)
        {            
            Assert.assertTrue(result.get(idx).getInput().contains(String.format("Item_%d_", idx + 1)));            
            Assert.assertTrue(result.get(idx).getOutput().contains(String.format("ITEM_%d_", idx + 1)));            
        }        
    }
    
    @Test
    public void chainRunner_runMultipleParallelAsync_succeed() throws InterruptedException, TimeoutException, ExecutionException    
    {        
        List<String> somedata = getWork(100000);
        ChainRunner<String> chainRunner = new ChainRunner<>(chain);        
        
        log.log(Level.INFO, "Starting chain");
        
        List<Solution<String>> results = chainRunner.processMultipleParallelAsync(somedata)
          .exceptionally((t) -> 
           {
                System.out.println("Something went wrong.");
                return null;                
          })
          .whenComplete((result, throwable)  -> 
           {
                log.log(Level.INFO, "Got results.");
                log.log(Level.INFO, gson.toJson(result.get(0)));
                log.log(Level.INFO, gson.toJson(result.get(99999)));
          })
          .whenComplete( (result, throwable) -> 
          { 
            for (int idx = 0; idx < 1000; idx++)
            {                
                Assert.assertTrue(result.get(idx).getInput().contains(String.format("Item_%d_", idx + 1)));                
                Assert.assertTrue(result.get(idx).getOutput().contains(String.format("ITEM_%d_", idx + 1)));                
            }
          })
          .get(10, TimeUnit.SECONDS);
                      
        log.log(Level.INFO, "Doing some work in main thread..");        
        Assert.assertEquals(100000, results.size());        
        log.log(Level.INFO, "Completed test");
    }
}
