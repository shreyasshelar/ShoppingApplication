package com.springboot.configuration;

import java.util.EnumSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

import com.springboot.domain.ShoppingEvents;
import com.springboot.domain.ShoppingStates;
import com.springboot.statemachine.ShoppingStateMachine;
import com.springboot.statemachine.ShoppingStateMachineFactory;

@Configuration
@EnableStateMachine
public class ShoppingStateMachineConfig extends StateMachineConfigurerAdapter<ShoppingStates, ShoppingEvents> {

	@Autowired
    private final ShoppingStateMachineFactory stateMachineFactory;

    public ShoppingStateMachineConfig(ShoppingStateMachineFactory stateMachineFactory) {
        this.stateMachineFactory = stateMachineFactory;
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<ShoppingStates, ShoppingEvents> config)
            throws Exception {
        config.withConfiguration()
                .listener(new StateMachineListenerAdapter<ShoppingStates, ShoppingEvents>() {
                    @Override
                    public void stateChanged(State<ShoppingStates, ShoppingEvents> from,
                            State<ShoppingStates, ShoppingEvents> to) {
                        System.out.println("Transitioning from " + from.getId() + " to " + to.getId());
                    }
                });
    }

    @Override
    public void configure(StateMachineStateConfigurer<ShoppingStates, ShoppingEvents> states) throws Exception {
        states.withStates()
                .initial(ShoppingStates.NEW_ORDER)
                .states(EnumSet.allOf(ShoppingStates.class))
                .end(ShoppingStates.ORDER_BOOKED)
                .end(ShoppingStates.ORDER_CANCELED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ShoppingStates, ShoppingEvents> transitions)
            throws Exception {
        transitions.withExternal()
                .source(ShoppingStates.NEW_ORDER).target(ShoppingStates.PAYMENT_DONE)
                .event(ShoppingEvents.PAYMENT_SUCCESSFUL)
                .and()
                .withExternal()
                .source(ShoppingStates.NEW_ORDER).target(ShoppingStates.PAYMENT_FAILED)
                .event(ShoppingEvents.PAYMENT_FAILED)
                .and()
                .withExternal()
                .source(ShoppingStates.PAYMENT_DONE).target(ShoppingStates.ITEM_SENT)
                .event(ShoppingEvents.ITEM_SENT)
                .and()
                .withExternal()
                .source(ShoppingStates.ITEM_SENT).target(ShoppingStates.DELIVERED)
                .event(ShoppingEvents.DELIVERED)
                .and()
                .withExternal()
                .source(ShoppingStates.DELIVERED).target(ShoppingStates.FULFILLED)
                .event(ShoppingEvents.FULFILLED)
                .and()
                .withExternal()
                .source(ShoppingStates.FULFILLED).target(ShoppingStates.ORDER_BOOKED)
                .event(ShoppingEvents.ORDER_BOOKED)
                .and()
                .withExternal()
                .source(ShoppingStates.ORDER_BOOKED).target(ShoppingStates.ORDER_CANCELED)
                .event(ShoppingEvents.ORDER_CANCEL)
                .and()
                .withExternal()
                .source(ShoppingStates.PAYMENT_FAILED).target(ShoppingStates.ORDER_CANCELED)
                .event(ShoppingEvents.ORDER_CANCEL);
    }

    @Bean
    public StateMachine<ShoppingStates, ShoppingEvents> getStateMachine() throws Exception {
        return new ShoppingStateMachine(stateMachineFactory.getStateMachine());
    }

    @Bean
    public StateMachineListener<ShoppingStates, ShoppingEvents> listener() {
        return new StateMachineListenerAdapter<ShoppingStates, ShoppingEvents>() {
            @Override
            public void transition(Transition<ShoppingStates, ShoppingEvents> transition) {
                System.out.println("Transition: " + transition.getSource().getId() + " -> " + transition.getTarget().getId());
            }
        };
    }
}
