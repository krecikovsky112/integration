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

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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

    @Test
    void shouldFindNoLikesIfRepositoryIsEmpty() {
        List<LikePost> likes = likePostRepository.findAll();
        assertThat(likes, hasSize(0));
    }

    @Test
    void shouldModifyUserInLikePost() {
        LikePost persistedLikePost = new LikePost();
        persistedLikePost.setUser(user);
        persistedLikePost.setPost(blogPost);
        entityManager.persistAndFlush(persistedLikePost);

        User userToUpdateWith = new User();
        userToUpdateWith.setAccountStatus(AccountStatus.CONFIRMED);
        String userMail = "newUser@mail.com";
        userToUpdateWith.setEmail(userMail);
        entityManager.persistAndFlush(userToUpdateWith);

        LikePost likePost = new LikePost();
        likePost.setId(persistedLikePost.getId());
        likePost.setPost(blogPost);
        likePost.setUser(userToUpdateWith);
        likePostRepository.save(likePost);

        entityManager.flush();

        entityManager.refresh(persistedLikePost);
        assertThat(persistedLikePost.getUser().getId(), equalTo(userToUpdateWith.getId()));
        assertThat(persistedLikePost.getUser().getEmail(), equalTo(userMail));
    }

    @Test
    void shouldFindLikePostByUserAndPost() {
        LikePost persistedLikePost = new LikePost();
        persistedLikePost.setUser(user);
        persistedLikePost.setPost(blogPost);
        entityManager.persistAndFlush(persistedLikePost);

        Optional<LikePost> optionalLikePost = likePostRepository.findByUserAndPost(user, blogPost);

        assertThat(optionalLikePost.isPresent(), equalTo(true));
        LikePost likePost = optionalLikePost.get();
        assertThat(likePost.getPost().getId(), equalTo(blogPost.getId()));
        assertThat(likePost.getUser().getId(), equalTo(user.getId()));
    }

}