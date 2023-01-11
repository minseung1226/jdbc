package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


@Slf4j
public class MemberServiceV3_1 {

    private final MemberRepositoryV3 repository;
    private final PlatformTransactionManager transactionManager;

    public MemberServiceV3_1(MemberRepositoryV3 repository, PlatformTransactionManager transactionManager) {
        this.repository = repository;
        this.transactionManager = transactionManager;
    }

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try{
            bizLogic(fromId, toId, money);
            transactionManager.commit(status);
        }catch (Exception e){
            transactionManager.rollback(status);
            throw  e;
        }

    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = repository.findById( fromId);
        Member toMember = repository.findById( toId);

        repository.update(fromMember.getMemberId(), fromMember.getMoney()- money);
        validation(toMember);

        repository.update(toMember.getMemberId(), toMember.getMoney()+ money);
    }

    private void release(Connection connection) {
        if(connection !=null){
            try{
                connection.setAutoCommit(true);
                connection.close();
            }catch (Exception e){
                log.error("error",e);
            }
        }
    }

    private void validation(Member toMember) {
        if(toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
