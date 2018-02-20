package de.vorb.wildfly_springboot;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleResource {

    @RequestMapping("/")
    public String hello() {
        return "response at Request '/'";
    }
    
    @RequestMapping("/rest")
    public String rest() {
        return "response at Request '/rest'";
    }
}
