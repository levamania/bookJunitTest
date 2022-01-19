package org.tgstudy.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tgstudy.book.domain.Book;
import org.tgstudy.book.domain.BookRepository;

import java.util.List;

// 기능을 정의 할 수 있고, 트랜잭션을 관리 할 수 있음
@Service
@RequiredArgsConstructor
public class BookService {
    // 함수 => 송금() -> Repostory에 여러개의 함수 실행 -> commit or rollback
    private final BookRepository bookRepository;


    @Transactional
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @Transactional(readOnly = true) //JPA 변경감지라는 내부 기능 활성화 X, update시 정합성을 유지해줌, insert의 유령데이터 (팬텀현상)을 못막음
    public Book findById(Long id) {
        return bookRepository.findById(id).orElseThrow( () -> new IllegalArgumentException("id를 확인해주세요"));
    }


    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Transactional(readOnly = false)
    public Book updateBookById(Long id, Book book) {
        // 더티체팅 update
        // 영속화 (book 오브젝트) -> findById로 가져옴 -> 영속성 컨텍스트 보관
        return bookRepository.findById(id).map( b -> {
            b.setAuthor(book.getAuthor());
            b.setTitle(book.getTitle());
            return b;
        }).orElseThrow( () -> new IllegalArgumentException("id를 확인해주세요"));
    } //종료시에 Transaction 종료 -> 영속화 되어 있는 데이터를 DB로 갱신(flush) => commit => 더티체킹

    @Transactional(readOnly = false)
    public String removeBook(Long id) {
        return bookRepository.findById(id).map(b -> {
            bookRepository.delete(b); //오류가 터지면 exception 발생
            return "ok";
        }).orElseThrow(() -> new IllegalArgumentException("id를 확인해주세요"));
    }
}
