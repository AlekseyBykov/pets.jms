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

public class PubSubMessagingTest {

	private static final String topicName = "topic/jmsTopic";
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

			JMSConsumer firstJmsConsumer = jmsContext.createConsumer(topic);
			JMSConsumer secondJmsConsumer = jmsContext.createConsumer(topic);
			JMSConsumer thirdJmsConsumer = jmsContext.createConsumer(topic);

			jmsProducer.send(topic, message);

			String firstConsumerMsg = firstJmsConsumer.receiveBody(String.class);
			String secondConsumerMsg = secondJmsConsumer.receiveBody(String.class);
			String thirdConsumerMsg = thirdJmsConsumer.receiveBody(String.class);

			assertEquals(message, firstConsumerMsg);
			assertEquals(message, secondConsumerMsg);
			assertEquals(message, thirdConsumerMsg);
		}
	}
}
