package org.tgstudy.book.web;

// 단위 테스트 (Controller 관련 로직만 뛰우기) Filter, Controller, Advice

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.tgstudy.book.domain.Book;
import org.tgstudy.book.service.BookService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@WebMvcTest // -> Controller, Filter, Advice 메모리에 load
public class BookControllerUniTest {

    @Autowired
    private MockMvc mockMvc;


    //@Mock //Mockito 환경에서 가짜로 메모리에 생성? -> 스프링 환경에서 생성 x -> mockito 환경에서 생성하는거
    @MockBean // IoC환경에 bean으로 등록 -> Controller 생성시 가짜 BookService 등록
    private BookService bookService;

    // BDDMockito 패턴
    @Test
    public void save_테스트() throws Exception {
        // given ( 테스트를 하기 위한 준비)
        Book book = new Book(null, "스프링 따라하기", "코스");
        String content = new ObjectMapper().writeValueAsString(book);

        // 스텁 -> 가상의 결과
        // bookService에 save가 실행한다면
        when(bookService.save(book)).thenReturn(new Book(1L,"스프링 따라하기", "코스" ));

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
        books.add(new Book(1L , "스프링 따라하기", "코스"));
        books.add(new Book(2L , "리엑트 따라하기", "코스"));
        when(bookService.findAll()).thenReturn(books);

        // when
        ResultActions resultActions = mockMvc.perform(get("/book")
                .accept(MediaType.APPLICATION_JSON_UTF8));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.[0].title").value("스프링 따라하기"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void findById_테스트() throws Exception {
        // given
        Long id = 1L;
        // stub
        Book book = new Book(id, "자바 공부하기", "쌀");
        when(bookService.findById(1L)).thenReturn(book);

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
        Long id = 1L;
        Book book = new Book(null, "C++ 따라하기", "코스");
        String content = new ObjectMapper().writeValueAsString(book);
        when(bookService.updateBookById(id,book)).thenReturn(new Book(id,"C++ 따라하기", "코스" ));


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
        Long id = 1L;
        when(bookService.removeBook(id)).thenReturn("ok");

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
    }


}
