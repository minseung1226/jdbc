package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class MemberServiceV3_3 {

    private final MemberRepositoryV3 repository;


    public MemberServiceV3_3(MemberRepositoryV3 repository) {
        this.repository = repository;
    }
    @Transactional
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
                bizLogic(fromId, toId, money);
    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
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
