# Java Spring Cloud Function With AWS Lambda Basic
Please check out my previous __[tutorial](https://github.com/junbetterway/spring-cloud-func-basic)__ which provides a basic introduction on using __[Spring Cloud Function](https://spring.io/projects/spring-cloud-function)__. 

This time, we will integrate Spring Cloud Function with __[AWS Lambda](https://aws.amazon.com/lambda/)__ which is a serverless compute service that lets you run code without provisioning or managing servers, creating workload-aware cluster scaling logic, maintaining event integrations, or managing runtimes. 

You can check this __[reference](https://docs.spring.io/spring-cloud-function/docs/3.1.1/reference/html/spring-cloud-function.html#_aws_lambda)__ for the complete guide on how to setup __AWS Lambda__ with __Spring Cloud Functions__.

*__Note:__ We will use the __traditional bean definitions__ instead of the __functional bean style__ for this tutorial.*

# Getting Started
The main goal of __Spring Cloud Function__ is to make your application code to be cloud-agnostic. Our entry-point __[SpringcloudfuncApplication](https://github.com/junbetterway/spring-cloud-func-aws-lambda-basic/blob/main/src/main/java/com/junbetterway/serverless/springcloudfunc/SpringcloudfuncApplication.java)__.class

```
@SpringBootApplication
public class SpringcloudfuncApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringcloudfuncApplication.class, args); 
	}
	
	@Bean
	public CreateAccount createAccount() {
		return new CreateAccount();
	}

	@Bean
	public ReadAccount readAccount() {
		return new ReadAccount();
	}
	
}
```

The interesting part of the code above is that we do not see any AWS-related configuration to setup our application to AWS Lambda. In fact, we just have to add the needed dependencies and deploy our package!

# Add Needed Dependencies
In order to run __Spring Cloud Function__ applications on __AWS Lambda__, we need to add below dependency.

```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-function-adapter-aws</artifactId>
</dependency>
```

# Build The AWS Deployable JAR
We need a shaded JAR in order to upload our code to AWS Lambda. A shaded artifact means it has all the dependencies exploded out as individual class files instead of jars. One can use the __[Maven Shade Plugin](https://maven.apache.org/plugins/maven-shade-plugin/)__ for such.

```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <configuration>
        <createDependencyReducedPom>false</createDependencyReducedPom>
        <shadedArtifactAttached>true</shadedArtifactAttached>
        <shadedClassifierName>aws</shadedClassifierName>
    </configuration>
</plugin>
```

In addition, we can use *spring-boot-thin-layout* dependency to help us reduce the size of the artifact by excluding some dependencies that are not needed:

```
<dependency>
    <groupId>org.springframework.boot.experimental</groupId>
    <artifactId>spring-boot-thin-layout</artifactId>
    <version>${spring-boot-thin-layout.version}</version>
</dependency>
```

Please check our __[pom.xml](https://github.com/junbetterway/spring-cloud-func-aws-lambda-basic/blob/main/pom.xml)__ for the complete configuration to achieve the deployable JAR to AWS Lambda.

Now, one can just run below command to generate the AWS deployable JAR:

```
./mvnw clean package
```

There will be three JARs under the __target__ folder. Select the one with the name __springcloudfunc-0.0.1-SNAPSHOT-aws.jar__. Take note of this file as we will need this in the succeeding steps involving AWS Lambda creation.

# Configure AWS Lambda
1. Go to __[AWS Lambda](https://aws.amazon.com/lambda/)__ then create a function by providing a unique name (__e.g.,__ MyCreateAccountFunc) and runtime environment (__e.g.,__ Java 11)
2. Once successfully created, let's setup the configuration by uploading the AWS deployable JAR (__springcloudfunc-0.0.1-SNAPSHOT-aws.jar__) under the __Function Code__ section
3. Update the *Handler* field under __Runtime settings__ section and paste below default handler provided by Spring Cloud Function

```
org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest
```

4. Let's edit the __Environment variables__ and add a new entry __SPRING_CLOUD_FUNCTION_DEFINITION__ with the value based on your bean method name. Take note that AWS Lambda can process __ONLY ONE__ function at a time hence, if your code has more than one bean functions then create a new AWS lambda for that. For our case, we have two traditional bean definitions thus, we select only one. For this step, we select the __createAccount__ bean which will result to:

```
SPRING_CLOUD_FUNCTION_DEFINITION=createAccount
```

# Create Test In AWS Lambda
Let's now test the AWS Lambda function by creating a test event and supplying a sample json body.
1. Select your newly created function then click __Test__ or select from the dropdown beside it __Configure test events__
2. Continue with the default "hello-world" template and fill in the needed items. See sample json body below:

```
{
    "name": "Jun King Minon",
    "balance": 12000
}
```
3. Now make sure that the newly created test event is selected on the *Saved Test Events* dropdown then click __Test__.

This will trigger the AWS Lambda Function you have just created. It will display a loading icon during the cold starts (if initial trigget it may take some time). One should be notified with __Execution result: succeeded__ message and can click __Details__ to see something similar to below logs:

```
START RequestId: 14d9a92f-9730-452e-a2b1-341b3dd6a951 Version: $LATEST
2021-03-01 03:48:50.531  INFO 8 --- [           main] o.s.c.f.a.aws.CustomRuntimeEventLoop     : Located function createAccount
2021-03-01 03:48:50.531  INFO 8 --- [           main] o.s.c.f.adapter.aws.AWSLambdaUtils       : Incoming JSON Event: {"name":"Jun King Minon","balance":12000}
2021-03-01 03:48:50.549  INFO 8 --- [           main] o.s.c.f.a.aws.CustomRuntimeEventLoop     : Result POST status: 202 ACCEPTED
END RequestId: 14d9a92f-9730-452e-a2b1-341b3dd6a951
REPORT RequestId: 14d9a92f-9730-452e-a2b1-341b3dd6a951	Duration: 24.61 ms	Billed Duration: 25 ms	Memory Size: 512 MB	Max Memory Used: 203 MB	
```

# Trigger AWS Lambda Using API Gateway
In reality, we need to expose an API endpoint to be invoked by users (or customers) in order to trigger our AWS Lambda function.
1. Select your newly created AWS Lambda function
2. Under __Designer__ section, click __Add trigger__ and select __API Gateway__
3. On the *API* dropdown, select the __Create an API__ option.
4. Select __REST API__ type 
5. Select __Open__ security mechanism for now. One would want to use *JWT authorizer* but this is out of scope for this basic tutorial.
6. Click __Add__ button

API Gateway should now be added as part of your triggers under *Layers* of __Designer__ section. Click this API Gateway and expand details. Take note of the API endpoint which could be something like this: 

```
https://ovspn81pl0.execute-api.ap-southeast-1.amazonaws.com/default/MyCreateAccountFunc
```

# Test AWS Lambda Function
Now one can invoke the API Gateway endpoint above in any API tool such as Postman or cURL. Using cURL, we can call:
1. Create Account function by:

```
curl -H "Content-type: application/json" -X POST -d '{"name":"Jun King Minon", "balance": 12000}' https://ovspn81pl0.execute-api.ap-southeast-1.amazonaws.com/default/MyCreateAccountFunc
```

2. Read Account using function by:

```
curl https://8jy9vksw5l.execute-api.ap-southeast-1.amazonaws.com/default/MyReadAccountFunc
```

*__Note:__ Repeat the above steps when creating a new AWS Lambda function for the __readAccount__ bean function definition.*

## Powered By
Contact me at [junbetterway](mailto:jkpminon12@yahoo.com)

Happy coding!!!
