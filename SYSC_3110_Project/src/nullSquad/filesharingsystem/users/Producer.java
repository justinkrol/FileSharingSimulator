/**
 * Title: SYSC 3110 Project
 * 
 * @author MVezina
 *         Student Number: 100934579
 *         Team: noSquad
 */

package nullSquad.filesharingsystem.users;

import nullSquad.filesharingsystem.*;
import nullSquad.filesharingsystem.document.*;
import nullSquad.simulator.gui.SimulatorGUI;

import java.util.*;

import nullSquad.strategies.act.ProducerActStrategyEnum;
import nullSquad.strategies.payoff.ProducerPayoffStrategy;

/**
 * Producer class that extends User and implements the default
 * ProducerPayoffStrategy
 * This class represents a Producer that belongs to a network
 * 
 * @author MVezina
 */
public class Producer extends User implements ProducerPayoffStrategy, DocumentLikeListener
{
	// The list of documents that the producer has produced
	private List<Document> docsProduced;

	// The Producer Payoff Strategy to be used
	private ProducerPayoffStrategy payoffStrategy;
	private ProducerActStrategyEnum actStrategy;

	public Producer(ProducerPayoffStrategy payoffStrat, ProducerActStrategyEnum actStrat, String userName, String taste)
	{
		this(userName, taste);

		this.payoffStrategy = payoffStrat;
		this.actStrategy = actStrat;
	}

	public Producer(ProducerPayoffStrategy payoffStrat, String userName, String taste)
	{
		this(userName, taste);

		this.payoffStrategy = payoffStrat;
	}

	public Producer(ProducerActStrategyEnum actStrat, String userName, String taste)
	{
		this(userName, taste);

		this.actStrategy = actStrat;
	}

	/**
	 * Default Constructor.
	 * Creates a producer with the specified userID and taste
	 * 
	 * @param userName the userID of the new user
	 * @param taste the taste of the user
	 */
	public Producer(String userName, String taste)
	{
		super(userName, taste);
		this.docsProduced = new ArrayList<Document>();

		// Sets the default payoff strategy to the interface implementation in
		// this class
		this.payoffStrategy = this;
		this.actStrategy = ProducerActStrategyEnum.Default;
	}

	/**
	 * The Producer Override of addFollower(..):
	 * - Requires re-calculation of Payoff after adding a follower
	 */
	@Override
	public boolean addFollower(User user)
	{
		boolean res = super.addFollower(user);

		if (res)
			SimulatorGUI.appendLog(this.getUserName() + " has been followed by " + user.getUserName() + ". Updated Producer Payoff: " + calculatePayoff());

		return res;
	};

	/**
	 * The Producer Acts By:
	 * - Produces a new document
	 * - Runs the specified producer act strategy
	 * - Updates the payoff
	 */
	@Override
	public void act(FileSharingSystem net, int kResults)
	{
		// Ensure the user has been registered
		if (userID <= 0)
		{
			System.out.println("The User is not currently registered.");
			return;
		}

		// Create a new document and upload to the network
		produceDocument(net);

		// Call the producer act strategy
		this.actStrategy.getStrategy().act(this, net, kResults);
		
	}

	/**
	 * Produces and uploads a new document to the network
	 * 
	 * @param net The network to upload the document to
	 * @author MVezina
	 */
	private void produceDocument(FileSharingSystem net)
	{
		// Create a new document
		Document newDoc = new Document("Document " + this.taste + " (" + (new Random()).nextInt(500) + ")", this.taste, this);

		// Add new document to document produced
		docsProduced.add(newDoc);

		// The document is now added to the network
		net.addDocument(newDoc);
		
		// Like your own document
		this.likeDocument(newDoc);

	}

	/**
	 * Calls the set Producer Payoff Strategy
	 * 
	 * @return The payoff of the producer using the selected strategy
	 * @author MVezina
	 */
	public int calculatePayoff()
	{
		return payoffStrategy.producerPayoffStrategy(this);
	}

	/**
	 * The Default Producer payoff method uses followers/likes to calculate
	 * payoff.
	 * One point is given for every person who likes the producer's documents
	 * Two points are given for every follower the producer has
	 * 
	 * @author MVezina
	 */
	@Override
	public int producerPayoffStrategy(Producer prod)
	{
		// Two points for every follower the producer has
		int totalPayoff = prod.getFollowers().size() * 2;

		// One point for every like the producer has
		for (Document d : prod.getDocumentsProduced())
		{
			totalPayoff += d.getUserLikes().size();
		}

		return totalPayoff;
	}

	/**
	 * @return The List of Documents Produced by the producer
	 * @author MVezina
	 */
	public List<Document> getDocumentsProduced()
	{
		return this.docsProduced;
	}

	/**
	 * Sets the act strategy
	 * @param producerActStrategy
	 */
	public void setActStrategyEnum(ProducerActStrategyEnum producerActStrategy)
	{
		if (producerActStrategy == null)
			return;
		this.actStrategy = producerActStrategy;
	}

	/**
	 * @return Producer act strategy enum
	 */
	public ProducerActStrategyEnum getActStrategyEnum()
	{
		return this.actStrategy;
	}

	@Override
	public String toString()
	{
		return "User Type: Producer (Payoff: " + calculatePayoff() + ")\n" + super.toString() + "\nDocuments Produced: " + docsProduced.size() + "\n";
	}

	@Override
	public boolean equals(Object o)
	{
		// Call the user equals method
		if (!super.equals(o))
			return false;

		// Check if the object is an instance of Producer
		if (!(o instanceof Producer))
			return false;

		// Cast to producer
		Producer p = (Producer) o;

		// Perform comparisons
		return (this.docsProduced.size() == p.docsProduced.size());

	}

	/**
	 * DocumentLiked Event Handler
	 */
	@Override
	public void DocumentLiked(DocumentLikeEvent docLikeEvent)
	{
		if (docLikeEvent.getDocument().getProducer().equals(this))
		{
			// Calculate and print the payoff
			SimulatorGUI.appendLog(docLikeEvent.getLikingUser().getUserName() + " has liked '" + docLikeEvent.getDocument().getDocumentName() + "'. " + docLikeEvent.getDocument().getProducer().getUserName() + " Payoff: "  + calculatePayoff());
		}

	}

	@Override
	public void addIterationPayoff(int currentIteration)
	{
		if (currentIteration == getPayoffHistory().size())
		{
			payoffHistory.add(calculatePayoff());
		}
	}

}
