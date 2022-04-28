package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBDiningCommonsMenuItem;
import edu.ucsb.cs156.example.repositories.UCSBDiningCommonsMenuItemRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UCSBDiningCommonsMenuItemController.class)
@Import(TestConfig.class)
public class UCSBDiningCommonsMenuItemControllerTests extends ControllerTestCase {

    @MockBean
    UCSBDiningCommonsMenuItemRepository ucsbDiningCommonsMenuItemRepository;

    @MockBean
    UserRepository userRepository;

    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
            mockMvc.perform(get("/api/ucsbdiningcommonsmenuitem/all"))
                            .andExpect(status().is(403)); // logged out users can't get all
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_users_can_get_all() throws Exception {
            mockMvc.perform(get("/api/ucsbdiningcommonsmenuitem/all"))
                            .andExpect(status().is(200)); // logged
    }

    @Test
    public void logged_out_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/ucsbdiningcommonsmenuitem/post"))
                            .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/ucsbdiningcommonsmenuitem/post"))
                            .andExpect(status().is(403)); // only admins can post
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_can_get_all_ucsbdiningcommonsmenuitems() throws Exception {

            // arrange

            UCSBDiningCommonsMenuItem meat_dish = UCSBDiningCommonsMenuItem.builder()
                            .diningCommonsCode("ortega")
                            .name("meat_dish")
                            .station("station1")
                            .build();

            UCSBDiningCommonsMenuItem veg_dish = UCSBDiningCommonsMenuItem.builder()
                            .diningCommonsCode("dlg")
                            .name("veg_dish")
                            .station("station2")
                            .build();

            ArrayList<UCSBDiningCommonsMenuItem> expectedMenuItems = new ArrayList<>();
            expectedMenuItems.addAll(Arrays.asList(meat_dish, veg_dish));

            when(ucsbDiningCommonsMenuItemRepository.findAll()).thenReturn(expectedMenuItems);

            // act
            MvcResult response = mockMvc.perform(get("/api/ucsbdiningcommonsmenuitem/all"))
                            .andExpect(status().isOk()).andReturn();

            // assert

            verify(ucsbDiningCommonsMenuItemRepository, times(1)).findAll();
            String expectedJson = mapper.writeValueAsString(expectedMenuItems);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void an_admin_user_can_post_a_new_commons() throws Exception {
            // arrange

            UCSBDiningCommonsMenuItem meat_dish = UCSBDiningCommonsMenuItem.builder()
                            .diningCommonsCode("ortega")
                            .name("meat_dish")
                            .station("station1")
                            .build();

            when(ucsbDiningCommonsMenuItemRepository.save(eq(meat_dish))).thenReturn(meat_dish);

            // act
            MvcResult response = mockMvc.perform(
                            post("/api/ucsbdiningcommonsmenuitem/post?diningCommonsCode=ortega&name=meat_dish&station=station1")
                                            .with(csrf()))
                            .andExpect(status().isOk()).andReturn();

            // assert
            verify(ucsbDiningCommonsMenuItemRepository, times(1)).save(meat_dish);
            String expectedJson = mapper.writeValueAsString(meat_dish);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

}