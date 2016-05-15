package de.m9d.edu.patterns.bvr.chainofresponsibilitypattern;

import java.util.Objects;

/**
 * Template for Links which can be inserted in Chain
 * 
 * @see  ChainRunner
 * @author Manfred Dreese
 * @param <T> Type of the payload to deal with
 */
public abstract class Link<T>
{        
    /**
     * Human readable Title of this Link
     */
    protected String title;    
    
    /**
     * Instance of NextLinkResolver to allow Links to resolve the next Link
     * in the chain.
     */
    private INextLinkResolver<T> nextItemResolver;
            
    public Link()
    {
        title = "";
    }
    
    protected abstract T process(T payload);
       
    /**
     * Execute the next element in the chain.
     * If there is no next Link, the result of the current Link is returned.
     * @param in
     * @return 
     */    
    public T runNext(T in)
    {
        Objects.requireNonNull(nextItemResolver, 
                               "This Link does not possess a nextItemResolver yet!");
        
        return nextItemResolver.resolve(this)
          .map(next -> next.process(in))
          .orElse(in);              
    }
                 
    
    protected void setNextItemResolver(INextLinkResolver<T> nextItemResolver)
    {
        this.nextItemResolver = nextItemResolver;
    }
            
}
