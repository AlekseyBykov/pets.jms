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
	private static final String topicName = "topic/jmsTopic";

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

			MessageConsumer firstConsumer = session.createConsumer(topic);
			MessageConsumer secondConsumer = session.createConsumer(topic);
			MessageConsumer thirdConsumer = session.createConsumer(topic);

			producer.send(textMessage);
			connection.start();

			TextMessage firstConsumerMsg = (TextMessage) firstConsumer.receive();
			TextMessage secondConsumerMsg = (TextMessage) secondConsumer.receive();
			TextMessage thirdConsumerMsg = (TextMessage) thirdConsumer.receive();

			assertEquals(message, firstConsumerMsg.getText());
			assertEquals(message, secondConsumerMsg.getText());
			assertEquals(message, thirdConsumerMsg.getText());

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
