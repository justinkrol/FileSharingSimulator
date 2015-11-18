/**
 * Interface for Producer Payoff Strategies
 * 
 * @author MVezina
 */
package nullSquad.strategies;

import nullSquad.filesharingsystem.users.*;

/**
 * This interface will be used to implement all Producer Payoff Strategies
 * @author MVezina
 */
public interface ProducerPayoffStrategy
{
	public int producerPayoffStrategy(Producer prod);
	
}
