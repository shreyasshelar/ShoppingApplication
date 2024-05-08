package com.springboot.statemachine;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.messaging.Message;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.statemachine.access.StateMachineAccessor;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

import com.springboot.domain.ShoppingEvents;
import com.springboot.domain.ShoppingStates;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ShoppingStateMachine implements StateMachine<ShoppingStates, ShoppingEvents> {
	private StateMachine<ShoppingStates, ShoppingEvents> stateMachine;

    public ShoppingStateMachine(StateMachine<ShoppingStates, ShoppingEvents> stateMachine) {
        this.stateMachine = stateMachine;
    }

    @Override
    public UUID getUuid() {
        return stateMachine.getUuid();
    }

    @Override
    public String getId() {
        return stateMachine.getId();
    }

    @Override
    public void start() {
        stateMachine.start();
    }

    @Override
    public void stop() {
        stateMachine.stop();
    }

    @Override
    public boolean sendEvent(Message<ShoppingEvents> event) {
        return stateMachine.sendEvent(event);
    }

    @Override
    public boolean sendEvent(ShoppingEvents event) {
        return stateMachine.sendEvent(event);
    }

    @Override
    public Flux<StateMachineEventResult<ShoppingStates, ShoppingEvents>> sendEvents(Flux<Message<ShoppingEvents>> events) {
        return stateMachine.sendEvents(events);
    }

    @Override
    public Flux<StateMachineEventResult<ShoppingStates, ShoppingEvents>> sendEvent(Mono<Message<ShoppingEvents>> event) {
        return stateMachine.sendEvent(event);
    }

    @Override
    public Mono<List<StateMachineEventResult<ShoppingStates, ShoppingEvents>>> sendEventCollect(Mono<Message<ShoppingEvents>> event) {
        return stateMachine.sendEventCollect(event);
    }

    @Override
    public State<ShoppingStates, ShoppingEvents> getState() {
        return stateMachine.getState();
    }

    @Override
    public Collection<State<ShoppingStates, ShoppingEvents>> getStates() {
        return stateMachine.getStates();
    }

    @Override
    public Collection<Transition<ShoppingStates, ShoppingEvents>> getTransitions() {
        return stateMachine.getTransitions();
    }

    @Override
    public boolean isComplete() {
        return stateMachine.isComplete();
    }

    @Override
    public void addStateListener(StateMachineListener<ShoppingStates, ShoppingEvents> listener) {
        stateMachine.addStateListener(listener);
    }

    @Override
    public void removeStateListener(StateMachineListener<ShoppingStates, ShoppingEvents> listener) {
        stateMachine.removeStateListener(listener);
    }

    @Override
    public State<ShoppingStates, ShoppingEvents> getInitialState() {
        return stateMachine.getInitialState();
    }

    @Override
    public ExtendedState getExtendedState() {
        return stateMachine.getExtendedState();
    }

    @Override
    public StateMachineAccessor<ShoppingStates, ShoppingEvents> getStateMachineAccessor() {
        return stateMachine.getStateMachineAccessor();
    }

    @Override
    public void setStateMachineError(Exception exception) {
        stateMachine.setStateMachineError(exception);
    }

    @Override
    public boolean hasStateMachineError() {
        return stateMachine.hasStateMachineError();
    }

}
