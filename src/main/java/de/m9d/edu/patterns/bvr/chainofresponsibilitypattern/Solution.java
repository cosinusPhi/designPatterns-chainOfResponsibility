package de.m9d.edu.patterns.bvr.chainofresponsibilitypattern;

import lombok.Getter;

/**
 * Input-Output Structure used by ChainRunner
 * @author Manfred Dreese
 * @param <T>
 */
public class Solution<T>
{
    @Getter
    private final T input;
    @Getter
    private final T output;

    public Solution(T input, T output)
    {
        this.input = input;
        this.output = output;
    }               
}
