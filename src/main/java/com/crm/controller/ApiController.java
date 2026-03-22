package com.crm.controller;

import com.crm.dto.ApiResponse;
import com.crm.entity.Customer;
import com.crm.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

/**
 * API Controller
 * Handles REST API requests for AJAX calls
 *
 * @author CRM Team
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    private static final Logger log = LoggerFactory.getLogger(ApiController.class);

    private final CustomerRepository customerRepository;

    public ApiController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

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
        log.info("API: Creating customer: {}", customer.getCustomerName());

        Customer savedCustomer = customerRepository.save(customer);
        return ApiResponse.success("Customer created successfully", savedCustomer);
    }

    @PutMapping("/customers/{id}")
    public ApiResponse<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        log.info("API: Updating customer with id: {}", id);

        return customerRepository.findById(id)
                .map(existingCustomer -> {
                    existingCustomer.setCustomerName(customer.getCustomerName());
                    existingCustomer.setCompanyName(customer.getCompanyName());
                    existingCustomer.setEmail(customer.getEmail());
                    existingCustomer.setPhone(customer.getPhone());
                    existingCustomer.setAddress(customer.getAddress());
                    existingCustomer.setIndustry(customer.getIndustry());
                    existingCustomer.setStatus(customer.getStatus());
                    existingCustomer.setRemark(customer.getRemark());
                    existingCustomer.setOwnerUserId(customer.getOwnerUserId());

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
            public final long totalCustomers = customerRepository.count();
            public final long activeCustomers = customerRepository.findByStatus("ACTIVE").size();
        };

        return ApiResponse.success(stats);
    }
}
