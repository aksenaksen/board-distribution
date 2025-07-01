package mini.board.article.application;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PageLimitCalculatorTest {


    @Test
    void calculatePageLimitTest(){
//        1페이지에 30개 10페이지씩 = 300 + 1개가 있으면 이동가능
        calculatePageLimitTest(1L, 30L, 10L,301L);
        calculatePageLimitTest(7L, 30L, 10L,301L);
        calculatePageLimitTest(11L, 30L, 10L,601L);
        calculatePageLimitTest(12L, 30L, 10L,601L);
    }

    void calculatePageLimitTest(Long page, Long pageSize, Long movablePageCount, Long expected) {
        Long result = PageLimitCalculator.calculatePageLimit(page,pageSize,movablePageCount);
        Assertions.assertThat(result).isEqualTo(expected);
    }
}