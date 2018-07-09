Sample class showing how to embed Dirigible into an arbitrary Java application
---

1. Create a Java project
2. Create a folder "content"
3. Create sub-folder "project1" under the "content" folder
4. Create a file named "hello1.js" under the "project1" folder with the following content

```javascript
console.log('Hello World!');
```

5. Create a Java class named "MyApp" with the following content:

```java
import java.io.IOException;

import org.eclipse.dirigible.commons.api.context.ContextException;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.runtime.core.embed.EmbeddedDirigible;

public class MyApp {

	public static void main(String[] args) {
		// create a Dirigible instance
		EmbeddedDirigible dirigible = new EmbeddedDirigible();
		try {
			// initialize the Dirigible instance
			dirigible.initialize();
			// import the content under the specified folder to the Dirigible's registry
			dirigible.load("./content");
			// execute a given service module
			dirigible.executeJavaScript("project1/hello1.js");
			// or more generic dirigible.execute(dirigible.ENGINE_TYPE_JAVASCRIPT, "project1/hello1.js");
			// or richer dirigible.execute(dirigible.ENGINE_TYPE_JAVASCRIPT, "project1/hello1.js", context, request, response);
		} catch (IOException | ScriptingException | ContextException e) {
			e.printStackTrace();
		} finally {
			// destroy the Dirigible instance
			dirigible.destroy();
			System.exit(0);
		}
	}

}
```

6. Run it as a Java application
7. You have to be able to find the following log record in the system output

> [main] INFO org.eclipse.dirigible.api.v3.core.Console - Hello World!
