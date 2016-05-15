package de.m9d.edu.patterns.bvr.chainofresponsibilitypattern;

import java.util.Optional;

/**
 * Contract for Next Chain Link resolver Implementations
 * @author Manfred Dreese
 */
public interface INextLinkResolver<T>
{
    /**
     * Returns the next Link in the chain.
     * 
     * @param current Link
     * @return Next link or null if not applicable
     */
    public Optional<Link<T>> resolve(Link<T> current);
}
