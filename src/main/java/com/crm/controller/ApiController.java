package com.crm.controller;

import com.crm.dto.ApiResponse;
import com.crm.entity.Customer;
import com.crm.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API Controller
 * Handles REST API requests for AJAX calls
 *
 * @author CRM Team
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final CustomerRepository customerRepository;

    @GetMapping("/customers")
    public ApiResponse<Page<Customer>> getCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        log.info("API: Fetching customers - page: {}, size: {}, keyword: {}", page, size, keyword);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Customer> customers;

        if (keyword != null && !keyword.trim().isEmpty()) {
            customers = customerRepository.searchCustomers(keyword, pageable);
        } else {
            customers = customerRepository.findAll(pageable);
        }

        return ApiResponse.success(customers);
    }

    @GetMapping("/customers/{id}")
    public ApiResponse<Customer> getCustomer(@PathVariable Long id) {
        log.info("API: Fetching customer with id: {}", id);

        return customerRepository.findById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error("Customer not found"));
    }

    @PostMapping("/customers")
    public ApiResponse<Customer> createCustomer(@RequestBody Customer customer) {
        log.info("API: Creating customer: {}", customer.getName());

        Customer savedCustomer = customerRepository.save(customer);
        return ApiResponse.success("Customer created successfully", savedCustomer);
    }

    @PutMapping("/customers/{id}")
    public ApiResponse<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        log.info("API: Updating customer with id: {}", id);

        return customerRepository.findById(id)
                .map(existingCustomer -> {
                    existingCustomer.setName(customer.getName());
                    existingCustomer.setCompanyName(customer.getCompanyName());
                    existingCustomer.setEmail(customer.getEmail());
                    existingCustomer.setPhone(customer.getPhone());
                    existingCustomer.setAddress(customer.getAddress());
                    existingCustomer.setIndustry(customer.getIndustry());
                    existingCustomer.setStatus(customer.getStatus());
                    existingCustomer.setNotes(customer.getNotes());
                    existingCustomer.setAssignedUserId(customer.getAssignedUserId());
                    existingCustomer.setRevenue(customer.getRevenue());

                    Customer updatedCustomer = customerRepository.save(existingCustomer);
                    return ApiResponse.success("Customer updated successfully", updatedCustomer);
                })
                .orElse(ApiResponse.error("Customer not found"));
    }

    @DeleteMapping("/customers/{id}")
    public ApiResponse<Void> deleteCustomer(@PathVariable Long id) {
        log.info("API: Deleting customer with id: {}", id);

        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return ApiResponse.success("Customer deleted successfully", null);
        } else {
            return ApiResponse.error("Customer not found");
        }
    }

    @GetMapping("/stats/summary")
    public ApiResponse<Object> getSummaryStats() {
        log.info("API: Fetching summary statistics");

        long totalCustomers = customerRepository.count();
        long activeCustomers = customerRepository.findByStatus("ACTIVE").size();

        var stats = new Object() {
            public final long totalCustomers = CustomerController.this.customerRepository.count();
            public final long activeCustomers = customerRepository.findByStatus("ACTIVE").size();
        };

        return ApiResponse.success(stats);
    }
}
