package sumdu.edu.ua.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sumdu.edu.ua.persistence.entity.CommentEntity;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    @Query("SELECT c FROM CommentEntity c JOIN FETCH c.book JOIN FETCH c.user WHERE c.user.id = :userId ORDER BY c.createdAt DESC")
    List<CommentEntity> findByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM CommentEntity c JOIN FETCH c.book JOIN FETCH c.user WHERE c.book.id = :bookId ORDER BY c.createdAt DESC")
    List<CommentEntity> findByBookId(@Param("bookId") Long bookId);
}
