package testexample;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JUnitCycleQuiz {

    @BeforeEach
    void hello() {
        System.out.println("Hello!");
    }

    @AfterAll
    static void bye() {
        System.out.println("Bye!");
    }

    @Test
    void junitQuiz3() {
        System.out.println("This is first test");
    }

    @Test
    void junitQuiz4() {
        System.out.println("This is second test");
    }


}
