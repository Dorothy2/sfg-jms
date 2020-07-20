package guru.springframework.sfgjms.sender;

import java.util.UUID;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import guru.springframework.sfgjms.config.JmsConfig;
import guru.springframework.sfgjms.model.HelloWorldMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class HelloSender {
	
	private final JmsTemplate jmsTemplate;

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
}
