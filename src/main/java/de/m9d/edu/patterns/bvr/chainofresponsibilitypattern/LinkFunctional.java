package de.m9d.edu.patterns.bvr.chainofresponsibilitypattern;

import java.util.function.BiFunction;

/**
 * Template for Links which can be inserted in Chain, functional approach
 * 
 * @see  ChainRunner
 * @author Manfred Dreese
 * @param <T> Type of the payload to deal with
 */
public class LinkFunctional<T> extends Link<T>
{
    private final BiFunction<Link<T>, T, T> function;

    public LinkFunctional(BiFunction<Link<T>, T, T> function)
    {        
        this("", function);
    }

    public LinkFunctional(String title, BiFunction<Link<T>, T, T> function)
    {
        this.function = function;
        this.title = title;
    }

    @Override
    protected T process(T payload)
    {
        return function.apply(this, payload);
    }
}
