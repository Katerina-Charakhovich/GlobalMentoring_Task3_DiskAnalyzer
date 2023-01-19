package com.epam.disk.analizer;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

class AppTest {




   @Test
    void testDiskAnalizer()  {
       String[] args = { "-s","s","-cfsc","-plf", "5", "-as", "c:/1/song-service"};
       App app = new App();
       new CommandLine(new App())
               .setCaseInsensitiveEnumValuesAllowed(true)
               .execute(args);
    }
}
