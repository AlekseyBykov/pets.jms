package alekseybykov.portfolio.jms.api2x;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import static org.junit.Assert.assertEquals;

public class PriorityTest {

	private static final String queueName = "queue/API2xPriorityQueue";
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
	public void testPrioritizeAndSendTextMessagesToQueueAndThenReceiveByPriority() {
		try (ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
		     JMSContext jmsContext = activeMQConnectionFactory.createContext()) {

			JMSProducer jmsProducer = jmsContext.createProducer();

			Message receivedMessage;
			String[] messages = new String[4];
			messages[0] = "message_1";
			messages[1] = "message_2";
			messages[2] = "message_3";
			messages[3] = "message_4";

			jmsProducer.setPriority(9);
			jmsProducer.send(queue, messages[0]);

			jmsProducer.setPriority(6);
			jmsProducer.send(queue, messages[1]);

			jmsProducer.setPriority(5);
			jmsProducer.send(queue, messages[2]);

			jmsProducer.setPriority(7);
			jmsProducer.send(queue, messages[3]);

			JMSConsumer jmsConsumer = jmsContext.createConsumer(queue);

			receivedMessage = jmsConsumer.receive();
			assertEquals(messages[0], receivedMessage.getBody(String.class));
			assertEquals(9, receivedMessage.getJMSPriority());

			receivedMessage = jmsConsumer.receive();
			assertEquals(messages[3], receivedMessage.getBody(String.class));
			assertEquals(7, receivedMessage.getJMSPriority());

			receivedMessage = jmsConsumer.receive();
			assertEquals(messages[1], receivedMessage.getBody(String.class));
			assertEquals(6, receivedMessage.getJMSPriority());

			receivedMessage = jmsConsumer.receive();
			assertEquals(messages[2], receivedMessage.getBody(String.class));
			assertEquals(5, receivedMessage.getJMSPriority());

		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
