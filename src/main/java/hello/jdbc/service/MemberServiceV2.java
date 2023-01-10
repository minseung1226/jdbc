package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RequiredArgsConstructor
@Slf4j
public class MemberServiceV2 {

    private final MemberRepositoryV2 repository;
    private final DataSource dataSource;
    public void accountTransfer(String fromId,String toId,int money) throws SQLException {
        Connection connection = dataSource.getConnection();
        try{
            connection.setAutoCommit(false);
            bizLogic(fromId, toId, money, connection);
            connection.commit();
        }catch (Exception e){
            connection.rollback();
            throw  e;
        }finally {
            release(connection);
        }

    }

    private void bizLogic(String fromId, String toId, int money, Connection connection) throws SQLException {
        Member fromMember = repository.findById(connection, fromId);
        Member toMember = repository.findById(connection, toId);

        repository.update(connection,fromMember.getMemberId(), fromMember.getMoney()- money);
        validation(toMember);

        repository.update(connection,toMember.getMemberId(), toMember.getMoney()+ money);
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
