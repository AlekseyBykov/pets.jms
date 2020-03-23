package alekseybykov.portfolio.jms.api2x;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import static org.junit.Assert.assertEquals;

public class P2PMessagingTest {

	private static final String queueName = "queue/jmsQueue";
	private static Queue queue;

	@BeforeClass
	public static void setup() {
		try {
			InitialContext ctx = new InitialContext();
			queue = (Queue) ctx.lookup(queueName);
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSendTextMessageToQueueAndThenReceive() {
		try (ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
		    JMSContext jmsContext = activeMQConnectionFactory.createContext()) {
			String message = "message";

			JMSProducer jmsProducer = jmsContext.createProducer();
			jmsProducer.send(queue, message);

			JMSConsumer jmsConsumer = jmsContext.createConsumer(queue);
			String receivedMessage = jmsConsumer.receiveBody(String.class);

			assertEquals(message, receivedMessage);
		}
	}
}
