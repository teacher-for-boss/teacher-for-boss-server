package kr.co.teacherforboss.repository.problemRepository;

import kr.co.teacherforboss.domain.Exam;
import kr.co.teacherforboss.domain.Problem;
import kr.co.teacherforboss.domain.enums.ExamType;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.ExamRepository;
import kr.co.teacherforboss.repository.ProblemRepository;
import kr.co.teacherforboss.util.ExamTestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;


@ExtendWith(MockitoExtension.class)
class ProblemRepositoryTest {

    @Mock
    private ExamRepository examRepository;
    @Mock
    private ProblemRepository problemRepository;
    @InjectMocks
    private ExamTestUtil examTestUtil;

    /*
    // TODO: 문제 조회 성능 테스트
     */
    @DisplayName("문제 조회 성능 비교 - count vs 연관관계 조회")
    @Test
    void countByExamIdAndStatus() {
        // given
        Exam exam = examTestUtil.generateExam(ExamType.MID);
        List<Problem> problemList = new ArrayList<>();
        LongStream.rangeClosed(1, 1000000)
                .forEach(i -> problemList.add(examTestUtil.generateProblem(exam, "문제" + i, i)));
        exam.setProblemList(problemList);

        examRepository.save(exam);
        problemRepository.saveAll(problemList);

        // when
        long start0 = System.currentTimeMillis();
        problemRepository.findAll();
        long end0 = System.currentTimeMillis();
        long elapsedTime0 = end0 - start0;

        long start1 = System.currentTimeMillis();
        if (examRepository.findByIdAndStatus(exam.getId(), Status.ACTIVE).isPresent())
            examRepository.findByIdAndStatus(exam.getId(), Status.ACTIVE).get().getProblemList(); // 시험 Id 조회 -> 시험의 problem list 조회
        long end1 = System.currentTimeMillis();
        long elapsedTime1 = end1 - start1;

        long start2 = System.currentTimeMillis();
        problemRepository.countByExamIdAndStatus(exam.getId(), Status.ACTIVE); // 시험 Id가 동일한 문제들 조회
        long end2 = System.currentTimeMillis();
        long elapsedTime2 = end2 - start2;

        // then
        System.out.println("Query execution time0: " + elapsedTime0 + " millisecond0");
        System.out.println("Query execution time1: " + elapsedTime1 + " milliseconds");
        System.out.println("Query execution time2: " + elapsedTime2 + " milliseconds");
    }
}