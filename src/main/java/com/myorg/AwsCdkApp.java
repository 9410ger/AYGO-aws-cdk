package com.myorg;

import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.Environment;
import software.amazon.awscdk.core.StackProps;

public class AwsCdkApp {
    public static void main(final String[] args) {
        App app = new App();

        Environment env = makeEnv("972353547499","us-east-1");

        StackProps stackProps = StackProps.builder()
                .env(env).build();

        new AwsCdkStack(app,"AYGO-Stack",stackProps);

        app.synth();
    }

    static Environment makeEnv(String account, String region) {
        return Environment.builder()
                .account(account)
                .region(region)
                .build();
    }
}
