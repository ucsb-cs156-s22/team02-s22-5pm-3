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
            mockMvc.perform(get("/api/UCSBDiningCommonsMenuItem/all"))
                            .andExpect(status().is(403)); // logged out users can't get all
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_users_can_get_all() throws Exception {
            mockMvc.perform(get("/api/UCSBDiningCommonsMenuItem/all"))
                            .andExpect(status().is(200)); // logged
    }

    @Test
    public void logged_out_users_cannot_get_by_id() throws Exception {
            mockMvc.perform(get("/api/UCSBDiningCommonsMenuItem?id=1"))
                            .andExpect(status().is(403)); // logged out users can't get by id
    }


    @Test
    public void logged_out_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/UCSBDiningCommonsMenuItem/post"))
                            .andExpect(status().is(403));
    }

    @Test
    public void logged_out_users_cannot_put() throws Exception {
            mockMvc.perform(post("/api/UCSBDiningCommonsMenuItem/put"))
                            .andExpect(status().is(403));
    }

    @Test
    public void logged_out_users_cannot_delete() throws Exception {
            mockMvc.perform(post("/api/UCSBDiningCommonsMenuItem/delete"))
                            .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/UCSBDiningCommonsMenuItem/post"))
                            .andExpect(status().is(403)); // only admins can post
    }

	@WithMockUser(roles = { "USER" })
	@Test
	public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

			// arrange

			UCSBDiningCommonsMenuItem menuItem = UCSBDiningCommonsMenuItem.builder()
                            .diningCommonsCode("ortega")
                            .name("meat_dish")
                            .station("station1")
							.build();

			when(ucsbDiningCommonsMenuItemRepository.findById(eq(1L))).thenReturn(Optional.of(menuItem));

			// act
			MvcResult response = mockMvc.perform(get("/api/UCSBDiningCommonsMenuItem?id=1"))
							.andExpect(status().isOk()).andReturn();

			// assert

			verify(ucsbDiningCommonsMenuItemRepository, times(1)).findById(eq(1L));
			String expectedJson = mapper.writeValueAsString(menuItem);
			String responseString = response.getResponse().getContentAsString();
			assertEquals(expectedJson, responseString);
	}

	@WithMockUser(roles = { "USER" })
	@Test
	public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

			// arrange

			when(ucsbDiningCommonsMenuItemRepository.findById(eq(42L))).thenReturn(Optional.empty());

			// act
			MvcResult response = mockMvc.perform(get("/api/UCSBDiningCommonsMenuItem?id=42"))
							.andExpect(status().isNotFound()).andReturn();

			// assert

			verify(ucsbDiningCommonsMenuItemRepository, times(1)).findById(eq(42L));
			Map<String, Object> json = responseToJson(response);
			assertEquals("EntityNotFoundException", json.get("type"));
			assertEquals("UCSBDiningCommonsMenuItem with id 42 not found", json.get("message"));
	}

    
    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_can_get_all_UCSBDiningCommonsMenuItems() throws Exception {

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
            MvcResult response = mockMvc.perform(get("/api/UCSBDiningCommonsMenuItem/all"))
                            .andExpect(status().isOk()).andReturn();

            // assert

            verify(ucsbDiningCommonsMenuItemRepository, times(1)).findAll();
            String expectedJson = mapper.writeValueAsString(expectedMenuItems);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void an_admin_user_can_post_a_new_menuitem() throws Exception {
            // arrange

            UCSBDiningCommonsMenuItem meat_dish = UCSBDiningCommonsMenuItem.builder()
                            .diningCommonsCode("ortega")
                            .name("meat_dish")
                            .station("station1")
                            .build();

            when(ucsbDiningCommonsMenuItemRepository.save(eq(meat_dish))).thenReturn(meat_dish);

            // act
            MvcResult response = mockMvc.perform(
                            post("/api/UCSBDiningCommonsMenuItem/post?diningCommonsCode=ortega&name=meat_dish&station=station1")
                                            .with(csrf()))
                            .andExpect(status().isOk()).andReturn();

            // assert
            verify(ucsbDiningCommonsMenuItemRepository, times(1)).save(meat_dish);
            String expectedJson = mapper.writeValueAsString(meat_dish);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_can_delete_a_menuitem() throws Exception {
            // arrange

            UCSBDiningCommonsMenuItem meat_dish = UCSBDiningCommonsMenuItem.builder()
                            .diningCommonsCode("ortega")
                            .name("meat_dish")
                            .station("station1")
                            .build();

            when(ucsbDiningCommonsMenuItemRepository.findById(eq(1L))).thenReturn(Optional.of(meat_dish));

            // act
            MvcResult response = mockMvc.perform(
                            delete("/api/UCSBDiningCommonsMenuItem?id=1")
                                            .with(csrf()))
                            .andExpect(status().isOk()).andReturn();

            // assert
            verify(ucsbDiningCommonsMenuItemRepository, times(1)).findById(1L);
            verify(ucsbDiningCommonsMenuItemRepository, times(1)).delete(any());

            Map<String, Object> json = responseToJson(response);
            assertEquals("UCSBDiningCommonsMenuItem with id 1 deleted", json.get("message"));
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_tries_to_delete_non_existant_commons_and_gets_right_error_message()
                    throws Exception {
            // arrange

            when(ucsbDiningCommonsMenuItemRepository.findById(eq(42L))).thenReturn(Optional.empty());

            // act
            MvcResult response = mockMvc.perform(
                            delete("/api/UCSBDiningCommonsMenuItem?id=42")
                                            .with(csrf()))
                            .andExpect(status().isNotFound()).andReturn();

            // assert
            verify(ucsbDiningCommonsMenuItemRepository, times(1)).findById(42L);
            Map<String, Object> json = responseToJson(response);
            assertEquals("UCSBDiningCommonsMenuItem with id 42 not found", json.get("message"));
    }


    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_can_edit_an_existing_menuitem() throws Exception {
            // arrange

            UCSBDiningCommonsMenuItem meat_dish = UCSBDiningCommonsMenuItem.builder()
                            .diningCommonsCode("ortega")
                            .name("meat_dish")
                            .station("station1")
                            .build();

            UCSBDiningCommonsMenuItem meat_dish_edited = UCSBDiningCommonsMenuItem.builder()
                            .diningCommonsCode("Ortega Dining Hall")
                            .name("veg_dish")
                            .station("station42")
                            .build();

            String requestBody = mapper.writeValueAsString(meat_dish_edited);

            when(ucsbDiningCommonsMenuItemRepository.findById(eq(1L))).thenReturn(Optional.of(meat_dish));

            // act
            MvcResult response = mockMvc.perform(
                            put("/api/UCSBDiningCommonsMenuItem?id=1")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .characterEncoding("utf-8")
                                            .content(requestBody)
                                            .with(csrf()))
                            .andExpect(status().isOk()).andReturn();

            // assert
            verify(ucsbDiningCommonsMenuItemRepository, times(1)).findById(1L);
            verify(ucsbDiningCommonsMenuItemRepository, times(1)).save(meat_dish_edited); // should be saved with updated info
            String responseString = response.getResponse().getContentAsString();
            assertEquals(requestBody, responseString);
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_cannot_edit_menuitem_that_does_not_exist() throws Exception {
            // arrange

            UCSBDiningCommonsMenuItem meat_dish_edited = UCSBDiningCommonsMenuItem.builder()
                            .diningCommonsCode("Ortega Dining Hall")
                            .name("veg_dish")
                            .station("station42")
                            .build();

            String requestBody = mapper.writeValueAsString(meat_dish_edited);

            when(ucsbDiningCommonsMenuItemRepository.findById(eq(1L))).thenReturn(Optional.empty());

            // act
            MvcResult response = mockMvc.perform(
                            put("/api/UCSBDiningCommonsMenuItem?id=1")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .characterEncoding("utf-8")
                                            .content(requestBody)
                                            .with(csrf()))
                            .andExpect(status().isNotFound()).andReturn();

            // assert
            verify(ucsbDiningCommonsMenuItemRepository, times(1)).findById(1L);
            Map<String, Object> json = responseToJson(response);
            assertEquals("UCSBDiningCommonsMenuItem with id 1 not found", json.get("message"));

    }

}
