package edu.iis.mto.blog.domain.repository;

import edu.iis.mto.blog.domain.model.AccountStatus;
import edu.iis.mto.blog.domain.model.BlogPost;
import edu.iis.mto.blog.domain.model.LikePost;
import edu.iis.mto.blog.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
public class LikePostRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LikePostRepository likePostRepository;

    @Autowired
    private BlogPostRepository blogPostRepository;

    private User user;
    private BlogPost blogPost;
    private LikePost likePost;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setFirstName("Jan");
        user.setLastName("Kowalski");
        user.setEmail("john@domain.com");
        user.setAccountStatus(AccountStatus.NEW);

        blogPost = new BlogPost();
        blogPost.setEntry("Sample Entry");
        blogPost.setUser(user);

        entityManager.persist(user);
        entityManager.persist(blogPost);
        entityManager.flush();

        likePost = new LikePost();
        likePost.setUser(user);
        likePost.setPost(blogPost);
    }

    @Test
    void shouldSaveLikePost() {
        LikePost persistedLikePost = likePostRepository.save(likePost);

        assertThat(persistedLikePost.getId(), notNullValue());
    }

    @Test
    void shouldAddSavedLikePostToLikesInBlogPost() {
        LikePost persistedLikePost = likePostRepository.save(likePost);

        entityManager.refresh(blogPost);
        assertThat(blogPost.getLikesCount(), equalTo(1));
        assertThat(blogPost.getLikes().get(0), equalTo(persistedLikePost));
    }

}