package com.example.reddiserver.repository;

import com.example.reddiserver.entity.Post;
import com.example.reddiserver.entity.PostTag;
import com.example.reddiserver.entity.enums.PostTagType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {

	PostTag findPostTagByPostAndPostTagTypeAndTag(Post post, PostTagType postTagType, String tag);

	List<PostTag> findAllByPost(Post post);
}
