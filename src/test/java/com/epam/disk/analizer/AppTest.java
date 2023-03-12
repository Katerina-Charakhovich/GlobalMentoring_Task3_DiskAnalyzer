package com.epam.disk.analizer;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

class AppTest {




   @Test
    void testDiskAnalizer()  {
       String[] args = { "-fs", "c:/project"};
       App app = new App();
       new CommandLine(new App())
               .setCaseInsensitiveEnumValuesAllowed(true)
               .execute(args);
    }
}
