package com.back.motionit.domain.comments.comment.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.back.motionit.domain.comments.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	@Query("""
			select c
			from Comnment c
			where c.challengeRoom.id = :roomId
				and c.deleted = false
			order by c.createdDate desc
		""")
	Page<Comment> findActiveByRoomId(Long roomId, Pageable pageable);

	@EntityGraph(attributePaths = "user")
	@Query("""
			select C
			from Comment c
			where c.challengeRoom.id = :roomId
			  and c.deleted = false
			order by c.createDate desc 
		""")
	Page<Comment> findActiveByRoomIdWithAuthor(Long roomId, Pageable pageable);

	Optional<Comment> findByIdAndChallengeRoom_Id(Long commentId, Long RoomId);

	@EntityGraph(attributePaths = "user")
	Optional<Comment> findWithUserById(Long id);
}
