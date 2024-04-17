package com.example.azurefunctiontc;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.utility.MountableFile;

import java.nio.file.Paths;

@TestConfiguration
public class TestContainersConfiguration {

    @Bean
    public Network containerNetwork() {
        return Network.newNetwork();
    }

    @Bean
    public MSSQLServerContainer<?> mssqlServerContainer(Network containerNetwork) {
        MSSQLServerContainer<?> mssqlServerContainer = new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2019-latest")
                .withNetwork(containerNetwork)
                .withNetworkAliases("sqlserver");
        return mssqlServerContainer;
    }

    @Bean("function")
    public GenericContainer<?> functionContainer(MSSQLServerContainer<?> mssqlServerContainer, Network containerNetwork) {

        var jdbcUrl = mssqlServerContainer.getJdbcUrl().replaceAll("localhost", "sqlserver");

        var mappedPort = mssqlServerContainer.getMappedPort(1433);

        jdbcUrl = jdbcUrl.replaceAll(String.valueOf(mappedPort), "1433");
        var javaOpts = "-Dspring.profiles.active=componentTest"
                + " -Dspring.datasource.url=" + jdbcUrl
                + " -Dspring.datasource.username=" + mssqlServerContainer.getUsername()
                + " -Dspring.datasource.password=" + mssqlServerContainer.getPassword();

        MountableFile functionFiles = MountableFile.forHostPath(Paths.get("target/azure-functions/azure-function-tc"));


        GenericContainer<?> function =  new GenericContainer<>("mcr.microsoft.com/azure-functions/java:4-java21-appservice")
                .withCopyFileToContainer(functionFiles, "/home/site/wwwroot")
                .withEnv("AzureWebJobsScriptRoot", "/home/site/wwwroot")
                .withEnv("AzureFunctionsJobHost__Logging__Console__IsEnabled", "true")
                .withEnv("AZURE_FUNCTIONS_ENVIRONMENT", "Test")
                .withEnv("AzureWebJobsStorage", "UseDevelopmentStorage=true")
                .withEnv("FUNCTIONS_WORKER_RUNTIME", "java")
                .withEnv("MAIN_CLASS", "com.example.azurefunctiontc.AzureFunctionTcApplication")
                .withEnv("JAVA_OPTS", javaOpts)
                .waitingFor(new LogMessageWaitStrategy().withRegEx(".*Application started.*"))
                .withExposedPorts(80)
                .withNetwork(containerNetwork)
                .withNetworkAliases("function")
                .withReuse(true);
        return function;
    }
}
