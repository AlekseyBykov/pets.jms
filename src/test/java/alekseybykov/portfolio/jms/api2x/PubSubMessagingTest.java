package alekseybykov.portfolio.jms.api2x;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import static org.junit.Assert.assertEquals;

/**
 * @author Aleksey Bykov
 * @since 23.03.2020
 */
public class PubSubMessagingTest {

	private static final String topicName = "topic/API2xPubSubMessagingTopic";
	private static Topic topic;

	@BeforeClass
	public static void setup() {
		try {
			InitialContext ctx = new InitialContext();
			topic = (Topic) ctx.lookup(topicName);
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSendTextMessageToTopicAndThenReceive() {
		try (ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
		     JMSContext jmsContext = activeMQConnectionFactory.createContext()) {

			String message = "message";

			JMSProducer jmsProducer = jmsContext.createProducer();

			JMSConsumer[] jmsConsumers = new JMSConsumer[3];
			jmsConsumers[0] = jmsContext.createConsumer(topic);
			jmsConsumers[1] = jmsContext.createConsumer(topic);
			jmsConsumers[2] = jmsContext.createConsumer(topic);

			jmsProducer.send(topic, message);

			String[] receivedMessages = new String[3];

			receivedMessages[0] = jmsConsumers[0].receiveBody(String.class);
			receivedMessages[1] = jmsConsumers[1].receiveBody(String.class);
			receivedMessages[2] = jmsConsumers[2].receiveBody(String.class);

			assertEquals(message, receivedMessages[0]);
			assertEquals(message, receivedMessages[1]);
			assertEquals(message, receivedMessages[2]);
		}
	}
}
