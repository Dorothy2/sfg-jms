package guru.springframework.sfgjms.sender;

import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import guru.springframework.sfgjms.config.JmsConfig;
import guru.springframework.sfgjms.model.HelloWorldMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class HelloSender {
	
	private final JmsTemplate jmsTemplate;
	private final ObjectMapper objectMapper;

	@Scheduled(fixedRate = 2000)
	public void sendMessage() {
		System.out.println("Sending a message...");
		
		HelloWorldMessage message = HelloWorldMessage
				.builder()
				.id(UUID.randomUUID())
				.message("Hello World!")
				.build();
		
		// use overloaded version with destination and message
		jmsTemplate.convertAndSend(JmsConfig.MY_QUEUE, message);
		
		System.out.println("Message sent!");
	}
	
	@Scheduled(fixedRate = 2000)
	public void sendAndReceiveMessage() throws JMSException {
		System.out.println("Sending a message...");

		HelloWorldMessage message = HelloWorldMessage.builder().id(UUID.randomUUID()).message("Hello").build();

		// use overloaded version with destination and message
		Message receivedMessage = jmsTemplate.sendAndReceive(JmsConfig.MY_SEND_RCV_QUEUE, new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				Message helloMessage;
				try {
					helloMessage = session.createTextMessage(objectMapper.writeValueAsString(message));
					helloMessage.setStringProperty("_type", "guru.springframework.sfgjms.model.HelloWorldMessage");
				} catch (JsonProcessingException e) {
					throw new JMSException("ka-boom");
				} catch (JMSException e) {
					throw e;
				}
				
				System.out.println("Sending Hello");
				return helloMessage;
			}

		});

		System.out.println(receivedMessage.getBody(String.class));
	}
}
