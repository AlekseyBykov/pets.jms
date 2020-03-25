package alekseybykov.portfolio.jms.api1x;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import static java.util.Objects.nonNull;
import static org.junit.Assert.assertEquals;

public class PubSubMessagingTest {

	private static final String connectionFactoryName = "ConnectionFactory";
	private static final String topicName = "topic/API1xPubSubMessagingTopic";

	private static InitialContext initialContext;
	private static Connection connection;
	private static Topic topic;

	@BeforeClass
	public static void setup() {
		try {
			initialContext = new InitialContext();
			ConnectionFactory cf = (ConnectionFactory) initialContext.lookup(connectionFactoryName);
			connection = cf.createConnection();
			topic = (Topic) initialContext.lookup(topicName);
		} catch (NamingException | JMSException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSendTextMessageToTopicAndThenReceive() {
		try {
			String message = "message";

			Session session = connection.createSession();
			MessageProducer producer = session.createProducer(topic);
			TextMessage textMessage = session.createTextMessage(message);

			MessageConsumer[] messageConsumers = new MessageConsumer[3];

			messageConsumers[0] = session.createConsumer(topic);
			messageConsumers[1] = session.createConsumer(topic);
			messageConsumers[2] = session.createConsumer(topic);

			producer.send(textMessage);
			connection.start();

			TextMessage[] textMessages = new TextMessage[3];

			textMessages[0] = (TextMessage) messageConsumers[0].receive();
			textMessages[1] = (TextMessage) messageConsumers[1].receive();
			textMessages[2] = (TextMessage) messageConsumers[2].receive();

			assertEquals(message, textMessages[0].getText());
			assertEquals(message, textMessages[1].getText());
			assertEquals(message, textMessages[2].getText());

		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void teardown() {
		if (nonNull(initialContext)) {
			try {
				initialContext.close();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}

		if (nonNull(connection)) {
			try {
				connection.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}
}
