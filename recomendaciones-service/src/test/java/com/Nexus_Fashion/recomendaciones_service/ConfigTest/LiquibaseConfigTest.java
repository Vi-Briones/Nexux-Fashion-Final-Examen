package com.Nexus_Fashion.recomendaciones_service.ConfigTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.Nexus_Fashion.recomendaciones_service.config.LiquibaseConfig;

import liquibase.integration.spring.SpringLiquibase;

@ExtendWith(MockitoExtension.class)
class LiquibaseConfigTest {

    @InjectMocks
    private LiquibaseConfig liquibaseConfig;

    private DataSource dataSourceMock;

    @BeforeEach
    void setUp() {
        dataSourceMock = mock(DataSource.class);
    }

    @Test
    void testLiquibaseBeanSeCreaCorrectamente() {

        SpringLiquibase resultado = liquibaseConfig.liquibase(dataSourceMock);


        assertNotNull(resultado, "El bean SpringLiquibase no debería ser nulo");
        

    }
}
