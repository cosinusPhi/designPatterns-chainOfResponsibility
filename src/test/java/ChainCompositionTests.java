import de.m9d.edu.patterns.bvr.chainofresponsibilitypattern.Chain;
import de.m9d.edu.patterns.bvr.chainofresponsibilitypattern.ChainFactory;
import de.m9d.edu.patterns.bvr.chainofresponsibilitypattern.LinkFunctional;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Manfred Dreese
 */
public class ChainCompositionTests
{        
    private ChainFactory chainRunnerFactory;
    
    @Before
    public void prepareFactory()
    {
        chainRunnerFactory = new ChainFactory();
    }
    
    public ChainCompositionTests()
    {
    }                           
               
    @Test
    public void chain_composeWithLambdaExpressionsOnly_succeedAndReturnExpectedResult()
    {
        Chain<String> runner = chainRunnerFactory.create();
       
        runner
          .registerLink(new LinkFunctional<>("uppercase", (t, s) -> t.runNext(s.toUpperCase())))
          .registerLink(new LinkFunctional<>("prefix", (t, s) -> t.runNext("prefix-".concat(s))))
          .registerLink(new LinkFunctional<>("makeAwesome", (t, s) -> t.runNext(s.concat(" is awesome"))))
          .registerLink(new LinkFunctional<>("explicit close", (t, s) -> s));
        
        String result = runner.run("enTc");
        System.out.println("result" + result);
        
        Assert.assertEquals("Unexpected result", "prefix-ENTC is awesome", result);
    }
    
    @Test
    public void chain_composeAndSplitToSecondChain_succeedAndReturnExpectedResult()
    {
        Chain<String> logRunner = chainRunnerFactory.create()        
            .registerLink(new LinkFunctional<>("logOnScreen", (t,s) -> 
            {
                System.out.println("LOG> " + s);
                return t.runNext(s);
            }))
            .registerLink(new LinkFunctional<>("fancyDb", (t,s) -> 
            {
                System.out.println("LOGFancyDb> " + s);
                return s;
            }));
        Chain<String> runner = chainRunnerFactory.create();
        
       runner
          .registerLink(new LinkFunctional<>("uppercase", (t, s) -> t.runNext(s.toUpperCase())))
          .registerLink(new LinkFunctional<>("prefix", (t, s) -> t.runNext("prefix-".concat(s))))
          .registerLink(new LinkFunctional<>("makeAwesome", (t, s) -> t.runNext(s.concat(" is awesome"))))
          .registerLink(new LinkFunctional<>("logCompletion", (t, s) -> {
              logRunner.run(" Completed with result : "+s);
              return s;
          }));
                 
        String result = runner.run("enTc");
                
        Assert.assertEquals("Unexpected result", "prefix-ENTC is awesome", result);
    }

    @Test
    public void chain_composeAndSplitToSecondChainFromMap_succeedAndReturnExpectedResult()
    {
        Map<String, LinkFunctional<String>> loggingFacilities = new HashMap<>();
        loggingFacilities.put("logOnScreen",new LinkFunctional<>( (t,s) -> 
            {
                System.out.println("LOG> " + s);
                return t.runNext(s);
            }));
        
        loggingFacilities.put("logToFancyDb",new LinkFunctional<>( (t,s) -> 
            {
                System.out.println("LOGFancyDb> " + s);
                return t.runNext(s);
            }));
        
        loggingFacilities.put("checkOperatorAlert",new LinkFunctional<>( (t,s) -> 
            {
                System.out.println("No operator Pager duty for> " + s);
                return t.runNext(s);
            }));
                
        Chain<String> logRunner = chainRunnerFactory.create()      
            .registerLink(loggingFacilities.get("logOnScreen"))
            .registerLink(loggingFacilities.get("logToFancyDb"))
            .registerLink(loggingFacilities.get("checkOperatorAlert"));
                        
       Chain<String> runner = chainRunnerFactory.create();
       
       runner
          .registerLink(new LinkFunctional<>("uppercase", (t, s) -> t.runNext(s.toUpperCase())))
          .registerLink(new LinkFunctional<>("prefix", (t, s) -> t.runNext("prefix-".concat(s))))
          .registerLink(new LinkFunctional<>("makeAwesome", (t, s) -> t.runNext(s.concat(" is awesome"))))
          .registerLink(new LinkFunctional<>("logCompletion", (t, s) -> {
              logRunner.run(" Completed with result : "+s);
              return s;
          }));
                 
        String result = runner.run("enTc");
                
        Assert.assertEquals("Unexpected result", "prefix-ENTC is awesome", result);
    }
    
    @Test
    public void chain_includeRetryPattern_giveUpOnNextAndReturnExpectedResult()
    {
        Map<String, LinkFunctional<String>> functions = new HashMap<>();
        functions.put("retry3Times",new LinkFunctional<>( (t,s) ->           
            {                
                int attempts = 0;
                while(++attempts <= 3)
                {
                    try {             
                        String result = null;
                        return t.runNext(s);
                    }
                    catch(Exception e)
                    {
                        System.out.println("retry3times attempts="+attempts+" exception="+e);
                    }
                }
                return "SafeResult+" + s;
            }));

       Random rnd = new Random();
       Chain<String> runner = chainRunnerFactory.create();
       
       runner
          .registerLink(new LinkFunctional<>("uppercase", (t, s) -> t.runNext(s.toUpperCase())))
          .registerLink(new LinkFunctional<>("prefix", (t, s) -> t.runNext("prefix-".concat(s))))
          .registerLink(new LinkFunctional<>("makeAwesome", (t, s) -> t.runNext(s.concat(" is awesome"))))
          .registerLink(functions.get("retry3Times"))
          .registerLink(new LinkFunctional<>("failSometimes", (t, s) -> 
          {
              throw new RuntimeException("something went wrong..");
          }))
          .registerLink(new LinkFunctional<>("logCompletion", (t, s) -> {
              System.out.println(" Completed with result : "+s);
              return s;
          }));
                 
        String result = runner.run("enTc");
                
        Assert.assertEquals("Unexpected result", "SafeResult+prefix-ENTC is awesome", result);
    }
    
}
