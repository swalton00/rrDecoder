package com.spw.rr.utilities

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Singleton
class BackgroundWorker {

    private static final Logger log = LoggerFactory.getLogger(BackgroundWorker.class)

    private final ExecutorService pool = Executors.newCachedThreadPool()

    void execute(Runnable task) {
        log.debug("about to run a task")
        pool.submit({

            log.debug("about to run the task")
            try {
                log.trace("in the try of runit task is ${task}")
                task
                log.trace("returned from the task")
            } catch (Exception e) {
                log.error("Exception in the new task", e)
            } finally {
                log.trace("runit finally block")
            }
        })
        log.trace("task submitted")
    }


}


