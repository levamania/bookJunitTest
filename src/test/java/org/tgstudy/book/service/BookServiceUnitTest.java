package org.tgstudy.book.service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tgstudy.book.domain.Book;
import org.tgstudy.book.domain.BookRepository;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 단위테스트 ( Service와 관련된 애들만 메모리에 뛰우면 됨. )
 * BookRepository => 가짜 객체로 만들 수 있음.
 *
 */

@ExtendWith(MockitoExtension.class)
public class BookServiceUnitTest {

    // Mockito 환경에서 개발적으로 등록
    // BookService 안에 BookRepository 있음 -> 등록해야함 -> InjectMocks @Mock으로 등록된 모든 것들을 주입 받는다.
    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @Test
    public void save_테스트() {
        // given
        Book book = new Book();
        book.setTitle("책제목1");
        book.setAuthor("책저자1");

        // stub
        when(bookRepository.save(book)).thenReturn(book);

        // test execute
        Book bookEntity = bookService.save(book);

        // then
        assertEquals(bookEntity, book);
    }
}
