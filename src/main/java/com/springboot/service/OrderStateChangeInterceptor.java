package com.springboot.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import com.springboot.domain.ShoppingEvents;
import com.springboot.domain.ShoppingOrder;
import com.springboot.domain.ShoppingStates;
import com.springboot.repository.ShoppingOrderRepository;

@Component
public class OrderStateChangeInterceptor extends StateMachineInterceptorAdapter<ShoppingStates, ShoppingEvents> {

	
	private final ShoppingOrderRepository shoppingOrderRepository;

	@Autowired
	public OrderStateChangeInterceptor(ShoppingOrderRepository shoppingOrderRepository) {
		this.shoppingOrderRepository = shoppingOrderRepository;
	}

	@Override
		public void preStateChange(State<ShoppingStates, ShoppingEvents> state, Message<ShoppingEvents> message,
			Transition<ShoppingStates, ShoppingEvents> transition,
			StateMachine<ShoppingStates, ShoppingEvents> stateMachine,
			StateMachine<ShoppingStates, ShoppingEvents> rootStateMachine) {
			Optional.ofNullable(message).ifPresent(msg -> {
				Optional.ofNullable(Long.class.cast(msg.getHeaders().getOrDefault(ShoppingStateMachineServiceImpl.ORDER_ID_HEADER, -1L)))
				.ifPresent(orderId -> {
				ShoppingOrder order = shoppingOrderRepository.getOne(orderId); 
				order.setState(state.getId());
				shoppingOrderRepository.save(order);

				});

				});
		}
}
