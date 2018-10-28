This introduction will hopefully help the reader conceptually understand how to read and modify Java classes
with a simple Java agent, using the library ASM. 
#What is a Java agent?
To help answer this question, we'll first answer a more familiar question: What is a Java main class? <br> 
A Java main class is a Java class with a method named `main`, that has the modifiers `public` and `static`, returns 
`void`, and takes one parameter of type `String[]`. Also, a main class is written with the intent to be ran as a main 
class, either directly as `java MainClassName` on a command line, or packaged in a JAR (Java ARchive) whose manifest
specifies `Main-Class: MainClassName`. <br>
Similarly, A Java agent is a Java class that has a method of the form 
`public static void premain(String premainArgs, Instrumentation inst)` and/or a method 
`public static void agentmain(String agentmainArgs, Instrumentation inst)`. The former is to be called during the 
startup phase of the JVM, while the latter is to be called after the JVM has started. For this introduction, we will 
ignore the `agentmain` method. Unlike a Java main class, an agent _must_ be packaged in a JAR, where the manifest
specifies `Premain-Class: AgentClassName`. Then, to run the agent, we pass the argument 
`-javaagent:AgentJARfilename.jar` to `java`.

#ASM
According to their website, "ASM is an all purpose Java bytecode manipulation and analysis framework. It can be used to
 modify existing classes or to dynamically generate classes, directly in binary form. ASM provides some common bytecode
  transformations and analysis algorithms from which custom complex transformations and code analysis tools can be 
  built. ASM offers similar functionality as other Java bytecode frameworks, but is focused on performance.
  Because it was designed and implemented to be as small and as fast as possible, it is well suited for use in dynamic 
  systems (but can of course be used in a static way too, e.g. in compilers)."
  
#Important Classes and Interfaces
`Instrumentation`, `ClassFileTransformer`, `ClassReader`, `ClassVisitor`, `ClassWriter` (inherits from ClassVisitor), `MethodVisitor` and 
similar, `LocalVariablesSorter`, `AdviceAdapter`.