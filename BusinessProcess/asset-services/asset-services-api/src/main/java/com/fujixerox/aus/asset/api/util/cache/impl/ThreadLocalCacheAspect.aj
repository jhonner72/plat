package com.fujixerox.aus.asset.api.util.cache.impl;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public aspect ThreadLocalCacheAspect {

    Object around(): execution(* *(..)) && @annotation(com.fujixerox.aus.asset.api.util.cache.ThreadLocalCache) {
        if (!ThreadLocalStorage.isActive()) {
            return proceed();
        }
        ExecutionKey executionKey = new ExecutionKey(thisJoinPoint);
        Result cached = (Result) ThreadLocalStorage.get(executionKey);
        if (cached != null) {
            return cached._result;
        }
        Object result = proceed();
        ThreadLocalStorage.put(executionKey, new Result(result));
        return result;
    }


    public class Result {

        private final Object _result;

        public Result(Object result) {
            _result = result;
        }

    }

}
