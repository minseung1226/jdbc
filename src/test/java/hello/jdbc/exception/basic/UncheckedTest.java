package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class UncheckedTest {

    @Test
    void unchecked_catch(){
        Service service = new Service();
        service.callCatch();
    }
    @Test
    void unchecked_throw(){
        Service service = new Service();

        Assertions.assertThatThrownBy(()->service.callThrow()).isInstanceOf(MyUncheckedException.class);
    }


    /**
     * RuntimeException 을 상속받은 Exception은 언체크예외가 된다.
     */
    static class MyUncheckedException extends RuntimeException{
        public MyUncheckedException(String message) {
            super(message);
        }
    }

    /**
     * UncheckedException은 예외를 던지거나 잡지 않아도 된다.
     * Exception을 잡지 않으면 자동으로 던지게 된다.
     */
    static class Service{
        Repository repository=new Repository();

        /**
         * 필요한 경우 잡아서 처리한다.
         */
        public void callCatch(){
            try{
                repository.call();
            }catch(MyUncheckedException e){
                log.info("예외처리,message={}",e.getMessage(),e);
            }
        }

        /**
         * checkedException과 다르게 던지는 구문을 작성하지 않아도 된다.
         * 자동으로 던져진다.
         */
        public void callThrow(){
            repository.call();
        }
    }

    static class Repository{
        public void call(){
            throw new MyUncheckedException("ex");
        }
    }
}
