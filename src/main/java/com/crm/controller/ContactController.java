package com.crm.controller;

import com.crm.entity.Contact;
import com.crm.repository.ContactRepository;
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

import java.util.List;

/**
 * Contact Controller
 * Handles contact-related requests
 *
 * @author CRM Team
 */
@Controller
@RequestMapping("/contacts")
public class ContactController {

    private static final Logger log = LoggerFactory.getLogger(ContactController.class);

    private final ContactRepository contactRepository;
    private final CustomerRepository customerRepository;

    public ContactController(ContactRepository contactRepository, CustomerRepository customerRepository) {
        this.contactRepository = contactRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping
    public String listContacts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        log.info("Listing contacts - page: {}, size: {}, keyword: {}", page, size, keyword);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Contact> contacts;

        if (keyword != null && !keyword.trim().isEmpty()) {
            contacts = contactRepository.searchContacts(keyword, pageable);
        } else {
            contacts = contactRepository.findAll(pageable);
        }

        model.addAttribute("contacts", contacts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contacts.getTotalPages());
        model.addAttribute("keyword", keyword);

        return "contacts";
    }

    @GetMapping("/{id}")
    public String viewContact(@PathVariable Long id, Model model) {
        log.info("Viewing contact with id: {}", id);

        Contact contact = contactRepository.findById(id).orElse(null);
        if (contact != null) {
            model.addAttribute("contact", contact);
            return "contact-detail";
        } else {
            return "redirect:/contacts";
        }
    }

    @GetMapping("/new")
    public String newContactForm(Model model) {
        model.addAttribute("contact", new Contact());
        model.addAttribute("customers", customerRepository.findAll());
        return "contact-form";
    }

    @PostMapping("/save")
    public String saveContact(@ModelAttribute Contact contact) {
        log.info("Saving contact: {} {}", contact.getFirstName(), contact.getLastName());

        contactRepository.save(contact);
        return "redirect:/contacts";
    }

    @GetMapping("/edit/{id}")
    public String editContactForm(@PathVariable Long id, Model model) {
        Contact contact = contactRepository.findById(id).orElse(null);
        if (contact != null) {
            model.addAttribute("contact", contact);
            model.addAttribute("customers", customerRepository.findAll());
            return "contact-form";
        } else {
            return "redirect:/contacts";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteContact(@PathVariable Long id) {
        log.info("Deleting contact with id: {}", id);

        contactRepository.deleteById(id);
        return "redirect:/contacts";
    }
}
