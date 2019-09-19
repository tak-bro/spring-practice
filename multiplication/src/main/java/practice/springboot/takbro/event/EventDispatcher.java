package practice.springboot.takbro.event;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// 이벤트 버스와의 통신을 처리
@Component
public class EventDispatcher {

    private RabbitTemplate rabbitTemplate;
    private String multiplicationExchange; // Multiplication 관련 정보를 전달하기 위한 익스체인지
    private String multiplicationSolvedRoutingKey; // Multiplication 관련 정보를 전달하기 위한 익스체인지

    @Autowired
    EventDispatcher(final RabbitTemplate rabbitTemplate,
                    @Value("${multiplication.exchange}") final String multiplicationExchange,
                    @Value("${multiplication,solved.key") final String multiplicationSolvedRoutingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.multiplicationExchange = multiplicationExchange;
        this.multiplicationSolvedRoutingKey = multiplicationSolvedRoutingKey;
    }

    public void send(final MultiplicationSolvedEvent multiplicationSolvedEvent) {
        rabbitTemplate.convertAndSend(multiplicationExchange, multiplicationSolvedRoutingKey, multiplicationSolvedEvent);
    }
}
