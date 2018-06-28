package com.syb.datacloud.controller.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Admin
 */
@RestController
@RequestMapping("logger")
public class LoggerController {


    private static Logger logger = LoggerFactory.getLogger(LoggerController.class);

    @RequestMapping("*")
    public String logger(String msg){
        logger.trace("debug-->{}",msg);
        logger.debug("debug-->{}",msg);
        logger.info("debug-->{}",msg);
        logger.warn("debug-->{}",msg);
        logger.error("debug-->{}",msg);
        return "logger";
    }

}
