package org.ricky;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ricky.common.password.IPasswordEncoder;
import org.ricky.core.category.domain.CategoryRepository;
import org.ricky.core.comment.domain.CommentRepository;
import org.ricky.core.commenthierarchy.domain.CommentHierarchyRepository;
import org.ricky.core.common.domain.event.DomainEventDao;
import org.ricky.core.doc.domain.DocRepository;
import org.ricky.core.tag.domain.TagRepository;
import org.ricky.core.user.domain.UserRepository;
import org.ricky.util.SetUpApi;
import org.ricky.util.TearDownApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/19
 * @className BaseApiTest
 * @desc
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AllDocsApplication.class)
public class BaseApiTest {

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected SetUpApi setUpApi;

    @Autowired
    protected TearDownApi tearDownApi;

    @Autowired
    protected DomainEventDao domainEventDao;

    @Autowired
    protected IPasswordEncoder passwordEncoder;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected DocRepository docRepository;

    @Autowired
    protected TagRepository tagRepository;

    @Autowired
    protected CategoryRepository categoryRepository;

    @Autowired
    protected CommentRepository commentRepository;

    @Autowired
    protected CommentHierarchyRepository commentHierarchyRepository;

    @BeforeEach
    protected void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

}
