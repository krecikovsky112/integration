package edu.iis.mto.blog.domain;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.iis.mto.blog.domain.model.BlogPost;
import edu.iis.mto.blog.domain.model.LikePost;
import edu.iis.mto.blog.domain.repository.BlogPostRepository;
import edu.iis.mto.blog.domain.repository.LikePostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import edu.iis.mto.blog.api.request.UserRequest;
import edu.iis.mto.blog.domain.model.AccountStatus;
import edu.iis.mto.blog.domain.model.User;
import edu.iis.mto.blog.domain.repository.UserRepository;
import edu.iis.mto.blog.services.BlogService;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class BlogManagerTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BlogPostRepository blogPostRepository;

    @MockBean
    private LikePostRepository likePostRepository;

    @Autowired
    private BlogService blogService;

    @Captor
    private ArgumentCaptor<User> userParam;

    @Captor
    private ArgumentCaptor<LikePost> likePost;

    @Test
    void creatingNewUserShouldSetAccountStatusToNEW() {
        blogService.createUser(new UserRequest("John", "Steward", "john@domain.com"));
        verify(userRepository).save(userParam.capture());
        User user = userParam.getValue();
        assertThat(user.getAccountStatus(), equalTo(AccountStatus.NEW));
    }

    @Test
    void addLikeToPostWhenUserHasStatusConfirmedShouldBeSuccessful() {
        Long modelValue = 0L;
        User user2 = new User();
        user2.setAccountStatus(AccountStatus.CONFIRMED);
        BlogPost blogPost = new BlogPost();
        User userWhoAddedPost = new User();
        userWhoAddedPost.setId(modelValue + 1);
        blogPost.setUser(userWhoAddedPost);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(blogPostRepository.findById(anyLong())).thenReturn(Optional.of(blogPost));
        when(likePostRepository.findByUserAndPost(any(), any())).thenReturn(Optional.empty());
        blogService.addLikeToPost(modelValue, modelValue);
        verify(likePostRepository).save(likePost.capture());
        assertThat(likePost.getValue().getPost(), equalTo(blogPost));
        assertThat(likePost.getValue().getUser(), equalTo(user2));
    }

}
