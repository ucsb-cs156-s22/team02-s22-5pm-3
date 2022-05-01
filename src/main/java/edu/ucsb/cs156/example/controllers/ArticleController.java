package edu.ucsb.cs156.example.controllers;

import java.time.LocalDateTime;

import edu.ucsb.cs156.example.entities.Article;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.ArticleRepository;
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

@Api(description = "Articles")
@RequestMapping("/api/Article")
@RestController
@Slf4j
public class ArticleController extends ApiController {

    @Autowired
    ArticleRepository articleRepository;

    @ApiOperation(value = "List all available articles")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<Article> allArticles() {
        Iterable<Article> articles = articleRepository.findAll();
        return articles;
    }

    @ApiOperation(value = "Get a single article")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public Article getById(
            @ApiParam("id of review to be viewed") @RequestParam Long id) {
            Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Article.class, id));

        return article;
    }

    @ApiOperation(value = "Create a Article")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public Article postArticle(
            @ApiParam("Title of the article") @RequestParam String title,
            @ApiParam("Url of the article") @RequestParam String url,
            @ApiParam("Explanation of the article") @RequestParam String explanation, 
            @ApiParam("String email (of person that submitted it") @RequestParam String email,  
            @ApiParam("Date the article was added YYYY-mm-ddTHH:MM:SS (Example: 2022-04-26T23:39:51)") @RequestParam LocalDateTime dateAdded
            )
    {
        Article article = new Article();
        article.setTitle(title); 
        article.setUrl(url);
        article.setExplanation(explanation);
        article.setEmail(email);
        article.setDateAdded(dateAdded);

        Article savedArticle = articleRepository.save(article);

        return savedArticle;
    }

    @ApiOperation(value = "Delete an article")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteArticle(
            @ApiParam("id") @RequestParam Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Article.class, id));

        articleRepository.delete(article);
        return genericMessage("Article with id %s deleted".formatted(id));
    }

    @ApiOperation(value = "Update a single article")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public Article updateArticle(
            @ApiParam("id of article to be updated") @RequestParam Long id,
            @RequestBody @Valid Article incoming) {

        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Article.class, id));

        article.setTitle(incoming.getTitle());
        article.setUrl(incoming.getUrl());
        article.setExplanation(incoming.getExplanation());
        article.setEmail(incoming.getEmail());
        article.setDateAdded(incoming.getDateAdded());

        articleRepository.save(article);

        return article;
    }

    
}
