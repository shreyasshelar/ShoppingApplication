package com.springboot.service;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import com.springboot.domain.ShoppingEvents;
import com.springboot.domain.ShoppingOrder;
import com.springboot.domain.ShoppingStates;
import com.springboot.repository.ShoppingOrderRepository;
import com.springboot.statemachine.ShoppingStateMachineFactory;

import jakarta.transaction.Transactional;

@Service
public class ShoppingStateMachineServiceImpl implements ShoppingStateMachineService {

	private final ShoppingOrderRepository shoppingOrderRepository;
	private final ShoppingStateMachineFactory stateMachineFactory;
	private final OrderStateChangeInterceptor orderStateChangeInterceptor;
	final static String ORDER_ID_HEADER = "order_id";

	public ShoppingStateMachineServiceImpl(ShoppingOrderRepository shoppingOrderRepository,
			ShoppingStateMachineFactory stateMachineFactory,
			OrderStateChangeInterceptor orderStateChangeInterceptor) {
		this.shoppingOrderRepository = shoppingOrderRepository;
		this.stateMachineFactory = stateMachineFactory;
		this.orderStateChangeInterceptor = orderStateChangeInterceptor;
	}

	@Override
	public ShoppingOrder newShoppingOrder(ShoppingOrder order) {
		order.setState(ShoppingStates.NEW_ORDER);
		System.err.println("order: " + order);
		ShoppingOrder so = shoppingOrderRepository.save(order);
		return so;
	}

	@Transactional
	@Override
	public StateMachine<ShoppingStates, ShoppingEvents> acceptedPayment(Long orderId) {
		StateMachine<ShoppingStates, ShoppingEvents> sm = build(orderId);
		sendEvent(orderId, sm, ShoppingEvents.PAYMENT_SUCCESSFUL);
		return sm;
	}

	@Transactional
	@Override
	public StateMachine<ShoppingStates, ShoppingEvents> failedPayment(Long orderId) {
		StateMachine<ShoppingStates, ShoppingEvents> sm = build(orderId);
		sendEvent(orderId, sm, ShoppingEvents.PAYMENT_FAILED);
		return sm;
	}

	@Transactional
	@Override
	public StateMachine<ShoppingStates, ShoppingEvents> itemSent(Long orderId) {
		StateMachine<ShoppingStates, ShoppingEvents> sm = build(orderId);
		sendEvent(orderId, sm, ShoppingEvents.ITEM_SENT);
		return sm;
	}

	@Transactional
	@Override
	public StateMachine<ShoppingStates, ShoppingEvents> itemDelivered(Long orderId) {
		StateMachine<ShoppingStates, ShoppingEvents> sm = build(orderId);
		sendEvent(orderId, sm, ShoppingEvents.DELIVERED);
		return sm;
	}

	@Transactional
	@Override
	public StateMachine<ShoppingStates, ShoppingEvents> orderFulfilled(Long orderId) {
		StateMachine<ShoppingStates, ShoppingEvents> sm = build(orderId);
		sendEvent(orderId, sm, ShoppingEvents.FULFILLED);
		return sm;
	}

	@Transactional
	@Override
	public StateMachine<ShoppingStates, ShoppingEvents> orderCancelled(Long orderId) {
		StateMachine<ShoppingStates, ShoppingEvents> sm = build(orderId);
		sendEvent(orderId, sm, ShoppingEvents.ORDER_CANCEL);
		return sm;
	}

	@Transactional
	@Override
	public StateMachine<ShoppingStates, ShoppingEvents> orderBooked(Long orderId) {
		StateMachine<ShoppingStates, ShoppingEvents> sm = build(orderId);
		sendEvent(orderId, sm, ShoppingEvents.ORDER_BOOKED);
		return sm;
	}

	private void sendEvent(Long orderId, StateMachine<ShoppingStates, ShoppingEvents> sm, ShoppingEvents event) {
		Message msg = MessageBuilder.withPayload(event).setHeader(ORDER_ID_HEADER, orderId).build();

		sm.sendEvent(msg);

	}

	public StateMachine<ShoppingStates, ShoppingEvents> build(Long orderId) {
        ShoppingOrder order = shoppingOrderRepository.getOne(orderId);

        StateMachine<ShoppingStates, ShoppingEvents> sm = stateMachineFactory.getStateMachine();

        sm.getStateMachineAccessor().doWithAllRegions(sma -> {
            sma.addStateMachineInterceptor(orderStateChangeInterceptor);
            sma.resetStateMachine(new DefaultStateMachineContext<>(order.getState(), null, null, null));
        });

        sm.start();
        return sm;
    }

}
