package de.m9d.edu.patterns.bvr.chainofresponsibilitypattern;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Simple resolver which just returns the next chain in the list
 * @author Manfred Dreese
 * @param <T> Payload type
 */
public class TrivialNextResolver<T> implements INextLinkResolver<T>
{
    private final List<Link<T>> links;
    
    public TrivialNextResolver(List<Link<T>> links)
    {
        this.links = links;
    }
    
    @Override
    public Optional<Link<T>> resolve(Link<T> current)
    {        
        Optional<Link<T>> result = Optional.empty();
                  
        if (Objects.nonNull(links) && Objects.nonNull(current))
        {
            int index = links.indexOf(current);
            if(index >=0 && index < links.size() -1 )
            {
                result = Optional.of(links.get(index+1));
            }
        }
        
        return result;
    }
    
}
