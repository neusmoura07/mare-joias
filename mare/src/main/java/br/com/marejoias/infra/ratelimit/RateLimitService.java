package br.com.marejoias.infra.ratelimit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitService {

    static final long WINDOW_MILLIS = 60_000L;

    private final int defaultRequestsPerMinute;
    private final Map<String, Window> windows = new ConcurrentHashMap<>();
    private volatile int requestsPerMinute;

    public RateLimitService(@Value("${api.rate-limit.requests-per-minute}") int defaultRequestsPerMinute) {
        this.defaultRequestsPerMinute = defaultRequestsPerMinute;
        this.requestsPerMinute = defaultRequestsPerMinute;
    }

    public synchronized boolean tryAcquire(String ip) {
        long now = System.currentTimeMillis();
        removeExpiredWindows(now);

        Window window = windows.get(ip);
        if (window == null || now - window.startedAt >= WINDOW_MILLIS) {
            window = new Window(now);
            windows.put(ip, window);
        }

        if (window.count >= requestsPerMinute) {
            return false;
        }
        window.count++;
        return true;
    }

    public synchronized void setRequestsPerMinute(int requestsPerMinute) {
        this.requestsPerMinute = requestsPerMinute;
    }

    public synchronized void reset() {
        windows.clear();
        this.requestsPerMinute = defaultRequestsPerMinute;
    }

    private void removeExpiredWindows(long now) {
        Iterator<Map.Entry<String, Window>> iterator = windows.entrySet().iterator();
        while (iterator.hasNext()) {
            if (now - iterator.next().getValue().startedAt >= WINDOW_MILLIS) {
                iterator.remove();
            }
        }
    }

    private static final class Window {
        private final long startedAt;
        private int count;

        private Window(long startedAt) {
            this.startedAt = startedAt;
        }
    }
}
