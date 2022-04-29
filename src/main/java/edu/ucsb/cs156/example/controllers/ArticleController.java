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
    
}
