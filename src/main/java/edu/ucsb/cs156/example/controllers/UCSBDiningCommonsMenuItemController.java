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

    @ApiOperation(value = "Get a single UCSB dining commons menu item")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public UCSBDiningCommonsMenuItem getById(
            @ApiParam("id of item to get") @RequestParam Long id){
            UCSBDiningCommonsMenuItem menuItem = ucsbDiningCommonsMenuItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBDiningCommonsMenuItem.class, id));

            return menuItem;
    }

    @ApiOperation(value = "Create a new menu item")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBDiningCommonsMenuItem postMenuItem(
            @ApiParam("name of dining commons where dish can be found, e.g. Carillo") @RequestParam String diningCommonsCode,
            @ApiParam("name of the dish, e.g. Chicken Fried Rice") @RequestParam String name,
            @ApiParam("name of station in the dining commons serving the dish, e.g. Entrees") @RequestParam String station
            )
    {
        UCSBDiningCommonsMenuItem menuItem = new UCSBDiningCommonsMenuItem();
        menuItem.setDiningCommonsCode(diningCommonsCode);
        menuItem.setName(name);
        menuItem.setStation(station);

        UCSBDiningCommonsMenuItem savedMenuItem = ucsbDiningCommonsMenuItemRepository.save(menuItem);

        return savedMenuItem;
    }

    @ApiOperation(value = "Update a single menu item")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public UCSBDiningCommonsMenuItem updateCommons(
            @ApiParam("id of menu item to update") @RequestParam Long id,
            @RequestBody @Valid UCSBDiningCommonsMenuItem incoming) {

        UCSBDiningCommonsMenuItem menuItem = ucsbDiningCommonsMenuItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBDiningCommonsMenuItem.class, id));


        menuItem.setDiningCommonsCode(incoming.getDiningCommonsCode());  
        menuItem.setName(incoming.getName());
        menuItem.setStation(incoming.getStation());

        ucsbDiningCommonsMenuItemRepository.save(menuItem);

        return menuItem;
    }

}
