package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.UCSBDiningCommonsMenuItem;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBDiningCommonsMenuItemRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@Api(description = "UCSBDiningCommonsMenuItem")
@RequestMapping("/api/ucsbdiningcommonsmenuitem")
@RestController
@Slf4j
public class UCSBDiningCommonsMenuItemController extends ApiController {

    @Autowired
    UCSBDiningCommonsMenuItemRepository ucsbDiningCommonsMenuItemRepository;

	@ApiOperation(value = "List all UCSB dining commons menu items")
	@PreAuthorize("hasRole('ROLE_USER')")
	@GetMapping("/all")
	public Iterable<UCSBDiningCommonsMenuItem> allMenuItems() {
        Iterable<UCSBDiningCommonsMenuItem> commons = ucsbDiningCommonsMenuItemRepository.findAll();
        return commons;
    }

    @ApiOperation(value = "Create a new menu item")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBDiningCommonsMenuItem postMenuItem(
            @ApiParam("diningCommonsCode") @RequestParam String diningCommonsCode,
            @ApiParam("name") @RequestParam String name,
            @ApiParam("station") @RequestParam String station
            )
    {
        UCSBDiningCommonsMenuItem menuItem = new UCSBDiningCommonsMenuItem();
        menuItem.setDiningCommonsCode(diningCommonsCode);
        menuItem.setName(name);
        menuItem.setStation(station);

        UCSBDiningCommonsMenuItem savedMenuItem = ucsbDiningCommonsMenuItemRepository.save(menuItem);

        return savedMenuItem;
    }
}