package com.crm.controller;

import com.crm.entity.Customer;
import com.crm.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Customer Controller
 * Handles customer-related requests
 *
 * @author CRM Team
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerRepository customerRepository;

    @GetMapping
    public String listCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model,
            @AuthenticationPrincipal OAuth2User oauth2User) {

        log.info("Listing customers - page: {}, size: {}, keyword: {}", page, size, keyword);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Customer> customers;

        if (keyword != null && !keyword.trim().isEmpty()) {
            customers = customerRepository.searchCustomers(keyword, pageable);
        } else {
            customers = customerRepository.findAll(pageable);
        }

        model.addAttribute("customers", customers);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", customers.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("user", oauth2User);

        return "customers";
    }

    @GetMapping("/{id}")
    public String viewCustomer(@PathVariable Long id, Model model) {
        log.info("Viewing customer with id: {}", id);

        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isPresent()) {
            model.addAttribute("customer", customer.get());
            return "customer-detail";
        } else {
            return "redirect:/customers";
        }
    }

    @GetMapping("/new")
    public String newCustomerForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "customer-form";
    }

    @PostMapping("/save")
    public String saveCustomer(@ModelAttribute Customer customer) {
        log.info("Saving customer: {}", customer.getName());

        customerRepository.save(customer);
        return "redirect:/customers";
    }

    @GetMapping("/edit/{id}")
    public String editCustomerForm(@PathVariable Long id, Model model) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isPresent()) {
            model.addAttribute("customer", customer.get());
            return "customer-form";
        } else {
            return "redirect:/customers";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id) {
        log.info("Deleting customer with id: {}", id);

        customerRepository.deleteById(id);
        return "redirect:/customers";
    }
}
