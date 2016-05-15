package de.m9d.edu.patterns.bvr.chainofresponsibilitypattern;

import java.util.List;

/**
 * Executor for Chains
 * @author Manfred Dreese
 * @param <T>
 */
public class Chain<T>
{
    private final List<Link<T>> chain;
    
    /**
     * Resolver which will be injected into all links in order
     * to get the next Link in the chain.
     * 
     * Should default to TrivialNextResolver
     * 
     * @see TrivialNextResolver
     */    
    private final INextLinkResolver<T> nextItemResolver;
       
    public Chain(List<Link<T>> chain, INextLinkResolver<T> nextItemResolver)
    {
        this.chain = chain;
        this.nextItemResolver = nextItemResolver;
    }
    
    /**
     * add another Link add the end of the managed chain
     * @param link
     * @return
     */
    public Chain<T> registerLink(Link<T> link)
    {           
        // Inject nextItemResolver
        link.setNextItemResolver(nextItemResolver);
        
        // Add to chain
        chain.add(link);
        return this;
    }
    
    /**
     * Run the managed chain
     * @param input
     * @return result
     */
    public T run(T input)
    {
        return chain.get(0).process(input);
    }
    
}