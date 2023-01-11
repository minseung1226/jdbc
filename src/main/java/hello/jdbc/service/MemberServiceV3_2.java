package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class MemberServiceV3_2 {

    private final MemberRepositoryV3 repository;
    private final TransactionTemplate txTemplate;
    //private final PlatformTransactionManager transactionManager;


    public MemberServiceV3_2(MemberRepositoryV3 repository, PlatformTransactionManager transactionManager) {
        this.repository = repository;
        this.txTemplate = new TransactionTemplate(transactionManager);
    }

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        txTemplate.executeWithoutResult((status) ->{
            try {
                bizLogic(fromId, toId, money);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });



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
