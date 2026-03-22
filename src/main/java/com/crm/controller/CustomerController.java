package com.crm.controller;

import com.crm.entity.Customer;
import com.crm.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 客户管理控制器
 * 处理前端页面的客户相关请求，返回Thymeleaf模板页面
 *
 * @author CRM Team
 */
@Controller
@RequestMapping("/customers")
public class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * 客户列表页面
     * 分页展示所有客户，支持关键词搜索
     *
     * @param page 页码，默认0
     * @param size 每页数量，默认10
     * @param keyword 搜索关键词
     * @param model 模型对象，用于传递数据到视图
     * @return 客户列表页面模板名称
     */
    @GetMapping
    public String listCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        log.info("Listing customers - page: {}, size: {}, keyword: {}", page, size, keyword);

        // 构建分页请求，按创建时间倒序排列
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Customer> customers;

        // 根据是否有关键词进行不同查询
        if (keyword != null && !keyword.trim().isEmpty()) {
            customers = customerRepository.searchCustomers(keyword, pageable);
        } else {
            customers = customerRepository.findAll(pageable);
        }

        // 添加模型数据
        model.addAttribute("customers", customers);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", customers.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("userName", "管理员");

        return "customers";
    }

    /**
     * 客户详情页面
     *
     * @param id 客户ID
     * @param model 模型对象
     * @return 客户详情页面或重定向到列表
     */
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

    /**
     * 新建客户表单页面
     *
     * @param model 模型对象
     * @return 客户表单页面
     */
    @GetMapping("/new")
    public String newCustomerForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "customer-form";
    }

    /**
     * 保存客户（新建或更新）
     *
     * @param customer 客户对象
     * @return 重定向到客户列表
     */
    @PostMapping("/save")
    public String saveCustomer(@ModelAttribute Customer customer) {
        log.info("Saving customer: {}", customer.getCustomerName());

        customerRepository.save(customer);
        return "redirect:/customers";
    }

    /**
     * 编辑客户表单页面
     *
     * @param id 客户ID
     * @param model 模型对象
     * @return 客户表单页面或重定向到列表
     */
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
}
