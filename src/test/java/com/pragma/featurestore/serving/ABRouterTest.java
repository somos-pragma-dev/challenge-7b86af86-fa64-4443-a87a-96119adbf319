package com.pragma.featurestore.serving;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.resps.Tuple;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ABRouterTest {

    @Mock
    private JedisPool jedisPool;

    @Mock
    private Jedis jedis;

    private ABRouter router;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(jedisPool.getResource()).thenReturn(jedis);
        router = new ABRouter(jedisPool, 0.1);
    }

    @Test
    void testTrafficDistribution() {
        String userId = "test_user_123";
        String experimentId = "exp_recommendation_v2";

        when(jedis.hget(anyString(), anyString())).thenReturn(null);

        String variant = router.route(userId, experimentId);

        assertNotNull(variant, "Debe retornar una variante");
        assertTrue(variant.equals("control") || variant.equals("challenger"),
            "Variante debe ser control o challenger");
    }

    @Test
    void testStickySessionPersistence() {
        String userId = "sticky_user_456";
        String experimentId = "exp_ctr_optimization";
        String expectedVariant = "challenger";

        when(jedis.hget("ab:session:" + experimentId, userId)).thenReturn(expectedVariant);

        String variant = router.route(userId, experimentId);

        assertEquals(expectedVariant, variant, "Sticky session debe persistir la variante");
        verify(jedis, never()).hset(anyString(), anyString(), anyString());
    }

    @Test
    void testTrafficAllocationPercentage() {
        int totalUsers = 1000;
        int challengerCount = 0;
        String experimentId = "exp_allocation_test";

        for (int i = 0; i < totalUsers; i++) {
            String userId = "user_" + i;
            when(jedis.hget("ab:session:" + experimentId, userId)).thenReturn(null);

            String variant = router.route(userId, experimentId);
            if ("challenger".equals(variant)) {
                challengerCount++;
            }
        }

        double actualPercentage = (double) challengerCount / totalUsers;
        assertTrue(actualPercentage >= 0.05 && actualPercentage <= 0.15,
            "Distribución debe estar entre 5% y 15%, pero fue: " + actualPercentage);
    }

    @Test
    void testNewUserAssignment() {
        String newUserId = "new_user_789";
        String experimentId = "exp_new_user";

        when(jedis.hget(anyString(), anyString())).thenReturn(null);
        when(jedis.hset(anyString(), anyString(), anyString())).thenReturn(1L);

        String variant = router.route(newUserId, experimentId);

        verify(jedis).hset(eq("ab:session:" + experimentId), eq(newUserId), anyString());
        assertNotNull(variant);
    }

    @Test
    void testExperimentNotFound() {
        String userId = "user_default";
        String experimentId = "nonexistent_experiment";

        when(jedis.hget(anyString(), anyString())).thenReturn(null);

        String variant = router.route(userId, experimentId);

        assertEquals("control", variant, "Debe retornar control por defecto");
    }

    @Test
    void testRedisConnectionFailure() {
        String userId = "user_fail_test";
        String experimentId = "exp_failover";

        when(jedisPool.getResource()).thenThrow(new RuntimeException("Redis unavailable"));

        ABRouter fallbackRouter = new ABRouter(jedisPool, 0.1);
        String variant = fallbackRouter.route(userId, experimentId);

        assertEquals("control", variant, "Debe fallback a control cuando Redis falla");
    }

    @Test
    void testMultipleExperimentsIsolation() {
        String userId = "user_multi_exp";
        String exp1 = "exp_product_ranking";
        String exp2 = "exp_search_ranking";

        when(jedis.hget("ab:session:" + exp1, userId)).thenReturn("control");
        when(jedis.hget("ab:session:" + exp2, userId)).thenReturn("challenger");

        String variant1 = router.route(userId, exp1);
        String variant2 = router.route(userId, exp2);

        assertEquals("control", variant1);
        assertEquals("challenger", variant2);
        assertNotEquals(variant1, variant2, "Experimentos deben estar aislados");
    }

    @Test
    void testVariantMetricsTracking() {
        String userId = "user_metrics";
        String experimentId = "exp_metrics";

        when(jedis.hget(anyString(), anyString())).thenReturn(null);
        when(jedis.zadd(anyString(), anyDouble(), anyString())).thenReturn(1L);

        router.route(userId, experimentId);

        verify(jedis).zadd(eq("ab:metrics:" + experimentId + ":control"), anyDouble(), anyString());
    }
}