package com.old.silence.core.context.distributed;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.tuple.Pair;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import com.old.silence.core.test.data.RandomData;

/**
 * @author moryzang
 */
class DistributedContextHolderTest {
    public DistributedContextHolderTest() {
    }

    @Test
    void testGetCorrectDistributedEvent() {
        int poolSize = 10;
        @SuppressWarnings("unchecked")
        CompletableFuture<Pair<String, SampleEvent>>[] futures = new CompletableFuture[poolSize];

        for (int i = 0; i < poolSize; i++) {
            var future = CompletableFuture.supplyAsync(() -> {
                String code = RandomData.randomName("Code");
                SampleEvent event = createAndGetEventFromContextHolder(code);
                return Pair.of(code, event);
            });
            futures[i] = future;
        }

        CompletableFuture.allOf(futures).whenComplete((r, ex) -> {
            for (var future : futures) {
                Pair<String, SampleEvent> eventPair;
                try {
                    eventPair = future.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new UndeclaredThrowableException(e);
                }
                assertThat(eventPair.getLeft()).isEqualTo(eventPair.getRight().getEventCode());
            }
        });
    }

    private static SampleEvent createAndGetEventFromContextHolder(String eventCode) {
        var sampleEvent = new SampleEvent(eventCode);
        DistributedContextHolder.getContext().setDistributedEvent(sampleEvent);

        return (SampleEvent) DistributedContextHolder.getContext().getDistributedEvent();
    }
}
