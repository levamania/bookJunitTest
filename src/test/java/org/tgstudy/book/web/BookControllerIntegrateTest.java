package org.tgstudy.book.web;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;
import org.tgstudy.book.domain.Book;
import org.tgstudy.book.domain.BookRepository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 통합 테스트 ( 모든 Bean들을 똑같이 IoC 올리고 테스트 하는것 )
 * WebEnvironment.MOCK => 실제 톰켓을 올리는게 아니라, 다른톰켓으로 테스트
 * WebEnvironment.RANDOM_PORT => 실제 톰켓ㅇ느로 테스트
 * @AutoConfigureMockMvc MockMvc를 IoC에 등록해줌
 * @Transactional은 각 각의 테스트 함수가 종료될 때마다 트랜잭션을 rollback 해주는 어노테이션
 */

@Slf4j
@Transactional
@AutoConfigureMockMvc
@SpringBootTest (webEnvironment = WebEnvironment.MOCK)
public class BookControllerIntegrateTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    public void setUp () {
        entityManager.createNativeQuery("ALTER TABLE book ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }

    // BDDMockito 패턴
    @Test
    public void save_테스트() throws Exception {
        // given ( 테스트를 하기 위한 준비)
        Book book = new Book(null, "스프링 따라하기", "코스");
        String content = new ObjectMapper().writeValueAsString(book);


        // when (테스트 실행)
        // 실제로 bookService 의 save 실행
        ResultActions resultActions = mockMvc.perform(post("/book")
                .contentType(MediaType.APPLICATION_JSON_UTF8) // 던지는 데이터 타입
                .content(content) // 실제로 던질 데이터
                .accept(MediaType.APPLICATION_JSON_UTF8) // 응답은?
        );
        // then (기대값)
        // resultActions 값 검증
        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("스프링 따라하기"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void findAll_테스트() throws Exception {
        //given
        List<Book> books = new ArrayList<>();
        books.add(new Book(null , "스프링 따라하기", "코스"));
        books.add(new Book(null , "리엑트 따라하기", "코스"));
        books.add(new Book(null , "JUnit 따라하기", "코스"));
//        when(bookService.findAll()).thenReturn(books);

        // 실제로  save
        bookRepository.saveAll(books);


        // when
        ResultActions resultActions = mockMvc.perform(get("/book")
                .accept(MediaType.APPLICATION_JSON_UTF8));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(3)))
                .andExpect(jsonPath("$.[2].title").value("JUnit 따라하기"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void findById_테스트() throws Exception {
        // given
        Long id = 3L;
        // stub
        List<Book> books = new ArrayList<>();
        books.add(new Book(null , "스프링 따라하기", "코스"));
        books.add(new Book(null , "리엑트 따라하기", "코스"));
        books.add(new Book(null , "자바 공부하기", "코스"));
        bookRepository.saveAll(books);

        // when
        ResultActions resultActions = mockMvc.perform(get("/book/{id}", id)
                .accept(MediaType.APPLICATION_JSON_UTF8));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("자바 공부하기"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void update_테스트() throws Exception {
        // given
        List<Book> books = new ArrayList<>();
        books.add(new Book(null , "스프링 따라하기", "코스"));
        books.add(new Book(null , "리엑트 따라하기", "코스"));
        books.add(new Book(null , "자바 공부하기", "코스"));
        bookRepository.saveAll(books);


        Long id = 2L;
        Book book = new Book(null, "C++ 따라하기", "코스");

        String content = new ObjectMapper().writeValueAsString(book);
//        when(bookService.updateBookById(id,book)).thenReturn(new Book(id,"C++ 따라하기", "코스" ));


        ResultActions resultActions = mockMvc.perform(put("/book/{id}", id)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(content) // 실제로 던질 데이터
                .accept(MediaType.APPLICATION_JSON_UTF8));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("C++ 따라하기"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void delete_테스트() throws Exception {
        // given
        List<Book> books = new ArrayList<>();
        books.add(new Book(null , "스프링 따라하기", "코스"));
        books.add(new Book(null , "리엑트 따라하기", "코스"));
        books.add(new Book(null , "자바 공부하기", "코스"));
        bookRepository.saveAll(books);

        // given
        Long id = 1L;
//        when(bookService.removeBook(id)).thenReturn("ok");

        // when
        ResultActions resultActions = mockMvc.perform(delete("/book/{id}", id)
                .accept(MediaType.TEXT_PLAIN));

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        MvcResult requestResult = resultActions.andReturn();
        String result = requestResult.getResponse().getContentAsString();

        assertEquals("ok", result);

        resultActions = mockMvc.perform(get("/book")
                .accept(MediaType.APPLICATION_JSON_UTF8));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andDo(MockMvcResultHandlers.print());
    }

}
