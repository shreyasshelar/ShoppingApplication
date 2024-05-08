package com.springboot.service;

import org.springframework.statemachine.StateMachine;

import com.springboot.domain.ShoppingEvents;
import com.springboot.domain.ShoppingOrder;
import com.springboot.domain.ShoppingStates;
//ORDER_BOOKED,PAYMENT_SUCCESSFUL, PAYMENT_FAILED, ITEM_SENT, DELIVERED, FULFILLED, ORDER_CANCEL
public interface ShoppingStateMachineService {

	ShoppingOrder newShoppingOrder(ShoppingOrder order);
	StateMachine<ShoppingStates, ShoppingEvents> acceptedPayment(Long orderId);
	StateMachine<ShoppingStates, ShoppingEvents> failedPayment(Long orderId);
	StateMachine<ShoppingStates, ShoppingEvents> itemSent(Long orderId);
	StateMachine<ShoppingStates, ShoppingEvents> itemDelivered(Long orderId);
	StateMachine<ShoppingStates, ShoppingEvents> orderFulfilled(Long orderId);
	StateMachine<ShoppingStates, ShoppingEvents> orderCancelled(Long orderId);
	StateMachine<ShoppingStates, ShoppingEvents> orderBooked(Long orderId);
}

