package kr.co.teacherforboss.service.boardService;

import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.PostLike;
import kr.co.teacherforboss.web.dto.BoardRequestDTO;

public interface BoardCommandService {
    Post savePost(BoardRequestDTO.SavePostDTO request);
    PostLike savePostLike(long postId);
}
