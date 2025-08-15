package com.github.amanguss.shopping_list_application.controller;

import com.github.amanguss.shopping_list_application.controller.mvc.WebController;
import com.github.amanguss.shopping_list_application.dto.shoppingList.ShoppingListCreateDto;
import com.github.amanguss.shopping_list_application.dto.shoppingList.ShoppingListResponseDto;
import com.github.amanguss.shopping_list_application.dto.user.UserCreateDto;
import com.github.amanguss.shopping_list_application.dto.user.UserResponseDto;
import com.github.amanguss.shopping_list_application.dto.item.ItemCreateDto;
import com.github.amanguss.shopping_list_application.dto.item.ItemResponseDto;
import com.github.amanguss.shopping_list_application.dto.category.CategoryResponseDto;
import com.github.amanguss.shopping_list_application.entity.enums.ListStatus;
import com.github.amanguss.shopping_list_application.entity.enums.PriorityLevel;
import com.github.amanguss.shopping_list_application.entity.enums.AccountStatus;
import com.github.amanguss.shopping_list_application.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class WebControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private ShoppingListService shoppingListService;

    @Mock
    private ItemService itemService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ListShareService listShareService;

    @InjectMocks
    private WebController webController;

    @BeforeEach
    void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(webController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void testHomeRedirectsToDashboard() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    void testLoginPageDisplay() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    void testRegisterPageDisplay() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void testSuccessfulRegistration() throws Exception {
        when(userService.existsByEmail(anyString())).thenReturn(false);
        when(userService.createUser(any(UserCreateDto.class))).thenReturn(createMockUserResponse());

        mockMvc.perform(post("/register")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john@example.com")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("success"));

        verify(userService).createUser(any(UserCreateDto.class));
    }

    @Test
    void testRegistrationWithExistingEmail() throws Exception {
        when(userService.existsByEmail("existing@example.com")).thenReturn(true);

        mockMvc.perform(post("/register")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "existing@example.com")
                        .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"));

        verify(userService, never()).createUser(any(UserCreateDto.class));
    }

    @Test
    void testSuccessfulLogin() throws Exception {
        UserResponseDto user = createMockUserResponse();
        when(userService.getUserByEmail("john@example.com")).thenReturn(user);

        mockMvc.perform(post("/login")
                        .param("email", "john@example.com")
                        .param("password", "password123")
                        .sessionAttr("userId", 1)
                        .sessionAttr("userName", "John Doe"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    void testDashboardWithAuthentication() throws Exception {
        UserResponseDto user = createMockUserResponse();
        when(userService.getUserById(1)).thenReturn(user);
        when(shoppingListService.getShoppingListsByOwner(1)).thenReturn(Arrays.asList(createMockShoppingList()));
        when(listShareService.getSharesReceivedByUser(1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/dashboard")
                        .sessionAttr("userId", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/index"))
                .andExpect(model().attributeExists("user", "lists", "sharedLists", "totalLists", "totalShared"));
    }

    @Test
    void testDashboardWithoutAuthenticationRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void testListsPage() throws Exception {
        when(shoppingListService.getShoppingListsByOwner(1))
                .thenReturn(Arrays.asList(createMockShoppingList()));

        mockMvc.perform(get("/lists")
                        .sessionAttr("userId", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("lists/index"))
                .andExpect(model().attributeExists("lists"));
    }

    @Test
    void testCreateListForm() throws Exception {
        mockMvc.perform(get("/lists/create")
                        .sessionAttr("userId", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("lists/create"))
                .andExpect(model().attributeExists("list"));
    }

    @Test
    void testCreateListSubmission() throws Exception {
        ShoppingListResponseDto createdList = createMockShoppingList();
        when(shoppingListService.createShoppingList(any(ShoppingListCreateDto.class), eq(1)))
                .thenReturn(createdList);

        mockMvc.perform(post("/lists/create")
                        .sessionAttr("userId", 1)
                        .param("name", "Grocery List")
                        .param("description", "Weekly groceries")
                        .param("priority", "MEDIUM"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/lists/1"));

        verify(shoppingListService).createShoppingList(any(ShoppingListCreateDto.class), eq(1));
    }

    @Test
    void testViewList() throws Exception {
        ShoppingListResponseDto list = createMockShoppingList();
        when(shoppingListService.getShoppingListById(1)).thenReturn(list);
        when(itemService.getItemsByShoppingList(1)).thenReturn(Arrays.asList(createMockItem()));
        when(categoryService.getAllCategories()).thenReturn(Arrays.asList(createMockCategory()));

        mockMvc.perform(get("/lists/1")
                        .sessionAttr("userId", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("lists/view"))
                .andExpect(model().attributeExists("list", "items", "categories", "newItem",
                        "totalEstimated", "totalSpent", "completionPercent"));
    }

    @Test
    void testAddItemToList() throws Exception {
        ItemResponseDto item = createMockItem();
        when(itemService.createItem(eq(1), any(ItemCreateDto.class))).thenReturn(item);

        mockMvc.perform(post("/lists/1/items")
                        .sessionAttr("userId", 1)
                        .param("name", "Milk")
                        .param("quantity", "2")
                        .param("estimatedPrice", "3.99")
                        .param("categoryId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/lists/1"));

        verify(itemService).createItem(eq(1), any(ItemCreateDto.class));
    }

    @Test
    void testToggleItem() throws Exception {
        ItemResponseDto item = createMockItem();
        when(itemService.getItemById(1)).thenReturn(item);

        mockMvc.perform(post("/items/1/toggle")
                        .sessionAttr("userId", 1)
                        .param("actualPrice", "3.50"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/lists/1"));
    }

    @Test
    void testDeleteItem() throws Exception {
        ItemResponseDto item = createMockItem();
        when(itemService.getItemById(1)).thenReturn(item);

        mockMvc.perform(post("/items/1/delete")
                        .sessionAttr("userId", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/lists/1"));

        verify(itemService).deleteItem(1);
    }

    @Test
    void testProfilePage() throws Exception {
        UserResponseDto user = createMockUserResponse();
        when(userService.getUserById(1)).thenReturn(user);

        mockMvc.perform(get("/profile")
                        .sessionAttr("userId", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/index"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void testLogout() throws Exception {
        mockMvc.perform(get("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    private UserResponseDto createMockUserResponse() {
        return new UserResponseDto(
                1, "John", "Doe", "john@example.com", "1234567890",
                null, LocalDateTime.now(), LocalDateTime.now(),
                AccountStatus.ACTIVE, true, 5
        );
    }

    private ShoppingListResponseDto createMockShoppingList() {
        return ShoppingListResponseDto.builder()
                .id(1)
                .name("Grocery List")
                .description("Weekly groceries")
                .creationDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .status(ListStatus.ACTIVE)
                .isTemplate(false)
                .priority(PriorityLevel.MEDIUM)
                .ownerName("John Doe")
                .ownerId(10)
                .totalItems(3)
                .purchasedItems(0)
                .build();
    }

    private ItemResponseDto createMockItem() {
        return new ItemResponseDto(
                1, "Milk", "2% Milk", 2.0, "liters",
                3.99, 3.50, false, null, LocalDateTime.now(),
                PriorityLevel.MEDIUM, "Buy organic", "Dairy", 1, 1
        );
    }

    private CategoryResponseDto createMockCategory() {
        return new CategoryResponseDto(
                1, "Dairy", "Dairy products", "#FFD700",
                true, LocalDateTime.now(), 1, 5
        );
    }
}