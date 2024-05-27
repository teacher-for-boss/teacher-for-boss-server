package kr.co.teacherforboss.service.boardService;

import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.web.dto.BoardResponseDTO;

import java.util.List;

public interface BoardQueryService {
    BoardResponseDTO.GetPostDTO getPost(Long postId);
    List<Post> getPostList(int page, int size, String sort);
}
