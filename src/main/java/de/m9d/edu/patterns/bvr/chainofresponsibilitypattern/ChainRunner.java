package de.m9d.edu.patterns.bvr.chainofresponsibilitypattern;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Runs a previously defined chain
 * @author Manfred Dreese
 * @param <T>
 */
public class ChainRunner<T>
{
    private final Chain<T> chain;
    
    public ChainRunner(Chain<T> chain)
    {
        this.chain = chain;
    }

    public Solution<T> process(T item)
    {
        return new Solution<>(item, chain.run(item));
    }
    
    public List<Solution<T>> processMultiple(List<T> items)
    {
        return items.stream()          
          .map((T item) -> new Solution<>(item , chain.run(item)) )
          .collect(Collectors.toList());                        
    }
    
    public List<Solution<T>> processMultipleParallel(List<T> items)
    {
        return items.parallelStream()
          .map((T item) -> new Solution<>(item , chain.run(item)) )
          .collect(Collectors.toList());                        
    }
            
    public CompletableFuture <List<Solution<T>>> processMultipleParallelAsync(List<T> items)
    {
        return CompletableFuture.supplyAsync(() -> processMultipleParallel(items));
    }
}
