package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.ArticleRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.controllers.ArticleController;
import edu.ucsb.cs156.example.entities.Article;
import edu.ucsb.cs156.example.repositories.ArticleRepository;

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

import java.time.LocalDateTime;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = ArticleController.class)
@Import(TestConfig.class)
public class ArticleControllerTests extends ControllerTestCase {
    
    @MockBean
    ArticleRepository articleRepository;

    // Authorization tests for /api/Article/admin/all

    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
            mockMvc.perform(get("/api/Article/all"))
                            .andExpect(status().is(403)); // logged out users can't get all
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_users_can_get_all() throws Exception {
            mockMvc.perform(get("/api/Article/all"))
                            .andExpect(status().is(200)); // logged
    }

    @Test
    public void logged_out_users_cannot_get_by_id() throws Exception {
            mockMvc.perform(get("/api/Article?id=7"))
                            .andExpect(status().is(403)); // logged out users can't get by id
    }

    // Authorization tests for /api/Article/post
    // (Perhaps should also have these for put and delete)

    @Test
    public void logged_out_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/Article/post"))
                            .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/Article/post"))
                            .andExpect(status().is(403)); // only admins can post
    }

    // // Tests with mocks for database actions

    @WithMockUser(roles = { "USER" })
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

        // arrange
        LocalDateTime ldt = LocalDateTime.parse("2022-01-03T00:00:00");



        Article article = Article.builder()
                        .title("Using testing-playground with React Testing Library")
                        .url("https://dev.to/katieraby/using-testing-playground-with-react-testing-library-26j7")
                        .explanation("Helpful when we get to front end development")
                        .email("phtcon@ucsb.edu")
                        .dateAdded(ldt)
                        .build();

        when(articleRepository.findById(eq(7L))).thenReturn(Optional.of(article));

        // act
        MvcResult response = mockMvc.perform(get("/api/Article?id=7"))
                        .andExpect(status().isOk()).andReturn();

        // assert

        verify(articleRepository, times(1)).findById(eq(7L));
        String expectedJson = mapper.writeValueAsString(article);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

            // arrange

            when(articleRepository.findById(eq(7L))).thenReturn(Optional.empty());

            // act
            MvcResult response = mockMvc.perform(get("/api/Article?id=7"))
                            .andExpect(status().isNotFound()).andReturn();

            // assert

            verify(articleRepository, times(1)).findById(eq(7L));
            Map<String, Object> json = responseToJson(response);
            assertEquals("EntityNotFoundException", json.get("type"));
            assertEquals("Article with id 7 not found", json.get("message"));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_can_get_all_articles() throws Exception {

            // arrange

            LocalDateTime ldt = LocalDateTime.parse("2022-01-05T00:00:00");


            Article article = Article.builder()
                            .title("test1")
                            .url("www.google.com")
                            .explanation("something test1")
                            .email("someone@gmail.com")
                            .dateAdded(ldt)
                            .build();

            LocalDateTime ldt2 = LocalDateTime.parse("2022-01-03T00:00:00");
            


            Article article2 = Article.builder()
                            .title("test2")
                            .url("www.bing.com")
                            .explanation("something test2")
                            .email("someone@hotmail.com")
                            .dateAdded(ldt2)
                            .build();

            ArrayList<Article> expectedArticle = new ArrayList<>();
            expectedArticle.addAll(Arrays.asList(article, article2));

            when(articleRepository.findAll()).thenReturn(expectedArticle);

            // act
            MvcResult response = mockMvc.perform(get("/api/Article/all"))
                            .andExpect(status().isOk()).andReturn();

            // assert

            verify(articleRepository, times(1)).findAll();
            String expectedJson = mapper.writeValueAsString(expectedArticle);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_article() throws Exception {
                // arrange

                LocalDateTime ldt = LocalDateTime.parse("2022-01-05T00:00:00");

                Article article = Article.builder()
                                .title("Testing article")
                                .url("www.facebook.com")
                                .explanation("Testing article")
                                .email("mark@zuckerberg.com")
                                .dateAdded(ldt)
                                .build();

                when(articleRepository.save(eq(article))).thenReturn(article);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/Article/post?title=Testingarticle&url=www.facebook.com&explanation=Testingarticle&email=mark@zuckerberg.com&dateAdded=2022-01-05T00:00:00")
                                            .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(articleRepository, times(1)).save(article);
                String expectedJson = mapper.writeValueAsString(article);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_can_delete_an_article() throws Exception {
            // arrange

            LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

            Article article = Article.builder()
                .title("Testing article1")
                .url("www.google.com")
                .explanation("Testing article1")
                .email("sundar@pichai.com")
                .dateAdded(ldt1)
                .build();

            when(articleRepository.findById(eq(15L))).thenReturn(Optional.of(article));

            // act
            MvcResult response = mockMvc.perform(
                delete("/api/Article?id=15")
                    .with(csrf()))
                .andExpect(status().isOk()).andReturn();

            // assert
            verify(articleRepository, times(1)).findById(15L);
            verify(articleRepository, times(1)).delete(any());

            Map<String, Object> json = responseToJson(response);
            assertEquals("Article with id 15 deleted", json.get("message"));
    }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_tries_to_delete_non_existant_article_and_gets_right_error_message()
                        throws Exception {
            // arrange

            when(articleRepository.findById(eq(15L))).thenReturn(Optional.empty());

            // act
            MvcResult response = mockMvc.perform(
                            delete("/api/Article?id=15")
                                            .with(csrf()))
                            .andExpect(status().isNotFound()).andReturn();

            // assert
            verify(articleRepository, times(1)).findById(15L);
            Map<String, Object> json = responseToJson(response);
            assertEquals("Article with id 15 not found", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_edit_an_existing_article() throws Exception {
                // arrange

                
                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-05T00:00:00");

                Article article = Article.builder()
                            .title("Testing article1")
                            .url("www.google.com")
                            .explanation("Testing article1")
                            .email("sundar@pichai.com")
                            .dateAdded(ldt1)
                            .build();

                LocalDateTime ldt2 = LocalDateTime.parse("2022-02-03T00:00:00");

                Article articleEdited = Article.builder()
                            .title("Testing article2")
                            .url("www.google.com/helloson")
                            .explanation("Ain't no way")
                            .email("Aashay@yahoo.com")
                            .dateAdded(ldt2)
                            .build();

                String requestBody = mapper.writeValueAsString(articleEdited);

                when(articleRepository.findById(eq(67L))).thenReturn(Optional.of(article));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/Article?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(articleRepository, times(1)).findById(67L);
                verify(articleRepository, times(1)).save(articleEdited); // should be saved with correct user
                String responseString = response.getResponse().getContentAsString();
                assertEquals(requestBody, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_cannot_edit_article_that_does_not_exist() throws Exception {
                // arrange

                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                Article article = Article.builder()
                            .title("Testing article")
                            .url("www.google.edu")
                            .explanation("last one")
                            .email("Aashay@yahoo.yuh")
                            .dateAdded(ldt1)
                            .build();

                String requestBody = mapper.writeValueAsString(article);

                when(articleRepository.findById(eq(67L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/Article?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(articleRepository, times(1)).findById(67L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("Article with id 67 not found", json.get("message"));

        }




}
