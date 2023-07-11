package aspect;

import org.junit.jupiter.api.Test;

class CalculatorImplTest {
    @Test
    public void test(){
        CalculatorImpl calculator = new CalculatorImpl();
        calculator.add(3,2);
    }

}