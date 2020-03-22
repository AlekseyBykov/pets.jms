package alekseybykov.portfolio.jms.api1x;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import static java.util.Objects.nonNull;
import static org.junit.Assert.assertEquals;

public class P2PMessagingTest {

	private static final String connectionFactoryName = "ConnectionFactory";
	private static final String queueName = "queue/jmsQueue";

	private static InitialContext initialContext;
	private static Connection connection;
	private static Queue queue;

	@BeforeClass
	public static void setup() {
		try {
			initialContext = new InitialContext();
			ConnectionFactory cf = (ConnectionFactory) initialContext.lookup(connectionFactoryName);
			connection = cf.createConnection();
			queue = (Queue) initialContext.lookup(queueName);
		} catch (NamingException | JMSException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSendTextMessageToQueueAndThenReceive() {
		try {
			String message = "message";
			long timeout = 3000;

			Session session = connection.createSession();
			MessageProducer producer = session.createProducer(queue);
			TextMessage textMessage = session.createTextMessage(message);
			producer.send(textMessage);

			MessageConsumer consumer = session.createConsumer(queue);
			connection.start();

			TextMessage receivedMessage = (TextMessage) consumer.receive(timeout);

			assertEquals(message, receivedMessage.getText());
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
