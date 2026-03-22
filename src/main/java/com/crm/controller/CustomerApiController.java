package com.crm.controller;

import com.crm.dto.ApiResponse;
import com.crm.dto.CustomerDTO;
import com.crm.dto.PageResult;
import com.crm.entity.Customer;
import com.crm.entity.CustomerTag;
import com.crm.repository.CustomerTagRepository;
import com.crm.service.CustomerService;
import com.crm.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Customer Controller - 客户管理API
 *
 * @author CRM Team
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerApiController {

    private static final Logger log = LoggerFactory.getLogger(CustomerApiController.class);

    private final CustomerService customerService;
    private final CustomerTagRepository customerTagRepository;

    public CustomerApiController(CustomerService customerService, CustomerTagRepository customerTagRepository) {
        this.customerService = customerService;
        this.customerTagRepository = customerTagRepository;
    }

    /**
     * 创建客户
     */
    @PostMapping
    public ApiResponse<Customer> createCustomer(@RequestBody CustomerDTO customerDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        Customer customer = customerService.createCustomer(customerDTO, userId);
        return ApiResponse.success("客户创建成功", customer);
    }

    /**
     * 更新客户
     */
    @PutMapping("/{id}")
    public ApiResponse<Customer> updateCustomer(
            @PathVariable Long id,
            @RequestBody CustomerDTO customerDTO) {

        Long userId = SecurityUtils.getCurrentUserId();
        Customer customer = customerService.updateCustomer(id, customerDTO, userId);
        return ApiResponse.success("客户更新成功", customer);
    }

    /**
     * 删除客户
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCustomer(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        customerService.deleteCustomer(id, userId);
        return ApiResponse.success("客户删除成功", null);
    }

    /**
     * 获取客户详情
     */
    @GetMapping("/{id}")
    public ApiResponse<Customer> getCustomer(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id);
        return ApiResponse.success(customer);
    }

    /**
     * 分页查询客户列表
     */
    @GetMapping
    public ApiResponse<PageResult<Customer>> getCustomerList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long ownerUserId,
            @RequestParam(required = false) Boolean isPublic,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResult<Customer> result = customerService.getCustomerList(
                keyword, status, ownerUserId, isPublic, page, size);
        return ApiResponse.success(result);
    }

    /**
     * 认领公海客户
     */
    @PostMapping("/{id}/claim")
    public ApiResponse<Customer> claimCustomer(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        Customer customer = customerService.claimPublicCustomer(id, userId);
        return ApiResponse.success("客户认领成功", customer);
    }

    /**
     * 退回客户到公海
     */
    @PostMapping("/{id}/return")
    public ApiResponse<Customer> returnToPublic(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        Customer customer = customerService.returnToPublic(id, userId);
        return ApiResponse.success("客户已退回公海", customer);
    }

    /**
     * 分配客户
     */
    @PostMapping("/{id}/assign")
    public ApiResponse<Customer> assignCustomer(
            @PathVariable Long id,
            @RequestParam Long targetUserId) {

        Long userId = SecurityUtils.getCurrentUserId();
        Customer customer = customerService.assignCustomer(id, targetUserId, userId);
        return ApiResponse.success("客户分配成功", customer);
    }

    /**
     * 添加客户标签
     */
    @PostMapping("/{id}/tags")
    public ApiResponse<Void> addTag(
            @PathVariable Long id,
            @RequestParam Long tagId) {

        customerService.addTag(id, tagId);
        return ApiResponse.success("标签添加成功", null);
    }

    /**
     * 移除客户标签
     */
    @DeleteMapping("/{id}/tags/{tagId}")
    public ApiResponse<Void> removeTag(
            @PathVariable Long id,
            @PathVariable Long tagId) {

        customerService.removeTag(id, tagId);
        return ApiResponse.success("标签移除成功", null);
    }

    /**
     * 获取所有标签
     */
    @GetMapping("/tags")
    public ApiResponse<List<CustomerTag>> getAllTags() {
        List<CustomerTag> tags = customerTagRepository.findAllByOrderByCreateTimeDesc();
        return ApiResponse.success(tags);
    }

    /**
     * 创建标签
     */
    @PostMapping("/tags")
    public ApiResponse<CustomerTag> createTag(
            @RequestParam String tagName,
            @RequestParam(required = false) String tagDesc) {

        Long userId = SecurityUtils.getCurrentUserId();

        CustomerTag tag = new CustomerTag();
        tag.setTagName(tagName);
        tag.setTagDesc(tagDesc);
        tag.setCreateUserId(userId);

        CustomerTag saved = customerTagRepository.save(tag);
        return ApiResponse.success("标签创建成功", saved);
    }

    /**
     * 获取客户统计
     */
    @GetMapping("/stats")
    public ApiResponse<Object> getStats() {
        Object stats = customerService.getCustomerStats();
        return ApiResponse.success(stats);
    }
}
