package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepository;
import hello.jdbc.repository.MemberRepositoryV3;
import hello.jdbc.repository.MemberRepositoryV4_1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
/**
 * 예외 누수 문제 해결
 * SQLException 제거
 *
 * MemberRepository 인터페이스 의존
 */
@Slf4j
public class MemberServiceV4 {

    private final MemberRepository repository;


    public MemberServiceV4(MemberRepository repository) {
        this.repository = repository;
    }
    @Transactional
    public void accountTransfer(String fromId, String toId, int money){
                bizLogic(fromId, toId, money);
    }

    private void bizLogic(String fromId, String toId, int money){
        Member fromMember = repository.findById( fromId);
        Member toMember = repository.findById( toId);

        repository.update(fromMember.getMemberId(), fromMember.getMoney()- money);
        validation(toMember);

        repository.update(toMember.getMemberId(), toMember.getMoney()+ money);
    }
    private void validation(Member toMember) {
        if(toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
