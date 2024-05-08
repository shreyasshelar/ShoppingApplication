package com.springboot.statemachine;

import java.util.EnumSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.transition.Transition;

import com.springboot.domain.ShoppingEvents;
import com.springboot.domain.ShoppingStates;

@Configuration
public class ShoppingStateMachineFactory implements StateMachineFactory<ShoppingStates, ShoppingEvents> {

	private final ApplicationContext applicationContext;

    @Autowired
    public ShoppingStateMachineFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

	@Override
	@Bean
	public StateMachine<ShoppingStates, ShoppingEvents> getStateMachine() {
		StateMachineBuilder.Builder<ShoppingStates, ShoppingEvents> builder = StateMachineBuilder.builder();
		try {
			builder.configureStates().withStates().initial(ShoppingStates.NEW_ORDER)
					.states(EnumSet.allOf(ShoppingStates.class)).end(ShoppingStates.ORDER_BOOKED)
					.end(ShoppingStates.ORDER_CANCELED);

			builder.configureTransitions().withExternal().source(ShoppingStates.NEW_ORDER)
					.target(ShoppingStates.PAYMENT_DONE).event(ShoppingEvents.PAYMENT_SUCCESSFUL).and().withExternal()
					.source(ShoppingStates.NEW_ORDER).target(ShoppingStates.PAYMENT_FAILED)
					.event(ShoppingEvents.PAYMENT_FAILED).and().withExternal().source(ShoppingStates.PAYMENT_DONE)
					.target(ShoppingStates.ITEM_SENT).event(ShoppingEvents.ITEM_SENT).and().withExternal()
					.source(ShoppingStates.ITEM_SENT).target(ShoppingStates.DELIVERED).event(ShoppingEvents.DELIVERED)
					.and().withExternal().source(ShoppingStates.DELIVERED).target(ShoppingStates.FULFILLED)
					.event(ShoppingEvents.FULFILLED).and().withExternal().source(ShoppingStates.FULFILLED)
					.target(ShoppingStates.ORDER_BOOKED).event(ShoppingEvents.ORDER_BOOKED).and().withExternal()
					.source(ShoppingStates.ORDER_BOOKED).target(ShoppingStates.ORDER_CANCELED)
					.event(ShoppingEvents.ORDER_CANCEL).and().withExternal().source(ShoppingStates.PAYMENT_FAILED)
					.target(ShoppingStates.ORDER_CANCELED).event(ShoppingEvents.ORDER_CANCEL);
		} catch (Exception e) {
			e.printStackTrace();
		}

		StateMachine<ShoppingStates, ShoppingEvents> stateMachine = builder.build();
		stateMachine.addStateListener(listener());
		System.err.println("UUID "+stateMachine.getUuid());
		return stateMachine;
	}

	@Override
	
	public StateMachine<ShoppingStates, ShoppingEvents> getStateMachine(String machineId) {
		// Implement this if required
		return null;
	}

	@Override
	
	public StateMachine<ShoppingStates, ShoppingEvents> getStateMachine(UUID uuid) {
		// Implement this if required
		return null;
	}

	@Bean
	public StateMachineListener<ShoppingStates, ShoppingEvents> listener() {
		return new StateMachineListenerAdapter<ShoppingStates, ShoppingEvents>() {
			@Override
			public void transition(Transition<ShoppingStates, ShoppingEvents> transition) {
				System.out.println(
						"Transition: " + transition.getSource().getId() + " -> " + transition.getTarget().getId());
			}
		};
	}
}

