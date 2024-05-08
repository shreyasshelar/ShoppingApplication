package com.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springboot.domain.ShoppingEvents;
import com.springboot.domain.ShoppingOrder;
import com.springboot.domain.ShoppingStates;
import com.springboot.repository.ShoppingOrderRepository;
import com.springboot.service.ShoppingStateMachineServiceImpl;
import com.springboot.statemachine.ShoppingStateMachineFactory;

@Controller
@RequestMapping("order")
public class ShoppingOrderController {
	@Autowired
	private ShoppingStateMachineServiceImpl shoppingStateMachineService;

	@Autowired
	private ShoppingOrderRepository shoppingOrderRepository;

	@Autowired
	private ShoppingStateMachineFactory shoppingStateMachineFactory;

	ShoppingOrder savedOrder;

	@GetMapping("/customerForm")
	public String showCustomerForm() {
		return "customerForm";
	}

	@PostMapping("/create")
	public String createOrder(@ModelAttribute("order") ShoppingOrder order, RedirectAttributes redirectAttributes) {
		savedOrder = shoppingStateMachineService.newShoppingOrder(order);

		StateMachine<ShoppingStates, ShoppingEvents> sm = shoppingStateMachineFactory.getStateMachine();
		System.out.println("NEW");
		System.out.println(savedOrder.getState());
		System.err.println(savedOrder.getId());
		redirectAttributes.addFlashAttribute("orderId", savedOrder.getId());
		return "redirect:/order/paymentForm/" + savedOrder.getId();
	}

	@GetMapping("/paymentForm/{orderId}")
	public String showPaymentForm(@PathVariable Long orderId, Model model) {
		model.addAttribute("orderId", orderId);
		return "paymentForm";
	}

	@PostMapping("/payment/{orderId}")
	public String processPayment(@PathVariable("orderId") Long orderId,
			@RequestParam(name = "paymentSuccessful") String paymentSuccessful) {
		boolean isPaymentSuccessful = "true".equals(paymentSuccessful);
		System.err.println("order id: " + orderId);

		if (isPaymentSuccessful) {
			StateMachine<ShoppingStates, ShoppingEvents> sm = shoppingStateMachineService.acceptedPayment(orderId);
			ShoppingOrder paymentSuccessOrder = shoppingOrderRepository.getOne(orderId);
			System.out.println("Should accept\n" + sm.getState().getId());
			System.out.println(paymentSuccessOrder);
			return "redirect:/order/sendItem/" + orderId;
		} else {
			StateMachine<ShoppingStates, ShoppingEvents> sm = shoppingStateMachineService.failedPayment(orderId);
			ShoppingOrder paymentFailOrder = shoppingOrderRepository.getOne(orderId);
			System.out.println("Should fail\n" + sm.getState().getId());
			System.out.println(paymentFailOrder);
			return "redirect:/order/cancel/" + orderId;
		}

	}

	@GetMapping("/sendItem/{orderId}")
	public String sendItem(@PathVariable("orderId") Long orderId) {
		StateMachine<ShoppingStates, ShoppingEvents> sm = shoppingStateMachineService.itemSent(orderId);
		ShoppingOrder itemSentOrder = shoppingOrderRepository.getOne(orderId);
		System.out.println("Should sent item\n" + sm.getState().getId());
		System.out.println(itemSentOrder);
		return "redirect:/order/deliver/" + orderId;
	}

	@GetMapping("/deliver/{orderId}")
	public String markAsDelivered(@PathVariable Long orderId) {
		StateMachine<ShoppingStates, ShoppingEvents> sm = shoppingStateMachineService.itemDelivered(orderId);
		ShoppingOrder itemDeliveredOrder = shoppingOrderRepository.getOne(orderId);
		System.out.println("Should deliver item\n" + sm.getState().getId());
		System.out.println(itemDeliveredOrder);
		return "redirect:/order/fulfill/" + orderId;
	}

	@GetMapping("/fulfill/{orderId}")
	public String markAsFulfilled(@PathVariable Long orderId) {
		StateMachine<ShoppingStates, ShoppingEvents> sm = shoppingStateMachineService.orderFulfilled(orderId);
		ShoppingOrder fulfilledOrder = shoppingOrderRepository.getOne(orderId);
		System.out.println("Should fulfill order\n" + sm.getState().getId());
		System.out.println(fulfilledOrder);
		return "redirect:/order/booked/" + orderId;
	}

	@GetMapping("/booked/{orderId}")
	public String bookedOrder(@PathVariable Long orderId, RedirectAttributes redirectAttributes) {
		StateMachine<ShoppingStates, ShoppingEvents> sm = shoppingStateMachineService.orderBooked(orderId);
		ShoppingOrder bookedOrder = shoppingOrderRepository.getOne(orderId);
		System.out.println("Should book order\n" + sm.getState().getId());
		System.out.println(bookedOrder);
		redirectAttributes.addFlashAttribute("message", "Order with "+orderId+" Booked !");
		return "redirect:/order/success/" + orderId;
	}

	@GetMapping("/cancel/{orderId}")
	public String cancelOrder(@PathVariable Long orderId, RedirectAttributes redirectAttributes) {
		StateMachine<ShoppingStates, ShoppingEvents> sm = shoppingStateMachineService.orderCancelled(orderId);
		ShoppingOrder cancelledOrder = shoppingOrderRepository.getOne(orderId);
		System.out.println("Should cancel order\n" + sm.getState().getId());
		System.out.println(cancelledOrder);
		redirectAttributes.addFlashAttribute("message", "Order with "+orderId+" Cancelled !");
		return "redirect:/order/message/" + orderId;
	}
	
	@GetMapping("/success/{orderId}")
	public String showSuccessPage(Model model) {
		return "success"; // Return the success page
	}

	@GetMapping("/message/{orderId}")
	public String showMessagePage(Model model) {
		return "message"; // Return the message page
	}
	
	
}




