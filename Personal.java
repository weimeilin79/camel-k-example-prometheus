// camel-k: language=java

import org.apache.camel.builder.RouteBuilder;
import com.microsoft.azure.storage.StorageCredentials;
import com.microsoft.azure.storage.StorageCredentialsAccountAndKey;
import org.apache.camel.BindToRegistry;

public class Personal extends RouteBuilder {
  
  @BindToRegistry("creds")
    public StorageCredentials datasoure() {
        return new StorageCredentialsAccountAndKey("camelkmetrics","YOURKEY");
  }

  @Override
  public void configure() throws Exception {

    from("azure-blob://camelkmetrics/datastore/hr.json?credentials=#creds")
    .split().jsonpath("$.[*]")
      .log("${body}")
      .to("direct:eyecolor")
    ;


    from("direct:eyecolor")
    .setHeader("firstName").jsonpath("$.name.first")
    .setHeader("lastName").jsonpath("$.name.last")
    .setHeader("eyeColor").jsonpath("$.eyeColor")
    .log("${headers.firstName} ${headers.lastName} with eye color ${headers.eyeColor}")
    .split().jsonpath("$.friends[*]")
      .log("${body}")
      .to("direct:friends")
    ;

    from("direct:friends")
    .setHeader("friendName").jsonpath("$.name")
    .log("${headers.friendName}")
    ;
  }
}
