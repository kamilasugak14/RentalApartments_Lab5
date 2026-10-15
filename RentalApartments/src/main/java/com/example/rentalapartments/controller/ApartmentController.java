package com.example.rentalapartments.controller;

import com.example.rentalapartments.model.Apartment;
import com.example.rentalapartments.model.SearchCriteria;
import com.example.rentalapartments.model.SearchLog;
import com.example.rentalapartments.service.ApartmentService;
import com.example.rentalapartments.util.PriceFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ApartmentController {

    private final ApartmentService service;

    @Autowired
    private PriceFormatter priceFormatter;

    @Autowired
    private ApplicationContext context;

    public ApartmentController(ApartmentService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String home() { return "redirect:/apartments"; }

    @GetMapping("/apartments")
    public String search(@ModelAttribute("criteria") SearchCriteria criteria, Model model) {
        model.addAttribute("apartments", service.search(criteria));
        return "search";
    }

    @GetMapping("/owner/apartments")
    public String ownerList(Model model) {
        model.addAttribute("apartments", service.findAll());
        return "owner-list";
    }

    @GetMapping("/owner/apartments/new")
    public String newForm(Model model) {
        model.addAttribute("apartment", new Apartment());
        return "owner-form";
    }

    @GetMapping("/owner/apartments/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("apartment", service.findById(id));
        return "owner-form";
    }

    @PostMapping("/owner/apartments")
    public String save(@ModelAttribute Apartment apartment) {
        service.save(apartment);
        return "redirect:/owner/apartments";
    }

    @PostMapping("/owner/apartments/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/owner/apartments";
    }

    @GetMapping("/demo/scopes")
    @ResponseBody
    public String scopes() {
        boolean sameFormatter = context.getBean(PriceFormatter.class) == priceFormatter;
        SearchLog a = context.getBean(SearchLog.class);
        SearchLog b = context.getBean(SearchLog.class);
        return "singleton PriceFormatter однаковий: " + sameFormatter
                + "\nprototype SearchLog #1 = " + a.getId()
                + "\nprototype SearchLog #2 = " + b.getId()
                + "\nоднакові: " + (a == b);
    }
}