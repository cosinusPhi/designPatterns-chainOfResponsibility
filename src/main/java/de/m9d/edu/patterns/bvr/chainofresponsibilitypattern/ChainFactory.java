package de.m9d.edu.patterns.bvr.chainofresponsibilitypattern;

import java.util.ArrayList;

/**
 * Builds a new Instance of Chain
 * 
 * @see Chain
 * @author Manfred Dreese
 */
public class ChainFactory
{
    public <T> Chain create()
    {
        ArrayList<Link<T>> list = new ArrayList<>();
        INextLinkResolver<T> nextLinkResolver = new TrivialNextResolver<>(list);
        Chain<T> result = new Chain<>(list,nextLinkResolver);
        return result;
    }
}
